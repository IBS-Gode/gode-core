package org.ibs.cds.gode.test.mock;

import org.ibs.cds.gode.util.StringUtils;

import java.util.Collections;

public class Prejudices {

    public static Prejudice autoGetSet(){
        return new Prejudice(
                "getSet",
                (m,a)-> m.getReturnType().getName().equals("void") && a.length == 1 && m.getName().startsWith("set"),
                m -> StringUtils.replace(m.getName(), "set", "get"),
                a -> Collections.emptyList(),
                (m,a) -> a[0]
                );
    }

    public static Prejudice autoSetBoolean(){
        return new Prejudice(
                "getSetBoolean",
                (m,a)-> m.getReturnType().getName().equals("void") && a.length == 1 && a[0].getClass().getSimpleName().equalsIgnoreCase("boolean") && m.getName().startsWith("set"),
                m -> StringUtils.replace(m.getName(), "set", "is"),
                a -> Collections.emptyList(),
                (m,a) -> a[0]
        );
    }
}
