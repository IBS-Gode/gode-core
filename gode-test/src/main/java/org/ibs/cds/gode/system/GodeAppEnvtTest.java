package org.ibs.cds.gode.system;

import org.springframework.context.ConfigurableApplicationContext;

public class GodeAppEnvtTest extends GodeAppEnvt {

    public GodeAppEnvtTest(ConfigurableApplicationContext applicationContext) {
        super(applicationContext);
    }

    public static void ofApp(ConfigurableApplicationContext applicationContext){
        GodeAppEnvt.ofApp(applicationContext);
    }
}
