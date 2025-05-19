package com.example.crypto.klineservice.service;

import com.example.crypto.klineservice.model.Interval;
import com.example.crypto.klineservice.model.Kline;
import com.example.crypto.klineservice.repo.KlineRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BinanceServiceTest {

    @InjectMocks
    private BinanceService binanceService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KlineRepo klineRepo;

    @BeforeEach
    void setUp() throws Exception {
        // Manually injecting values into @Value fields (they aren't auto-injected in unit tests)
        Field klineUrlField = BinanceService.class.getDeclaredField("klines_url");
        klineUrlField.setAccessible(true);
        klineUrlField.set(binanceService, "https://www.binance.com/api/v3/klines?symbol=%s&interval=%s&startTime=%d&endTime=%d");

        Field symbolUrlField = BinanceService.class.getDeclaredField("exchange_info_url");
        symbolUrlField.setAccessible(true);
        symbolUrlField.set(binanceService, "https://www.binance.com/api/v3/exchangeInfo");
    }

    /**
     * Test for isValidSymbol(symbol)
     * This test manually adds a valid symbol to the internal set and checks that:
     * - isValidSymbol returns true for it
     * - returns false for a symbol not in the set
     */
    @Test
    public void testIsValidSymbol_shouldReturnTrue() {
        // Simulate symbol loaded into the service (skip real API call)
        binanceService.validSymbols.add("BTCUSDT");

        // This should return true because we manually added it
        assertTrue(binanceService.isValidSymbol("BTCUSDT"));

        // This should return false because it wasn't added
        assertFalse(binanceService.isValidSymbol("DOGEADA"));
    }

    /**
     * Test for fetchKline()
     * This simulates the Binance API returning two kline entries.
     * It verifies:
     * - The correct endpoint URL is constructed and called
     * - The returned 2D array is properly converted into a list of Kline objects
     */
    @Test
    public void testFetchKline_shouldReturnKlineList() {
        // Mock response from Binance API: 2 klines worth of data in string[][] form
        String[][] mockResponse = {
                {"1735689600000", "93576.00000000",
                        "93610.93000000",
                        "93537.50000000",
                        "93610.93000000",
                        "8.21827000",
                        "1735689659999", "768978.75522470",
                        "2631", "3.95157000",
                        "369757.32652890",
                        "0"},
                {"1735689660000", "93610.93000000",
                        "93652.00000000",
                        "93606.20000000",
                        "93652.00000000",
                        "12.14029000",
                        "1735689719999", "1136550.56817500",
                        "1273", "4.08887000",
                        "382791.50017200",
                        "0"}
        };

        // The exact URL BinanceService should build and call
        String expectedUrl = "https://www.binance.com/api/v3/klines?symbol=BTCUSDT&interval=1m&startTime=1735689600000" +
                "&endTime=1735689719999";

        // When restTemplate.getForEntity is called with that URL, return our mock data
        when(restTemplate.getForEntity(expectedUrl, String[][].class))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // Call the method we're testing
        List<Kline> result = binanceService.fetchKline("BTCUSDT", 1735689600000L, 1735689719999L, Interval.ONE_MINUTE);

        // Validate the size and contents of the returned list
        assertEquals(2, result.size());

        Kline first = result.get(0);
        assertEquals(new BigDecimal("93576.00000000"), first.getOpenPrice()); // Open price from mock data
        assertEquals("BTCUSDT", first.getSymbol());                // Symbol passed in
        assertEquals(2631, first.getNumberOfTrades());               // Trades from mock data
    }
}

