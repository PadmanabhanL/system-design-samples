package com.leetcode.contest.fileserver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

public class FileHandler implements HttpHandler {

    public static final String DIR_PATH = "leetcode-contest/src/main/resources/problem-files/";

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestUri = exchange.getRequestURI().toString();

        String[] parts = requestUri.split("\\/");

        if (parts.length > 3) {
            fetchFileData(exchange, parts);
        } else {
            fetchFilesMetadataFromDirectory(exchange, parts);
        }


    }

    private void fetchFilesMetadataFromDirectory(HttpExchange exchange, String[] parts) throws IOException {
        String problemDirPath = parts[2];

        List<File> files = Files.walk(Paths.get(DIR_PATH + problemDirPath)).map(path -> path.toFile()).filter(File::isFile).toList();

        List<Map<String, Object>> responseList = files.stream().map(file -> {
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> individualFileResponse = new HashMap<>();
            individualFileResponse.put("fileName", file.getName());
            individualFileResponse.put("fileSize", file.length());
            individualFileResponse.put("fileDate",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    .format(new Timestamp(file.lastModified()).toLocalDateTime()));
            list.add(individualFileResponse);
            return list;
        }).flatMap(Collection::stream).toList();

        exchange.sendResponseHeaders(200, 0);
        TypeToken<List<Map<String, Object>>> typeToken = new TypeToken<>() {};

        exchange.getResponseBody().write(new Gson().toJson(responseList, typeToken.getType()).getBytes());
        exchange.getResponseBody().close();

        exchange.sendResponseHeaders(200, 0);
    }

    private void fetchFileData(HttpExchange exchange, String[] parts) throws IOException {
        System.out.println("Fetching file data from server");
        String problemDirPath = parts[2];

        String fileName = parts[3];

        Optional<File> fileOptional = Files.walk(Paths.get(DIR_PATH + problemDirPath))
                .map(path -> path.toFile())
                .filter(file -> file.isFile() && file.getName().equals(fileName)).findFirst();

        if (fileOptional.isPresent()) {
            File file = fileOptional.get();
            byte[] bytes = Files.readAllBytes(file.toPath());
            TypeToken<Map<String, Object>> typeToken = new TypeToken<>() {};
            long timestamp = file.lastModified();
            String lastModifiedAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(new Timestamp(timestamp).toLocalDateTime());
            String content = new Gson().toJson(Map.of("fileName", file.getName(), "content", new String(bytes), "updatedAt", lastModifiedAt), typeToken.getType());
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().write(content.getBytes());
            exchange.getResponseBody().close();
        } else {
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().write("Not found".getBytes());
            exchange.getResponseBody().close();
        }
    }
}
