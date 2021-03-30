package com.scalable.capital.rates_api.rates.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.scalable.capital.rates_api.currencies.services.ICurrencyService;
import com.scalable.capital.rates_api.rates.data.IRateRepository;
import com.scalable.capital.rates_api.rates.models.Link;
import com.scalable.capital.rates_api.rates.models.Rate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RateServiceImplTest {

    @Mock
    private IRateRepository mockRepository;

    @Mock
    private ICurrencyService mockCurrencyService;

    @InjectMocks
    private RateServiceImpl testObj;

    @BeforeEach
    public void setup() {
        testObj = new RateServiceImpl(mockCurrencyService, mockRepository);
    }

    @Test
    void getRateForCurrencyPair_happyPath() {

        // arrange
        String inputFrom = "USD";
        String inputTo = "CZK";

        Rate mockRateResponse = Rate.builder().from(inputFrom).to(inputTo).rate(22.02).build();

        when(mockRepository.getRateByCurrencyPair(inputFrom, inputTo)).thenReturn(mockRateResponse);

        // action
        Rate result = testObj.getRateForCurrencyPair(inputFrom, inputTo);

        // assert
        verify(mockRepository, times(1)).getRateByCurrencyPair(inputFrom, inputTo);
        verify(mockRepository, times(0)).createRate(any());
        verify(mockCurrencyService, times(1)).incrementNumOfRequests(inputFrom, inputTo);

        assertEquals("USD", result.getFrom());
        assertEquals("CZK", result.getTo());
        assertEquals(22.02, result.getRate());
    }

    @Test
    void getRateForCurrencyPair__whenPairDoesNotExist_shouldCalculatePairAndReturnIt() {

        // arrange
        String inputFrom = "USD";
        String inputTo = "CZK";

        Rate mockUSDRateResponse = Rate.builder().from("EUR").to(inputFrom).rate(2.0).build();
        Rate mockCZKRateResponse = Rate.builder().from("EUR").to(inputTo).rate(44.04).build();

        when(mockRepository.getRateByCurrencyPair(inputFrom, inputTo)).thenReturn(null);
        when(mockRepository.getRateByCurrencyPair("EUR", inputFrom)).thenReturn(mockUSDRateResponse);
        when(mockRepository.getRateByCurrencyPair("EUR", inputTo)).thenReturn(mockCZKRateResponse);

        // action
        Rate result = testObj.getRateForCurrencyPair(inputFrom, inputTo);

        // assert
        verify(mockRepository, times(3)).getRateByCurrencyPair(any(), any());
        verify(mockRepository, times(1)).createRate(any());
        verify(mockCurrencyService, times(1)).incrementNumOfRequests(inputFrom, inputTo);

        assertEquals("USD", result.getFrom());
        assertEquals("CZK", result.getTo());
        assertEquals(22.02, result.getRate());
    }

    @Test
    void getChartLinkForCurrencyPair_happyPath() {

        // arrange
        String inputFrom = "USD";
        String inputTo = "EUR";

        Rate mockRateResponse = Rate.builder().from(inputFrom).to(inputTo).rate(22.02).build();

        when(mockRepository.getRateByCurrencyPair(inputFrom, inputTo)).thenReturn(mockRateResponse);

        // action
        Link result = testObj.getChartLinkForCurrencyPair(inputFrom, inputTo);

        // assert
        verify(mockRepository, times(1)).getRateByCurrencyPair(inputFrom, inputTo);
        verify(mockRepository, times(0)).createRate(any());
        verify(mockCurrencyService, times(1)).incrementNumOfRequests(inputFrom, inputTo);

        assertNotNull(result);
    }

    @Test
    void updateRates_happyPath() {

        // arrange
        List<Rate> inputRates = new ArrayList<>();
        List<Rate> mockResponseRates = new ArrayList<>();

        Rate r1 = Rate.builder().from("EUR").to("USD").rate(1.172).build();
        Rate r2 = Rate.builder().from("EUR").to("CZK").rate(26.0).build();
        Rate r3 = Rate.builder().from("EUR").to("JPY").rate(130.0).build();
        Rate r4 = Rate.builder().from("CZK").to("JPY").rate(5.2).build();

        inputRates.add(r1);
        inputRates.add(r2);
        inputRates.add(r3);

        mockResponseRates.add(r1);
        mockResponseRates.add(r2);
        mockResponseRates.add(r3);
        mockResponseRates.add(r4);

        when(mockRepository.getAll()).thenReturn(mockResponseRates);
        when(mockRepository.getRateByCurrencyPair("EUR", "CZK")).thenReturn(r2);
        when(mockRepository.getRateByCurrencyPair("EUR", "JPY")).thenReturn(r3);

        // action
        Boolean result = testObj.updateRates(inputRates);

        // assert
        verify(mockRepository, times(1)).getAll();
        verify(mockRepository, times(4)).updateRate(any());
        verify(mockRepository, times(2)).getRateByCurrencyPair(any(), any());
        verify(mockRepository, times(0)).deleteRate(any());

        assertTrue(result);
    }

}
