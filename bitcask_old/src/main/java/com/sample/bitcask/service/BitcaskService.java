package com.sample.bitcask.service;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class BitcaskService {

    private final Environment environment;

    public BitcaskService(Environment environment) {
        this.environment = environment;
    }

    public void add(String key, String value) {
        String filePrefix = environment.getProperty("file-prefix");

        


    }

}
