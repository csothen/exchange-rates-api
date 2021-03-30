package com.scalable.capital.rates_api.currencies.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scalable.capital.rates_api.currencies.models.Currency;

import org.springframework.stereotype.Repository;

@Repository
public class CurrencyRepositoryInMemory implements ICurrencyRepository {

    private Map<String, Currency> currenciesData = new HashMap<>();

    @Override
    public List<Currency> getAll() {
        return new ArrayList<>(currenciesData.values());
    }

    @Override
    public Currency getCurrencyByCode(String code) {
        return currenciesData.get(code);
    }

    @Override
    public Currency createCurrency(Currency currency) {
        Currency existingCurrency = currenciesData.get(currency.getCode());

        if (existingCurrency != null) {
            return null;
        }

        currenciesData.put(currency.getCode(), currency);
        return currency;
    }

    @Override
    public Currency updateCurrency(Currency currency) {
        return currenciesData.put(currency.getCode(), currency);
    }

    @Override
    public Boolean deleteCurrency(String code) {
        currenciesData.remove(code);
        return true;
    }

}
