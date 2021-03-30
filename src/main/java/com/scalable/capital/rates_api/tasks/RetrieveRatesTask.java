package com.scalable.capital.rates_api.tasks;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.scalable.capital.rates_api.currencies.models.Currency;
import com.scalable.capital.rates_api.currencies.services.ICurrencyService;
import com.scalable.capital.rates_api.rates.models.Rate;
import com.scalable.capital.rates_api.rates.services.IRateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class RetrieveRatesTask {

    private final Logger logger = LoggerFactory.getLogger(RetrieveRatesTask.class);

    @Autowired
    private IRateService rateService;

    @Autowired
    private ICurrencyService currencyService;

    @Value("${rates.source.url}")
    public String url;

    @Scheduled(initialDelay = 0, fixedRate = 24 * 60 * 60 * 1000)
    public void retrieveRates() {
        logger.info("Started retrieve rates task...");
        List<Rate> rates = loadRates();
        List<Currency> currencies = getCurrenciesFromRates(rates);
        Boolean rSuccess = rateService.updateRates(rates);
        Boolean cSuccess = currencyService.updateCurrencies(currencies);
        if (rSuccess && cSuccess) {
            logger.info("Finished updating database successfully...");
        } else {
            logger.error("Something went wrong while updating the database...");
        }
    }

    private List<Rate> loadRates() {
        List<Rate> rates = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(url);

            doc.getDocumentElement().normalize();

            NodeList ratesNodeList = doc.getElementsByTagName("Cube");

            for (int i = 0; i < ratesNodeList.getLength(); i++) {
                Node rateNode = ratesNodeList.item(i);
                if (rateNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) rateNode;
                    if (elem.getAttribute("currency").length() > 0 && elem.getAttribute("rate").length() > 0) {
                        Rate rate = Rate.builder().from("EUR").to(elem.getAttribute("currency"))
                                .rate(Double.parseDouble(elem.getAttribute("rate"))).build();

                        rates.add(rate);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return rates;
    }

    private List<Currency> getCurrenciesFromRates(List<Rate> rates) {
        List<Currency> currencies = new ArrayList<>();

        currencies.add(Currency.builder().code(rates.get(0).getFrom()).numOfRequests(0).build());

        for (Rate r : rates) {
            currencies.add(Currency.builder().code(r.getTo()).numOfRequests(0).build());
        }

        return currencies;
    }
}
