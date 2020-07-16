package org.ibs.cds.gode.http;

import org.ibs.cds.gode.util.YamlReadWriteUtil;
import java.io.IOException;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.http.configuration.Configuration;

/**
 *
 * @author manugraj
 */
public class CallHttp {

    private Configuration configuration;

    private CallHttp(Configuration configuration) {
        this.configuration = configuration;
    }

    public static CallHttp config(String location) {
        try {
            return new CallHttp(YamlReadWriteUtil.readResource(location, Configuration.class));
        } catch (IOException ex) {
            throw KnownException.HTTP_EXCEPTION.provide(ex);
        }
    }
    
    public HttpRequest request(String requestName){
       return new HttpRequest(this.configuration.getRequest().stream().filter(k->k.getName().equals(requestName)).findAny().orElseThrow());
    }
}
