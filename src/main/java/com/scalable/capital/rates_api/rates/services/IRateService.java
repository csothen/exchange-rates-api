package com.scalable.capital.rates_api.rates.services;

import java.util.List;

import com.scalable.capital.rates_api.rates.models.Link;
import com.scalable.capital.rates_api.rates.models.Rate;

public interface IRateService {
    public Rate getRateForCurrencyPair(String from, String to);

    public Link getChartLinkForCurrencyPair(String from, String to);

    public Boolean updateRates(List<Rate> rates);
}
