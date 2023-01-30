
package com.cydeo.dto.common;


import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("jsonschema2pojo")
public class UsdDto {


 
    private BigDecimal cad;
    private BigDecimal eur;
    private BigDecimal gbp;
    private BigDecimal inr;
    private BigDecimal jpy;


}