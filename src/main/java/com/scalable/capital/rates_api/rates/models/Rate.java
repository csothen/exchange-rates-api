package com.scalable.capital.rates_api.rates.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Rate {
    private String from;
    private String to;
    private Double rate;
}
