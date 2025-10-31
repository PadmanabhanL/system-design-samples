package com.system.hashing.function;

import static com.system.service.ConsistentHashingService.LIMIT;

public interface HashingFunction {

    int hash(String key);

    default int toInt(byte[] digest) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (digest[i] & 0xFF) << (8 * i);
        }

        long unsignedValue = result & 0xFFFFFFFFL;

        return (int) (unsignedValue % LIMIT);
    }
}
