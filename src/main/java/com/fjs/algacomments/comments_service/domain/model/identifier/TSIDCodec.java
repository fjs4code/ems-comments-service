package com.fjs.algacomments.comments_service.domain.model.identifier;

import io.hypersistence.tsid.TSID;

public final class TSIDCodec {

    private TSIDCodec() {
    }

    public static String encode(long id) {
        return TSID.from(id).toString();
    }

    public static long decode(String id) {
        if (!TSID.isValid(id)) {
            throw new IllegalArgumentException(
                    "Invalid TSID: " + id
            );
        }

        return TSID.from(id).toLong();
    }

}
