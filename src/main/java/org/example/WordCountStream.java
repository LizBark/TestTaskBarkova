package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.*;


public class WordCountStream {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide the filename as an argument.");
            return;
        }

        String filepath = args[0];
        WordCountStream obj = new WordCountStream();
        obj.WordCountStreamMethod(filepath);
    }
    public Map<String, Integer> WordCountStreamMethod(String filepath) {
        Map<String, Integer> wordCounts;
        try {
            Stream<String> lines = Files.lines(Paths.get(filepath));
            wordCounts = lines.flatMap(line -> Arrays.stream(line.split("[^a-zA-Z']")))
                    .filter(word -> !word.isEmpty())
                    .map(String::toLowerCase)
                    .collect(Collectors.toMap(word -> word, word -> 1, Integer::sum));

            Comparator<Map.Entry<String, Integer>> compareByValue =
                    Map.Entry.<String, Integer>comparingByValue().reversed();

            Comparator<Map.Entry<String, Integer>> compareByKey =
                    Map.Entry.comparingByKey();

            Comparator<Map.Entry<String, Integer>> compareBoth =
                    compareByValue.thenComparing(compareByKey);

            Map<String, Integer> sortedMap = wordCounts.entrySet().stream()
                    .sorted(compareBoth)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (entry1, entry2) -> entry1, LinkedHashMap::new));

            sortedMap.forEach((key, value) -> System.out.println(key + "=" + value));

            return sortedMap;

        } catch (IOException e) {
            System.out.println("File not found: " + e.getMessage());
            return null;
        }
    }
}