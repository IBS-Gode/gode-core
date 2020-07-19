package org.ibs.cds.gode.test;

import lombok.Data;

import java.util.List;

@Data
public class TestStatus {

    private final Boolean success;
    private List<TestError> errors;
}
