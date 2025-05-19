package com.example.crypto.klineservice.service;

import com.example.crypto.klineservice.model.exception.InputInvalidException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class InputValidationService {
    @Autowired
    private BinanceService binanceService;

    //checked exception vs runtime exception
    public void validateTimeRange(@NotNull @Min(0) Long startTime, @NotNull Long endTime){
        if(startTime > endTime){
            String msg = String.format("start time = %s, end time = %s. Start time must smaller than end time", startTime, endTime);
            throw new InputInvalidException(msg);
        }
    }

    public void validateSymbol(@NotBlank String symbol){
        if(!binanceService.isValidSymbol(symbol)){

            throw new InputInvalidException(symbol + " is not a valid symbol.");
        }
    }

    public void setBinanceService(BinanceService binanceService) {
        this.binanceService = binanceService;
    }
}
