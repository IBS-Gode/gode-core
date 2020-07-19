package org.ibs.cds.gode.system;

import org.ibs.cds.gode.exception.KnownException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.concurrent.atomic.AtomicReference;

public class GodeAppEnvt extends GodeConstant{

    private static GodeAppEnvt godeAppEnvt;
    private AtomicReference<ConfigurableApplicationContext> applicationContext;
    private Environment environment;

    public GodeAppEnvt(ConfigurableApplicationContext applicationContext){
        this.applicationContext = new AtomicReference<>(applicationContext);
        this.environment = applicationContext.getEnvironment();
    };

    protected static void ofApp(ConfigurableApplicationContext applicationContext){
        godeAppEnvt = new GodeAppEnvt(applicationContext);
    }

    public static <T> T getObject(Class<T> className) {
        try {
            return godeAppEnvt.applicationContext.get().getBean(className);
        } catch (Exception e) {
            throw KnownException.OBJECT_NOT_FOUND.provide(e);
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T getObject(String beanName) {
        try {
            return (T) godeAppEnvt.applicationContext.get().getBean(beanName);
        } catch (Exception e) {
            throw KnownException.OBJECT_NOT_FOUND.provide(e);
        }

    }

    public <T> T getProperty(String prop, Class<T> type){
        return environment.getProperty(prop, type);
    }

    public String getProperty(String prop){
        return environment.getProperty(prop);
    }

    public String getPropertyOrDefault(String prop,String defaultValue){
        return environment.getProperty(prop, defaultValue);
    }

}
