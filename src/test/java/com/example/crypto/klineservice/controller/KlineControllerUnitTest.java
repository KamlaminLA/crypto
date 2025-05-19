package com.example.crypto.klineservice.controller;

import com.example.crypto.klineservice.model.Interval;
import com.example.crypto.klineservice.model.Kline;
import com.example.crypto.klineservice.service.BinanceService;
import com.example.crypto.klineservice.service.InputValidationService;
import com.example.crypto.klineservice.service.KlineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KlineControllerUnitTest {

    @Mock
    private KlineService klineService;

    @Mock
    private InputValidationService validationService;

    @Mock
    private BinanceService binanceService;

    @InjectMocks
    private KlineController klineController;

    @Test
    public void test_get() {
        // Arrange
        Kline kline = new Kline();
        kline.setSymbol("BTCUSDT");
        kline.setOpenTime(1000L);
        kline.setCloseTime(2000L);
        kline.setOpenPrice(new BigDecimal("100"));
        kline.setClosePrice(new BigDecimal("110"));

        when(klineService.getKlineByPrimaryKey("BTCUSDT", 1000L, 2000L, Interval.FIVE_MINUTES))
                .thenReturn(List.of(kline));

        // Act
        List<Kline> result = klineController.getKlines(1000L, 2000L, "BTCUSDT", "5m");

        // Assert
        assertEquals(1, result.size());
        assertEquals("BTCUSDT", result.get(0).getSymbol());
        assertEquals(new BigDecimal("100"), result.get(0).getOpenPrice());

        // verify methods are called
        verify(validationService).validateTimeRange(1000L, 2000L);
        verify(validationService).validateSymbol("BTCUSDT");
        verify(klineService).getKlineByPrimaryKey("BTCUSDT", 1000L, 2000L, Interval.FIVE_MINUTES);
    }

    @Test
    public void test_load() {
        // Arrange
        String symbol = "BTCUSDT";
        Long startTime = 1000L;
        Long endTime = 2000L;

        // No need to mock return values — just verify they don’t throw
        doNothing().when(validationService).validateTimeRange(startTime, endTime);
        doNothing().when(validationService).validateSymbol(symbol);
        doNothing().when(klineService).loadKlines(symbol, startTime, endTime, null);

        // Act
        ResponseEntity response = klineController.insertKlineByRange(symbol, startTime, endTime);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // verify methods are called
        verify(validationService).validateTimeRange(startTime, endTime);
        verify(validationService).validateSymbol(symbol);
        verify(klineService).loadKlines(symbol, startTime, endTime, null);
    }
}

