package com.scalable.capital.rates_api.currencies.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.scalable.capital.rates_api.currencies.data.ICurrencyRepository;
import com.scalable.capital.rates_api.currencies.models.Amount;
import com.scalable.capital.rates_api.currencies.models.Currency;
import com.scalable.capital.rates_api.rates.models.Rate;
import com.scalable.capital.rates_api.rates.services.IRateService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceImplTest {

    @Mock
    private ICurrencyRepository mockRepository;

    @Mock
    private IRateService mockRateService;

    @InjectMocks
    private CurrencyServiceImpl testObj;

    @BeforeEach
    public void setup() {
        testObj = new CurrencyServiceImpl(mockRateService, mockRepository);
    }

    @Test
    void listAll_happyPath() {

        // arrange
        List<Currency> mockResponse = new ArrayList<>();
        mockResponse.add(Currency.builder().code("EUR").numOfRequests(0).build());
        mockResponse.add(Currency.builder().code("USD").numOfRequests(0).build());
        mockResponse.add(Currency.builder().code("CZK").numOfRequests(0).build());

        when(mockRepository.getAll()).thenReturn(mockResponse);

        // action
        List<Currency> result = testObj.listAll();

        // assert
        verify(mockRepository, times(1)).getAll();

        assertEquals(3, result.size());
        assertEquals("EUR", result.get(0).getCode());
        assertEquals("USD", result.get(1).getCode());
        assertEquals("CZK", result.get(2).getCode());
    }

    @Test
    void convertAmount_happyPath() {

        // arrange
        Rate mockResponse = Rate.builder().from("EUR").to("USD").rate(1.2).build();

        when(mockRateService.getRateForCurrencyPair("EUR", "USD")).thenReturn(mockResponse);

        // action
        Amount result = testObj.convertAmount(15.0, "EUR", "USD");

        // assert
        verify(mockRateService, times(1)).getRateForCurrencyPair("EUR", "USD");

        assertEquals(18.0, result.getValue());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void convertAmount_whenCurrencyPairIsNotValid_shouldReturnNull() {

        // arrange
        when(mockRateService.getRateForCurrencyPair("WRONG", "CURRENCY")).thenReturn(null);

        // action
        Amount result = testObj.convertAmount(15.0, "WRONG", "CURRENCY");

        // assert
        verify(mockRateService, times(1)).getRateForCurrencyPair("WRONG", "CURRENCY");

        assertNull(result);
    }

    @Test
    void updateCurrencies_happyPath() {

        // arrange
        List<Currency> input = new ArrayList<>();

        Currency c1 = Currency.builder().code("NEW").numOfRequests(0).build();
        Currency c2 = Currency.builder().code("EUR").numOfRequests(5).build();
        Currency c3 = Currency.builder().code("USD").numOfRequests(5).build();

        input.add(c1);
        input.add(c2);
        input.add(c3);

        when(mockRepository.getCurrencyByCode("NEW")).thenReturn(null);
        when(mockRepository.getCurrencyByCode("EUR")).thenReturn(c2);
        when(mockRepository.getCurrencyByCode("USD")).thenReturn(c3);

        // action
        Boolean result = testObj.updateCurrencies(input);

        // assert
        verify(mockRepository, times(1)).createCurrency(any());

        assertTrue(result);
    }

    @Test
    void incrementNumOfRequests_happyPath() {
        // arrange
        String inputFrom = "EUR";
        String inputTo = "USD";

        Currency cFrom = Currency.builder().code(inputFrom).numOfRequests(0).build();
        Currency cTo = Currency.builder().code(inputTo).numOfRequests(0).build();

        when(mockRepository.getCurrencyByCode(inputFrom)).thenReturn(cFrom);
        when(mockRepository.getCurrencyByCode(inputTo)).thenReturn(cTo);

        // action
        testObj.incrementNumOfRequests(inputFrom, inputTo);

        // assert
        verify(mockRepository, times(1)).updateCurrency(cFrom);
        verify(mockRepository, times(1)).updateCurrency(cTo);
    }

    @Test
    void incrementNumOfRequests_whenOneCurrencyDoesNotExist_shouldNotCallTheUpdateMethod() {
        // arrange
        String inputFrom = "EUR";
        String inputTo = "USD";

        Currency cTo = Currency.builder().code(inputTo).numOfRequests(0).build();

        when(mockRepository.getCurrencyByCode(inputFrom)).thenReturn(null);
        when(mockRepository.getCurrencyByCode(inputTo)).thenReturn(cTo);

        // action
        testObj.incrementNumOfRequests(inputFrom, inputTo);

        // assert
        verify(mockRepository, times(0)).updateCurrency(any());
    }

    @Test
    void incrementNumOfRequests_whenBothCurrenciesDoNotExist_shouldNotCallTheUpdateMethod() {
        // arrange
        String inputFrom = "EUR";
        String inputTo = "USD";

        when(mockRepository.getCurrencyByCode(inputFrom)).thenReturn(null);
        when(mockRepository.getCurrencyByCode(inputTo)).thenReturn(null);

        // action
        testObj.incrementNumOfRequests(inputFrom, inputTo);

        // assert
        verify(mockRepository, times(0)).updateCurrency(any());
    }

}
