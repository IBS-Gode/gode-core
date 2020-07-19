package org.ibs.cds.gode.test.mock;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Mock {

    private @Getter(AccessLevel.PROTECTED) static final Map<Class<?>, Object> classBank;
    private @Getter(AccessLevel.PROTECTED) static final Map<Class<?>, MockClass<?>> mocks;

    static {
        classBank = new ConcurrentHashMap();
        mocks = new ConcurrentHashMap<>();
    }

    public static <T> T of(Class<T> classType, Prejudice... prejudices){
        MockClass<T> mock = getMockClass(classType, prejudices);
        return provideMock(classType, mock);
    }

    public static <T> T data(Class<T> classType){
        return Mock.of(classType, Prejudices.autoSetBoolean(), Prejudices.autoGetSet());
    }

    @NotNull
    protected static <T> MockClass<T> getMockClass(Class<T> classType, Prejudice... prejudices) {
        return ArrayUtils.isEmpty(prejudices) ? MockClass.of(classType) : MockClassPrejudiced.of(classType, prejudices);
    }

    protected static <T> T provideMock(Class<T> classType, MockClass<T> mock) {
        mocks.put(classType, mock);
        T provided = mock.provide();
        classBank.put(classType, provided);
        return provided;
    }

    public static <T> T partial(Class<T> classType, Prejudice... prejudices){
        MockClass<T> mock = getMockClass(classType, prejudices);
        mock.trueMethodCall();
        return provideMock(classType, mock);
    }

    public static CallMethod when(Class classType, String method, Object... args){
      if(!mocks.containsKey(classType)) Mock.of(classType);
       return mocks.get(classType).when(method, args);
    }

    public static CallBluntMethod when(Class classType, String method){
        if(!mocks.containsKey(classType)) Mock.of(classType);
        return mocks.get(classType).blunt(method);
    }
}
