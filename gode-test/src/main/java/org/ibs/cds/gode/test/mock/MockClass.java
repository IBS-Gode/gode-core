package org.ibs.cds.gode.test.mock;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MockClass<T> {
    private  final @Getter(AccessLevel.PROTECTED) Table<String, List<Object>, Object> resultMap;
    private final @Getter(AccessLevel.PROTECTED) Map<String,Object> bluntMap;
    private final @Getter(AccessLevel.PROTECTED) Class<T> target;
    private boolean trueMethodCall = false;

    public void trueMethodCall() {
        this.trueMethodCall = true;
    }

    public static <T> MockClass<T> of(T object){
        return new MockClass(object.getClass());
    }

    public static <T> MockClass<T> of(Class<T> object){
        return new MockClass(object);
    }

    protected MockClass(Class<T> target) {
        this.target = target;
        this.resultMap = HashBasedTable.create();
        this.bluntMap = new HashMap();
    }

    public CallMethod when(String methodName, Object... args) {
        return new CallMethod(methodName, args, this);
    }

    public CallBluntMethod blunt(String methodName) {
        return new CallBluntMethod(methodName, this);
    }

    public CallMethod when(Method method, Object... args) {
        return new CallMethod(method.getName(), args, this);
    }

    private <V> V getBestResult(Method method, Object[] args){
        Object exactResult = resultMap.get(method.getName(), Arrays.asList(args));
        return (V) (exactResult == null ? bluntMap.get(method.getName()) : exactResult);
    }

    @SneakyThrows
    public T provide(){
        ProxyFactory pf = new ProxyFactory();
        if(target.isInterface()){
            pf.setInterfaces(new Class[]{target});
        }else{
            pf.setSuperclass(target);
        }

        MethodHandler mh = getMethodHandler();
        Constructor<?>[] constructors = target.getConstructors();
        for (Constructor constructor : constructors) {
            int parameterCount = constructor.getParameterCount();
            if(parameterCount == 0) return defaultMethod(pf.createClass(), mh);
            List<Pair<Object, Class>> params = Arrays
                    .stream(constructor.getParameterTypes())
                    .map(k-> Pair.of( Mock.of(k), k))
                    .collect(Collectors.toList());
            if(CollectionUtils.size(params) == parameterCount){
                return (T) pf.create(params.stream().map(k -> k.getValue()).toArray(Class[]::new),params.stream().map(k->k.getKey()).toArray(), mh);
            }

        }
        return defaultMethod(pf.createClass(), mh);
    }

    @SneakyThrows
    protected T defaultMethod(Class curr, MethodHandler mh){
        T t = (T) curr.getDeclaredConstructor().newInstance();
        ((Proxy) t).setHandler(mh);
        return t;
    }

    @NotNull
    protected MethodHandler getMethodHandler() {
        return (self, m, proceed, args) -> defaultValueProvider(self, m, proceed, args);
    }

    protected Object defaultValueProvider(Object self, Method m, Method proceed, Object[] args) throws IllegalAccessException, InvocationTargetException {
        Object result = getBestResult(m, args);
        return result == null && trueMethodCall ?  proceed.invoke(self, args) : result;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.target);
    }
}
