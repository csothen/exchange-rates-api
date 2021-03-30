package com.scalable.capital.rates_api.currencies.services;

import java.util.List;

import com.scalable.capital.rates_api.currencies.models.Amount;
import com.scalable.capital.rates_api.currencies.models.Currency;

import org.springframework.stereotype.Component;

@Component
public interface ICurrencyService {

    public List<Currency> listAll();

    public Amount convertAmount(Double amount, String from, String to);

    public Boolean updateCurrencies(List<Currency> currencies);

    public void incrementNumOfRequests(String from, String to);

}
