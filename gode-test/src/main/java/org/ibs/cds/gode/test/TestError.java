package org.ibs.cds.gode.test;

import lombok.Data;

@Data
public class TestError {
    private String testCaseName;
    private int testStepNo;
    private Throwable exception;
}
