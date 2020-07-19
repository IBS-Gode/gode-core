package org.ibs.cds.gode.test.mock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CallBluntMethod implements ICallMethod{

    private final String method;
    private final MockClass mockClass;

    public MockClass thenReturn(Object result) {
        mockClass.getBluntMap().put(method, result);
        return mockClass;
    }
}