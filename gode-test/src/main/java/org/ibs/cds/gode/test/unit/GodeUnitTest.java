package org.ibs.cds.gode.test.unit;

import org.ibs.cds.gode.counter.CounterGenerator;
import org.ibs.cds.gode.system.GodeAppEnvtTest;
import org.ibs.cds.gode.test.Test;
import org.ibs.cds.gode.test.TestType;
import org.ibs.cds.gode.test.mock.Mock;
import org.ibs.cds.gode.util.RandomUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.testng.annotations.BeforeClass;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class GodeUnitTest implements Test {

    private static final AtomicBoolean initStatus = new AtomicBoolean(false);

    @BeforeClass
    public static void initUnitTest(){
        if(!initStatus.get()) {
            ConfigurableEnvironment envt = Mock.of(ConfigurableEnvironment.class);
            ConfigurableApplicationContext ctx = Mock.of(ConfigurableApplicationContext.class);
            CounterGenerator counterGenerator = Mock.of(CounterGenerator.class);
            Mock.when(ConfigurableApplicationContext.class, "getEnvironment").thenReturn(envt);
            Mock.when(ConfigurableApplicationContext.class, "getBean", CounterGenerator.class).thenReturn(counterGenerator);
            Mock.when(CounterGenerator.class, "getNextValue").thenReturn(RandomUtils.unique(BigInteger.class));
            GodeAppEnvtTest.ofApp(ctx);
            initStatus.compareAndSet(false, true);
        }
    }

    @Override
    public TestType testType() {
        return TestType.UNIT;
    }
}
