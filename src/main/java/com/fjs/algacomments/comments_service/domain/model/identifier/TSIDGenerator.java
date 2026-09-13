package com.fjs.algacomments.comments_service.domain.model.identifier;

import io.hypersistence.tsid.TSID;

public final class TSIDGenerator {

    private TSIDGenerator() {
    }

    public static long nextLong() {
        return TSID.Factory.getTsid().toLong();
    }

    public static String nextString() {
        return TSID.Factory.getTsid().toString();
    }
}
