package com.example.crypto.klineservice.service;

import com.example.crypto.klineservice.model.exception.InputInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InputValidationServiceTest {
    private InputValidationService validationService;
    private BinanceService binanceService;

    @BeforeEach
    public void setUp() {
        binanceService = mock(BinanceService.class);
        validationService = new InputValidationService();
        validationService.setBinanceService(binanceService);
    }

    @Test
    public void testValidateTimeRange_validTimeRange() {
        assertDoesNotThrow(() -> validationService.validateTimeRange(100L, 200L));
    }

    @Test
    public void testValidateTimeRange_invalidTimeRange() {
        assertThrows(InputInvalidException.class, () -> validationService.validateTimeRange(200L, 100L));
    }

    @Test
    public void testValidateSymbol_validCase() {
        when(binanceService.isValidSymbol("BTCUSDT")).thenReturn(true);
        assertDoesNotThrow(() -> validationService.validateSymbol("BTCUSDT"));
    }

    @Test
    public void testValidateSymbol_invalidCase() {
        when(binanceService.isValidSymbol("Felix")).thenReturn(false);
        assertThrows(InputInvalidException.class, () -> validationService.validateSymbol("Felix"));
    }
}
