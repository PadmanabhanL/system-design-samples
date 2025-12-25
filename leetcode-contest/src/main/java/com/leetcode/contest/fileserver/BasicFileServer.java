package com.leetcode.contest.fileserver;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class BasicFileServer {

    private static final int PORT = 8082;

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new FileHandler());

        server.setExecutor(null);

        server.start();
    }
}
