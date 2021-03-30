package com.scalable.capital.rates_api.rates.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scalable.capital.rates_api.rates.models.Rate;

import org.springframework.stereotype.Repository;

@Repository
public class RateRepositoryInMemory implements IRateRepository {

    private Map<String, Rate> ratesData = new HashMap<>();

    @Override
    public List<Rate> getAll() {
        return new ArrayList<>(ratesData.values());
    }

    @Override
    public Rate getRateByCurrencyPair(String from, String to) {
        String id = generateIdentifier(from, to);
        Rate rate = ratesData.get(id);
        return rate;
    }

    @Override
    public Rate createRate(Rate rate) {
        String id = generateIdentifier(rate.getFrom(), rate.getTo());

        Rate existingRate = ratesData.get(id);

        if (existingRate != null) {
            return null;
        }

        ratesData.put(id, rate);
        return rate;
    }

    @Override
    public Rate updateRate(Rate rate) {
        String id = generateIdentifier(rate.getFrom(), rate.getTo());
        return ratesData.put(id, rate);
    }

    @Override
    public Boolean deleteRate(Rate rate) {
        String id = generateIdentifier(rate.getFrom(), rate.getTo());
        ratesData.remove(id);
        return true;
    }

    private String generateIdentifier(String from, String to) {
        return String.format("%s_%s", from, to);
    }
}
