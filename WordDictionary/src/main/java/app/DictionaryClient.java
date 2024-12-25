package app;

import app.service.SearchService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import static common.DictionaryConstants.DICTIONARY_FILE_PATH;

public class DictionaryClient {

    boolean useIndex = true;
    public static void main(String[] args) throws IOException {
        File dictionaryFile = new File(DICTIONARY_FILE_PATH);

        DictionaryClient client = new DictionaryClient();


        Map<String, SearchMetadata> indexMetadataMap = prepareIndex(client, dictionaryFile);

        SearchService searchService = new SearchService();

        String inputSearchWord = "zzzow"; //client.readInputForSearchFromUser(indexMetadataMap);

        if (client.useIndex) {
            if (!indexMetadataMap.containsKey(inputSearchWord)) {
                System.out.println("Unknown Word:" + inputSearchWord);
            }
            SearchMetadata metadata = indexMetadataMap.get(inputSearchWord);
            searchService.searchForMeaningFromWord(metadata);
        } else {
            searchService.searchWithoutIndex(inputSearchWord);
        }
    }

    private static Map<String, SearchMetadata> prepareIndex(DictionaryClient client, File dictionaryFile) throws
                                                                                                            FileNotFoundException {
        Map<String, SearchMetadata> indexMetadataMap = new LinkedHashMap<>();

        client.preExtractByteOffsets(dictionaryFile, indexMetadataMap);
        return indexMetadataMap;
    }

    private String readInputForSearchFromUser(Map<String, SearchMetadata> indexMetadataMap) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter word to find meaning:");
        String inputSearchWord = scanner.nextLine();

        scanner.close();
        return inputSearchWord;
    }

    private static void searchForMeaningFromWord(RandomAccessFile randomAccessFile, long byteOffset, int length) throws IOException {
        long startTime = System.currentTimeMillis();
        try {
            randomAccessFile.seek(byteOffset);
            byte[] buffer = new byte[length];

            randomAccessFile.read(buffer);
            System.out.println("Identified word and meaning " + new String(buffer));
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("Time taken to search using index:" + (endTime - startTime)+"ms");
        }
    }

    private void preExtractByteOffsets(File dictionaryFile, Map<String, SearchMetadata> wordByteOffsetMap) throws FileNotFoundException {
        Scanner myReader = new Scanner(dictionaryFile);
        long byteOffset = 0;
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            String[] pair = data.split("\\,");
            wordByteOffsetMap.put(pair[0], new SearchMetadata(byteOffset, data.getBytes().length));
            byteOffset += data.getBytes().length + 1;
        }
        myReader.close();
    }


}
