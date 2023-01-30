package com.cydeo.dto.common;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class CurrencyDto {

    private BigDecimal euro;
    private BigDecimal britishPound;
    private BigDecimal indianRupee;
    private BigDecimal japaneseYen;
    private BigDecimal canadianDollar;

}

