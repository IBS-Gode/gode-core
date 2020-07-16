
package org.ibs.cds.gode.http.configuration;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "type",
    "mandatory",
    "default",
    "where"
})
public class Variable {

    @JsonProperty("name")
    private String name;
     @JsonProperty("value_as")
    private String valueAs;
    @JsonProperty("type")
    private VariableType type;
    @JsonProperty("mandatory")
    private boolean mandatory;
    @JsonProperty("default")
    private String _default;
    @JsonProperty("where")
    private List<VariableContext> where = null;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("type")
    public VariableType getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(VariableType type) {
        this.type = type;
    }

    @JsonProperty("mandatory")
    public boolean getMandatory() {
        return mandatory;
    }

    @JsonProperty("mandatory")
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @JsonProperty("default")
    public String getDefault() {
        return _default;
    }

    @JsonProperty("default")
    public void setDefault(String _default) {
        this._default = _default;
    }

    @JsonProperty("where")
    public List<VariableContext> getWhere() {
        return where;
    }

    @JsonProperty("where")
    public void setWhere(List<VariableContext> where) {
        this.where = where;
    }

    @JsonProperty("value_as")
    public String getValueAs() {
        return valueAs;
    }

   @JsonProperty("value_as")
    public void setValueAs(String valueAs) {
        this.valueAs = valueAs;
    }
}
