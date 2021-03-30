package com.scalable.capital.rates_api.currencies.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Amount {
    private String currency;
    private Double value;
}
