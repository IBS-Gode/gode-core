package org.ibs.cds.gode.test.mock;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public class CallMethod implements ICallMethod{

    private final String method;
    private final Object[] args;
    private final MockClass mockClass;

    public MockClass thenReturn(Object result) {
        mockClass.getResultMap().put(method, Arrays.asList(args), result);
        return mockClass;
    }
}