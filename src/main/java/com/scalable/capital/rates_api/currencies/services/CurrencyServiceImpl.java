package com.scalable.capital.rates_api.currencies.services;

import java.text.DecimalFormat;
import java.util.List;

import com.scalable.capital.rates_api.currencies.data.ICurrencyRepository;
import com.scalable.capital.rates_api.currencies.models.Amount;
import com.scalable.capital.rates_api.currencies.models.Currency;
import com.scalable.capital.rates_api.rates.models.Rate;
import com.scalable.capital.rates_api.rates.services.IRateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceImpl implements ICurrencyService {

    @Autowired
    private ICurrencyRepository repository;

    @Autowired
    private IRateService rateService;

    @Override
    public List<Currency> listAll() {
        return repository.getAll();
    }

    @Override
    public Amount convertAmount(Double amount, String from, String to) {
        Rate rate = rateService.getRateForCurrencyPair(from, to);

        if (rate == null) {
            return null;
        }

        Double convertedValue = rate.getRate() * amount;

        return Amount.builder().currency(to).value(convertedValue).build();
    }

    @Override
    public Boolean updateCurrencies(List<Currency> currencies) {
        for (Currency c : currencies) {
            Currency existingCurrency = repository.getCurrencyByCode(c.getCode());
            if (existingCurrency == null) {
                repository.createCurrency(c);
            }
        }
        return true;
    }

    @Override
    public void incrementNumOfRequests(String from, String to) {
        Currency currencyFrom = repository.getCurrencyByCode(from);
        Currency currencyTo = repository.getCurrencyByCode(to);

        if (currencyFrom == null || currencyTo == null) {
            return;
        }

        currencyFrom.setNumOfRequests(currencyFrom.getNumOfRequests() + 1);
        currencyTo.setNumOfRequests(currencyTo.getNumOfRequests() + 1);

        repository.updateCurrency(currencyFrom);
        repository.updateCurrency(currencyTo);

    }

}
