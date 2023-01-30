package com.cydeo.dto.common;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "auth_token"
})
@Generated("jsonschema2pojo")
public class TokenDto {

    @JsonProperty("auth_token")
    private String authToken;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("auth_token")
    public String getAuthToken() {
        return authToken;
    }

    @JsonProperty("auth_token")
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}