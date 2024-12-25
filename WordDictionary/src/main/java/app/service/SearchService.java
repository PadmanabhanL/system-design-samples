package app.service;

import app.SearchMetadata;
import common.DictionaryConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class SearchService {

    public void searchForMeaningFromWord(SearchMetadata metadata) throws IOException {
        long startTime = System.currentTimeMillis();

        RandomAccessFile randomAccessFile = new RandomAccessFile(DictionaryConstants.DICTIONARY_FILE_PATH, "r");

        try {
            randomAccessFile.seek(metadata.getByteOffset());
            byte[] buffer = new byte[metadata.getLength()];

            randomAccessFile.read(buffer);
            String line = new String(buffer);

            System.out.println("Identified word and meaning " + line.split("\\,")[1]);
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("Time taken to search with index:"+ (endTime-startTime)+"ms");
        }
    }

    public String searchWithoutIndex(String inputSearchWord) {

        long startTime = System.currentTimeMillis();

        try {

            Scanner scanner = new Scanner(new File(DictionaryConstants.DICTIONARY_FILE_PATH));

            while (scanner.hasNextLine()) {
                String[] pair = scanner.nextLine().split("\\,");
                if (pair[0].equalsIgnoreCase(inputSearchWord)) {
                    return pair[1];
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            return "Unable to find Dictionary";
        } finally {
            long endTime = System.currentTimeMillis();

            System.out.println("Time taken to search "+inputSearchWord+":"+ (endTime - startTime) + "ms");
        }
        return "Unable to find word";
    }
}
