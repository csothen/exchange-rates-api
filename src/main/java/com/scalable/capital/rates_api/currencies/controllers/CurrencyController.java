package com.scalable.capital.rates_api.currencies.controllers;

import java.util.List;

import com.scalable.capital.rates_api.currencies.models.Amount;
import com.scalable.capital.rates_api.currencies.models.Currency;
import com.scalable.capital.rates_api.currencies.services.ICurrencyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/currencies")
@RestController
public class CurrencyController {

    @Autowired
    private ICurrencyService service;

    @GetMapping
    public ResponseEntity<List<Currency>> getAvailableCurrencies() {
        return new ResponseEntity<List<Currency>>(service.listAll(), HttpStatus.OK);
    }

    @GetMapping("/convert/{amount}")
    public ResponseEntity<Amount> getCurrencyAmountConversion(@PathVariable Double amount, @RequestParam String from,
            @RequestParam String to) {
        Amount convertedAmount = service.convertAmount(amount, from, to);

        if (convertedAmount == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Amount>(convertedAmount, HttpStatus.OK);
    }
}
