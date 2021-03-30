package com.scalable.capital.rates_api.rates.services;

import java.util.List;

import com.scalable.capital.rates_api.currencies.services.ICurrencyService;
import com.scalable.capital.rates_api.rates.data.IRateRepository;
import com.scalable.capital.rates_api.rates.models.Link;
import com.scalable.capital.rates_api.rates.models.Rate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RateServiceImpl implements IRateService {

    private static final String EUR_CURRENCY = "EUR";

    @Autowired
    private IRateRepository repository;

    @Autowired
    private ICurrencyService currencyService;

    @Value("${rates.chart.url}")
    public String chartUrl;

    public RateServiceImpl(ICurrencyService currencyService, IRateRepository repository) {
        this.currencyService = currencyService;
        this.repository = repository;
    }

    @Override
    public Rate getRateForCurrencyPair(String from, String to) {
        Rate rate = repository.getRateByCurrencyPair(from, to);

        if (rate == null) {
            Rate newRate = calculateRate(from, to);

            if (newRate == null) {
                return null;
            }

            repository.createRate(newRate);
            rate = newRate;
        }

        currencyService.incrementNumOfRequests(rate.getFrom(), rate.getTo());

        return rate;
    }

    @Override
    public Link getChartLinkForCurrencyPair(String from, String to) {
        Rate rate = getRateForCurrencyPair(from, to);
        if (rate == null) {
            return null;
        }

        String url = String.format("%s?from=%s&to=%s", chartUrl, from, to);
        return Link.builder().url(url).build();
    }

    @Override
    public Boolean updateRates(List<Rate> rates) {
        List<Rate> allRates = repository.getAll();

        for (Rate r : rates) {
            repository.updateRate(r);
        }

        for (Rate r : allRates) {
            if (!r.getFrom().equals(EUR_CURRENCY)) {
                Rate updatedRate = calculateRate(r.getFrom(), r.getTo());
                if (updatedRate == null) {
                    repository.deleteRate(r);
                } else {
                    repository.updateRate(updatedRate);
                }
            }
        }
        return true;
    }

    private Rate calculateRate(String from, String to) {

        Rate fromRate = repository.getRateByCurrencyPair(EUR_CURRENCY, from);
        Rate toRate = repository.getRateByCurrencyPair(EUR_CURRENCY, to);

        if (to.equals(EUR_CURRENCY)) {
            toRate = Rate.builder().from(EUR_CURRENCY).to(EUR_CURRENCY).rate(1.0).build();
        }

        if (fromRate == null || toRate == null) {
            return null;
        }

        Double rate = toRate.getRate() / fromRate.getRate();

        return Rate.builder().from(from).to(to).rate(rate).build();
    }

}
