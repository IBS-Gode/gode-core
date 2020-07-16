
package org.ibs.cds.gode.http.configuration;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "requestType",
    "header",
    "queryParams",
    "pathParams",
    "body",
    "variables"
})
public class Request {

    public Request(){
        this.header = new HashMap();
        this.queryParams = new HashMap();
        this.pathParams = new HashMap();
    }
    
    @JsonProperty("name")
    private String name;
    @JsonProperty("requestType")
    private RequestType requestType;
    @JsonProperty("header")
    private Map<String,String> header;
    @JsonIgnore
    private Map<String,Object> queryParams;
    @JsonProperty("pathParams")
    private Map<String,String> pathParams;
    @JsonProperty("body")
    private Body body;
    @JsonProperty("variables")
    private List<Variable> variables;
    @JsonProperty("path")
    private String path;
    @JsonProperty("response")
    private List<Result> response;

    @JsonProperty("response")
    public List<Result> getResponse() {
        return response;
    }
    
    @JsonProperty("response")
    public void setResponse(List<Result> response) {
        this.response = response;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("requestType")
    public RequestType getRequestType() {
        return requestType;
    }

    @JsonProperty("requestType")
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    @JsonProperty("header")
    public Map<String,String> getHeader() {
        return header;
    }

    @JsonProperty("header")
    public void setHeader(Map<String,String> header) {
        this.header = header;
    }

    @JsonAnyGetter
    public Map<String,Object> getQueryParams() {
        return queryParams;
    }

    @JsonAnySetter
    public void setQueryParams(Map<String,Object> queryParams) {
        this.queryParams = queryParams;
    }

    @JsonProperty("pathParams")
    public Map<String,String> getPathParams() {
        return pathParams;
    }

    @JsonProperty("pathParams")
    public void setPathParams(Map<String,String> pathParams) {
        this.pathParams = pathParams;
    }

    @JsonProperty("body")
    public Body getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(Body body) {
        this.body = body;
    }

    @JsonProperty("variables")
    public List<Variable> getVariables() {
        return variables;
    }

    @JsonProperty("variables")
    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
