package com.scalable.capital.rates_api.currencies.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Currency {
    private String code;
    private Integer numOfRequests;
}
