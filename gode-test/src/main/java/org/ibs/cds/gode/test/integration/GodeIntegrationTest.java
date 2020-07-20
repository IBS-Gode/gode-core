package org.ibs.cds.gode.test.integration;

import org.ibs.cds.gode.test.Test;
import org.ibs.cds.gode.test.TestType;

public abstract class GodeIntegrationTest implements Test {

    @Override
    public TestType testType() {
        return TestType.IT;
    }
}
