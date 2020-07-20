package org.ibs.cds.gode.test.system;

import org.ibs.cds.gode.test.Test;
import org.ibs.cds.gode.test.TestType;

public abstract class GodeSystemTest implements Test {

    @Override
    public TestType testType() {
        return TestType.SYSTEM;
    }
}
