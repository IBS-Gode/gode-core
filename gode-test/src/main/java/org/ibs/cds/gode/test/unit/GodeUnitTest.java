package org.ibs.cds.gode.test.unit;

import org.ibs.cds.gode.counter.CounterGenerator;
import org.ibs.cds.gode.system.GodeAppEnvtTest;
import org.ibs.cds.gode.test.Test;
import org.ibs.cds.gode.test.TestType;
import org.ibs.cds.gode.test.mock.Mock;
import org.junit.BeforeClass;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class GodeUnitTest implements Test {

    private static final AtomicBoolean initStatus = new AtomicBoolean(false);

    @BeforeClass
    public static void initUnitTest(){
        if(!initStatus.get()) {
            ConfigurableEnvironment envt = Mock.of(ConfigurableEnvironment.class);
            ConfigurableApplicationContext ctx = Mock.of(ConfigurableApplicationContext.class);
            CounterGenerator counterGenerator = Mock.of(CounterGenerator.class);
            Mock.when(ConfigurableApplicationContext.class, "getEnvironment").thenReturn(envt);
            Mock.when(ConfigurableApplicationContext.class, "getBean", CounterGenerator.class).thenReturn(counterGenerator);
            Mock.when(CounterGenerator.class, "getNextValue").thenReturn(BigInteger.valueOf(UUID.randomUUID().getLeastSignificantBits()));
            GodeAppEnvtTest.ofApp(ctx);
            initStatus.compareAndSet(false, true);
        }
    }

    @Override
    public TestType testType() {
        return TestType.UNIT;
    }
}
