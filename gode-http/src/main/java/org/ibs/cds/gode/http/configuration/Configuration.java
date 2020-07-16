
package org.ibs.cds.gode.http.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "request"
})
public class Configuration {

    @JsonProperty("request")
    private List<Request> request;

    @JsonProperty("request")
    public List<Request> getRequest() {
        return request;
    }

    @JsonProperty("request")
    public void setRequest(List<Request> request) {
        this.request = request;
    }

}
