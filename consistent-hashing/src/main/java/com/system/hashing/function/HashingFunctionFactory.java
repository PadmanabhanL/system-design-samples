package com.system.hashing.function;

import java.util.List;

public class HashingFunctionFactory {

    private final List<HashingFunction> hashingFunctions;

    public HashingFunctionFactory() {
        this.hashingFunctions = List.of(new MD5Hashing(), new SHA1Hashing(), new SHA256Hashing());
    }

    public List<HashingFunction> getHashingFunctions() {
        return hashingFunctions;
    }
}
