package org.example;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide the filename as an argument.");
            return;
        }

        String filepath = args[0];
        Main obj = new Main();
        obj.WordCount(filepath);
    }

    public Map<String, Integer> WordCount(String filepath) {
        Map<String, Integer> wordCounts = new HashMap<>();

        try (Scanner fileReader = new Scanner(new File(filepath))){
//            File myObj = new File(filepath);
//            Scanner fileReader = new Scanner(myObj);
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String[] wholeText = line.split("[^a-zA-Z']");
                for (String word : wholeText) {
                    if (!word.isEmpty()) {
                        word = word.toLowerCase();
                        wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                    }
                }
            }
            fileReader.close();

            List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(wordCounts.entrySet());
            sortedList.sort((e1, e2) -> {
                int cmp = e2.getValue().compareTo(e1.getValue());
                if (cmp != 0) return cmp;
                return e1.getKey().compareTo(e2.getKey());
            });

            LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : sortedList) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
                sortedMap.put(entry.getKey(), entry.getValue());
            }

            return sortedMap;

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            return Collections.emptyMap();
        }
    }
}