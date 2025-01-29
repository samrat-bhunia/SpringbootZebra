package com.poc.poc.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexCodeScanner {

	public static void main(String[] args) throws IOException {
        String filePath = "src/main/java/com/poc/poc/service/TransactionService.java";

        String code = new String(Files.readAllBytes(Paths.get(filePath)));

        Pattern pattern = Pattern.compile("\"(SELECT|INSERT|UPDATE|DELETE|MERGE|TRUNCATE)[^;]*?\"");
        Pattern concatPattern = Pattern.compile("\"(SELECT|INSERT|UPDATE|DELETE|MERGE|TRUNCATE)[^\"]*\"\\s*\\+\\s*\".*\"");

        Matcher matcher = pattern.matcher(code);
        while (matcher.find()) {
            String matchedQuery = matcher.group(0);
            System.out.println("Found static SQL Query: " + matchedQuery);
        }

        Matcher concatMatcher = concatPattern.matcher(code);
        while (concatMatcher.find()) {
            String matchedQuery = concatMatcher.group(0);
            System.out.println("Found dynamically generated SQL Query: " + matchedQuery);
        }
    }
}
