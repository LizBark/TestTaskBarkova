package org.example;

import java.sql.*;
import java.io.*;
import java.util.*;

public class WordCountDBSecond {
    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length == 0) {
            System.out.println("Please provide the filename as an argument");
            return;
        }

        String filepath = args[0];

        WordCountDBSecond obj = new WordCountDBSecond();
        obj.WordCountDBMethodSecond(filepath);
    }

    public void WordCountDBMethodSecond(String filepath) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:WordCountsSecond.db");
             Statement statement = connection.createStatement()) {

            statement.setQueryTimeout(30);

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS BookScans (id INTEGER PRIMARY KEY AUTOINCREMENT, book TEXT UNIQUE)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Words (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT, count INTEGER, bookScanId INTEGER, FOREIGN KEY(bookScanId) REFERENCES BookScans(id))");

            int bookIdScanInt;
            try (PreparedStatement psWasScanned = connection.prepareStatement("SELECT id FROM BookScans WHERE book = ?")) {
                psWasScanned.setString(1, filepath);
                ResultSet rs = psWasScanned.executeQuery();

                if (rs.next()) {

                    bookIdScanInt = rs.getInt("id");
                    printResultsAndBookScan(bookIdScanInt, connection);

                } else {

                    try (PreparedStatement insertScans = connection.prepareStatement("INSERT INTO BookScans(book) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
                        insertScans.setString(1, filepath);
                        insertScans.executeUpdate();
                        ResultSet rs1 = insertScans.getGeneratedKeys();
                        rs1.next();
                        bookIdScanInt = rs1.getInt(1);
                    }

                    try (Scanner fileReader = new Scanner(new File(filepath))) {
                        while (fileReader.hasNextLine()) {
                            String line = fileReader.nextLine();
                            String[] words = line.split("[^a-zA-Z']");
                            for (String word : words) {
                                if (!word.isEmpty()) {
                                    word = word.toLowerCase();
                                    insertOrUpdateWord(word, bookIdScanInt, connection);
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("File not found: " + e.getMessage());
                    }

                    printResultsAndBookScan(bookIdScanInt, connection);

                }
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void insertOrUpdateWord(String word, int bookScanId, Connection connection) throws SQLException {
        String selectSql = "SELECT count FROM Words WHERE word = ? AND bookScanId = ?";
        String insertSql = "INSERT INTO Words(word, count, bookScanId) VALUES(?,?,?)";
        String updateSql = "UPDATE Words SET count = count + 1 WHERE word = ? AND bookScanId = ?";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
            selectStatement.setString(1, word);
            selectStatement.setInt(2, bookScanId);
            ResultSet rs = selectStatement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                        updateStatement.setString(1, word);
                        updateStatement.setInt(2, bookScanId);
                        updateStatement.executeUpdate();
                    }
                }
            } else {
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setString(1, word);
                    insertStatement.setInt(2, 1);
                    insertStatement.setInt(3, bookScanId);
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    private void printResultsAndBookScan(int bookIdScanInt, Connection connection) throws SQLException {
        System.out.println("Results");

        try (PreparedStatement ps = connection.prepareStatement("SELECT word, count, bookScanId FROM Words WHERE bookScanId = ? ORDER BY count DESC, word ASC")) {
            ps.setInt(1, bookIdScanInt);
            ResultSet rsPrint = ps.executeQuery();

            while (rsPrint.next()) {
                System.out.println(rsPrint.getString("word") + " = " + rsPrint.getInt("count") + " scanned from book " + rsPrint.getString("bookScanId"));
            }
        }

        System.out.println("BookScan");
        try (Statement statement = connection.createStatement();
             ResultSet rsB = statement.executeQuery("SELECT * FROM BookScans")) {
            while (rsB.next()) {
                System.out.println("Book id is " + rsB.getInt("id") + " filepath is " + rsB.getString("book"));
            }
        }
    }

}