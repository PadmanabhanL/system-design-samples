package datageneration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static common.DictionaryConstants.DICTIONARY_FILE_PATH;

public class DictionaryGenerator {

    public static void main(String[] args) throws IOException {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        int length = 5;
        Set<String> dictionary = new HashSet<>();
        generateCombinations(alphabet, length, "", dictionary);

        System.out.println(dictionary.size());

        File dictionaryFile = new File(DICTIONARY_FILE_PATH);

        FileWriter fw = new FileWriter(dictionaryFile, true);

        Set<String> sortedDictionary = dictionary.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        for (String word: sortedDictionary) {
            fw.write(word + ","+ word+"\n");
        }
        fw.close();
    }

    private static void generateCombinations(char[] alphabet, int length, String prefix, Set<String> dictionary)  {
        if (prefix.length() == length) {
            dictionary.add(prefix);
            return;
        }

        for (char letter : alphabet) {
            generateCombinations(alphabet, length, prefix + letter, dictionary);
        }
    }
}
