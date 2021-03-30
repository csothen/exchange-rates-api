package com.scalable.capital.rates_api.rates.data;

import java.util.List;

import com.scalable.capital.rates_api.rates.models.Rate;

import org.springframework.stereotype.Component;

@Component
public interface IRateRepository {

    public List<Rate> getAll();

    public Rate getRateByCurrencyPair(String from, String to);

    public Rate createRate(Rate rate);

    public Rate updateRate(Rate rate);

    public Boolean deleteRate(Rate rate);
}
