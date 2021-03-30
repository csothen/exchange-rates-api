package com.scalable.capital.rates_api.rates.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.scalable.capital.rates_api.rates.models.Link;
import com.scalable.capital.rates_api.rates.models.Rate;
import com.scalable.capital.rates_api.rates.services.IRateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/rates")
@RestController
public class RateController {

    @Autowired
    private IRateService service;

    @GetMapping
    public ResponseEntity<Rate> getRateForCurrencyPair(@RequestParam String from, @RequestParam String to) {
        Rate rate = service.getRateForCurrencyPair(from, to);
        if (rate == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Rate>(rate, HttpStatus.OK);
    }

    @GetMapping("/chart")
    public ResponseEntity<Link> getChartLinkForCurrencyPair(@RequestParam String from, @RequestParam String to) {
        Link link = service.getChartLinkForCurrencyPair(from, to);
        if (link == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Link>(link, HttpStatus.OK);
    }
}
