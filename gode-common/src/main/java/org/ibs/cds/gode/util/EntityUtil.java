package org.ibs.cds.gode.util;

import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;

public class EntityUtil {

    public static int hashCode(Object object){
        return HashCodeBuilder.reflectionHashCode(object);
    }

    public static String toString(Object object){
        return ToStringBuilder.reflectionToString(object);
    }

    public static boolean equals(Object i, Object he) { return EqualsBuilder.reflectionEquals(i, he); }

    public static boolean equals(Object i, Object he, Collection<String> excluded) { return EqualsBuilder.reflectionEquals(i, he, excluded); }

    public static <T> Class<T> rawType(TypeToken<T> typeToken){
       return (Class<T>) typeToken.getRawType();
    }

}
