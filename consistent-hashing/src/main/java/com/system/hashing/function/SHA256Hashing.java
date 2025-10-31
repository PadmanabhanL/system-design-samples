package com.system.hashing.function;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Hashing implements HashingFunction {

    public int hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(key.getBytes());
            return toInt(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
