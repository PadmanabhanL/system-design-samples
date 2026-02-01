package com.app.service;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class CrcFactory {

    public static Checksum create(CrcType type) {
        return switch (type) {
            case CRC32  -> new CRC32();
            case CRC32C -> new java.util.zip.CRC32C();
        };
    }
}