package org.example.wigellgym.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrencyConverterDTO {

    @JsonProperty("conversion_result")
    private double conversionResult;

    public CurrencyConverterDTO() {}

    public double getConversionResult() {
        return conversionResult;
    }

    public void setConversionResult(double conversionResult) {
        this.conversionResult = conversionResult;
    }
}
