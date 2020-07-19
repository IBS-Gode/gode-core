package org.ibs.cds.gode.util;

import java.util.UUID;

public class RandomUtils extends org.apache.commons.lang3.RandomUtils {

    public static Long unique(){
        return UUID.randomUUID().getLeastSignificantBits();
    }

    public static String uniqueString(){
        return String.valueOf(UUID.randomUUID().getLeastSignificantBits());
    }
}
