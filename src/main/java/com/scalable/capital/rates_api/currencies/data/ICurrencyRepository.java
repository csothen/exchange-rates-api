package com.scalable.capital.rates_api.currencies.data;

import java.util.List;

import com.scalable.capital.rates_api.currencies.models.Currency;

import org.springframework.stereotype.Component;

@Component
public interface ICurrencyRepository {

    public List<Currency> getAll();

    public Currency getCurrencyByCode(String code);

    public Currency createCurrency(Currency currency);

    public Currency updateCurrency(Currency currency);

    public Boolean deleteCurrency(String code);

}
