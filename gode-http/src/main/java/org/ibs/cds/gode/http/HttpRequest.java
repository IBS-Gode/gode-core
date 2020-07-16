package org.ibs.cds.gode.http;

import org.ibs.cds.gode.util.FileReader;
import java.io.IOException;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import org.ibs.cds.gode.exception.GodeException;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.http.configuration.Request;
import org.ibs.cds.gode.http.configuration.RequestType;
import org.ibs.cds.gode.http.configuration.Variable;
import org.ibs.cds.gode.http.configuration.VariableContext;
import org.ibs.cds.gode.util.Assert;

/**
 *
 * @author manugraj
 */

public class HttpRequest {

    private @Getter(AccessLevel.PROTECTED) Request request;
    private @Getter(AccessLevel.PROTECTED) String rawRequest;

    protected HttpRequest(Request request) {
        this.request = request;
        this.rawRequest = requestBody();
    }

    private String requestBody() throws GodeException {
        if(this.request.getBody() == null) return null;
        try {
            return this.request.getBody().getRaw() != null ? this.request.getBody().getRaw() : FileReader.readFile(this.request.getBody().getFile());
        } catch (IOException ex) {
            throw KnownException.HTTP_EXCEPTION.provide(ex, "Request body could not be set");
        }
    }

    public HttpRequest header(String key, String value) {
        this.request.getHeader().put(key, value);
        return this;
    }

    public HttpRequest queryParam(String key, String value) {
        this.request.getQueryParams().put(key, value);
        return this;
    }

    public HttpRequest pathParam(String key, String value) {
        this.request.getPathParams().put(key, value);
        return this;
    }

    public HttpRequest variables(Map<String, String> variables) {
        this.request.getVariables().stream().forEach(variable -> processVariable(variable, variables));
        return this;
    }
    
    public HttpInvoke prepare(){
        Assert.notNull("Request type cannot be empty", request.getRequestType());
        if(RequestType.GET == request.getRequestType()) return new HttpInvoker(this);
        return new HttpInvokerWithBody(this);
    }

    private void processVariable(Variable variable, Map<String, String> variableValues) {
        String toSetValue = variableValues.get(variable.getName());
        if (toSetValue == null) {
            if (variable.getMandatory()) {
                throw KnownException.HTTP_EXCEPTION.provide("Mandatory field: " + variable.getName() + " is not set");
            }
            String defaultValue = variable.getDefault();
            if (defaultValue == null) {
                return;
            }
            toSetValue = defaultValue;
        }

        verifyFieldNature(variable, toSetValue);
        setVariable(variable, toSetValue);

    }

    private void setVariable(Variable variable, String toSetValue) {
        String variableType;
        for (VariableContext where : variable.getWhere()) {
            variableType = variable.getValueAs() == null ? variable.getName() : variable.getValueAs() ;
            switch (where) {
                case queryParams:
                    this.queryParam(variableType, toSetValue);
                    break;
                case pathParams:
                    this.pathParam(variableType, toSetValue);
                    break;
                case header:
                    this.header(variableType, toSetValue);
                    break;
                case body:
                    this.rawRequest = this.rawRequest.replace("${".concat(variableType).concat("}"), toSetValue);
                    break;
            }

        }
    }

    private void verifyFieldNature(Variable variable, String toSetValue) throws GodeException {
        try {
            variable.getType().value(toSetValue);
        } catch (Exception e) {
            throw KnownException.HTTP_EXCEPTION.provide(e, "FieldType mismatch exception for" + variable.getName());
        }
    }

}
