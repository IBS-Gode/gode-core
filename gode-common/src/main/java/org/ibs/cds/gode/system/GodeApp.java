package org.ibs.cds.gode.system;

import org.ibs.cds.gode.stream.repo.DataPipeline;
import org.springframework.boot.SpringApplication;

import java.time.LocalDateTime;

public class GodeApp {

    public static void start(Class<?> runner,String... args){
        beforeStart();
        doStart(runner, args);
        afterStart();
    }

    public static void beforeStart(){
        System.out.println("Starting application");
    }

    public static void doStart(Class<?> runner,String... args){
        GodeAppEnvt.ofApp(SpringApplication.run(runner, args));
        System.out.println("A̳ᴡ̳ᴀ̳ᴋ̳ᴇ̳ @̳"+ LocalDateTime.now());
    }

    public static void afterStart(){
        GodeAppEnvt.getOptionalObject(DataPipeline.class).ifPresent(DataPipeline::start);
    }



}
