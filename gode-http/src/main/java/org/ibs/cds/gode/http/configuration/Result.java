package org.ibs.cds.gode.http.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 *
 * @author manugraj
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "value_at",
    "type"
})
public class Result {

    @JsonProperty("name")
    private String name;
    @JsonProperty("value_at")
    private String valueAt;
     @JsonProperty("type")
    private VariableType type;
     
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("value_at")
    public String getValueAt() {
        return valueAt;
    }

    @JsonProperty("value_at")
    public void setValueAt(String valueAt) {
        this.valueAt = valueAt;
    }
    
    @JsonProperty("type")
    public VariableType getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(VariableType type) {
        this.type = type;
    }

}
