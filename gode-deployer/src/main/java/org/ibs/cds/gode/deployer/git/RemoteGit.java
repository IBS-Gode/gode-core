package org.ibs.cds.gode.deployer.git;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.http.CallHttp;
import org.ibs.cds.gode.http.HttpResult;

/**
 *
 * @author manugraj
 */
public class RemoteGit {
  
    public enum RequestType{
        CREATE_REPO("CreateRepo"),
        CHECK_REPO("CheckRepo");
        
        private final String type;
        private RequestType(String type){
            this.type = type;
        }
    }
    
    public enum ResponseType{
        CLONE_URL("cloneUrl");
        private final String type;
        
        private ResponseType(String type){
            this.type = type;
        }
    }
    
    private final CallHttp http;
    private @Getter final String url;
    public RemoteGit(String gitType, String url){
      this.http = CallHttp.config("pipeline/git/".concat(gitType).concat(".yml"));
      this.url = url;
    }
    
    public Pair<Map<ResponseType, String>,Integer> execute(RequestType request, Map<String, String> variables, ResponseType... responseTypes){
        HttpResult result =execute(request, variables);
        Map<String, Object> preparedResponse = result.preparedResponse();
        return Pair.of(Arrays.stream(responseTypes).filter(k->preparedResponse.containsKey(k.type)).map(k->Pair.of(k,preparedResponse.get(k.type))).collect(Collectors.toMap(s->s.getKey(), s->s.getValue().toString())),result.getStatus()) ;
    }
    
    public HttpResult execute(RequestType request, Map<String, String> variables){
        return this.http.request(request.type).variables(variables).prepare().call(url);
    }
}
