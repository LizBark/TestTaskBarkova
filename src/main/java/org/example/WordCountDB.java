package org.example;

import java.sql.*;
import java.io.*;
import java.util.*;

public class WordCountDB {
    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length == 0) {
            System.out.println("Please provide the filename as an argument");
            return;
        }

        String filepath = args[0];

        WordCountDB obj = new WordCountDB();
        Map<String, Integer> sortedMap = obj.WordCountDBMethod(filepath);
        createNewDB(sortedMap, filepath);
    }

    public Map<String, Integer> WordCountDBMethod(String filepath) {
        Map<String, Integer> wordCounts = new HashMap<>();

        try (Scanner fileReader = new Scanner(new File(filepath))) {
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String[] words = line.split("[^a-zA-Z']");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        word = word.toLowerCase();
                        wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                    }
                }
            }

            List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(wordCounts.entrySet());
            sortedList.sort((entry1, entry2) -> {
                int cmp = entry2.getValue().compareTo(entry1.getValue());
                if (cmp != 0) return cmp;
                return entry1.getKey().compareTo(entry2.getKey());
            });

            LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : sortedList) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }

            return sortedMap;

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    public static void createNewDB(Map<String, Integer> sortedMap, String filepath) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:WordCounts.db"); Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS BookScans (id INTEGER PRIMARY KEY AUTOINCREMENT, book TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Words (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT, count INTEGER, bookScanId INTEGER, FOREIGN KEY(bookScanId) REFERENCES BookScans(id))");

            try (PreparedStatement insertScans = connection.prepareStatement("INSERT INTO BookScans(book) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
                insertScans.setString(1, filepath);
                insertScans.executeUpdate();
                ResultSet rs1 = insertScans.getGeneratedKeys();

                if (rs1.next()) {
                    int bookIdScanInt = rs1.getInt(1);
                    try (PreparedStatement insertWords = connection.prepareStatement("INSERT INTO Words(word, count, bookScanId) VALUES(?,?,?)")) {
                        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
                            insertWords.setString(1, entry.getKey());
                            insertWords.setInt(2, entry.getValue());
                            insertWords.setInt(3, bookIdScanInt);
                            insertWords.addBatch();
                        }
                        insertWords.executeBatch();
                    }
                }
            }

            System.out.println("Results");
            ResultSet rs = statement.executeQuery("SELECT * FROM Words");
            while (rs.next()) {
                System.out.println(rs.getString("word") + " = " + rs.getInt("count") + " scanned from book " + rs.getString("bookScanId"));
            }

            System.out.println("BookScan");
            ResultSet rsB = statement.executeQuery("SELECT * FROM BookScans");
            while (rsB.next()) {
                System.out.println("Book id is " + rsB.getInt("id") + " filepath is " + rsB.getString("book"));
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}