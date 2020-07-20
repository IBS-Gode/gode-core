package org.ibs.cds.gode.util;

import org.ibs.cds.gode.exception.KnownException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

public class RandomUtils extends org.apache.commons.lang3.RandomUtils {

    public static Long unique(){
        return UUID.randomUUID().getLeastSignificantBits();
    }

    public static String uniqueString(){
        return String.valueOf(UUID.randomUUID().getLeastSignificantBits());
    }

    public static <T> T unique(Class<T> classType){
        if (String.class.equals(classType)) {
            return (T) uniqueString();
        }else if(Long.class.equals(classType)){
            return (T) unique();
        }else if(Date.class.equals(classType)){
            return (T) new Date();
        }else if(Boolean.class.equals(classType)){
            return (T) Boolean.TRUE;
        }else if(BigDecimal.class.equals(classType)){
            return (T) BigDecimal.valueOf(unique());
        }else if(BigInteger.class.equals(classType)){
            return (T) BigInteger.valueOf(unique());
        }
        throw KnownException.INVALID_RANDOM_CONFIG.provide();
    }
}
