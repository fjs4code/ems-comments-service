package com.fjs.algacomments.comments_service.domain.model.identifier;

import java.security.SecureRandom;
import java.util.UUID;

public final class UUIDv7Generator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private UUIDv7Generator() {
    }

    public static UUID next() {
        long mostSignificantBits = (System.currentTimeMillis() << 16)
                | 0x7000L | (RANDOM.nextLong() & 0x0fffL);
        long leastSignificantBits = (RANDOM.nextLong() & 0x3fffffffffffffffL)
                | 0x8000000000000000L;
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
