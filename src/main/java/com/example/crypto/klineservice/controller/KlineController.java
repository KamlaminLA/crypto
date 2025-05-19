package com.example.crypto.klineservice.controller;

import com.example.crypto.klineservice.model.Interval;
import com.example.crypto.klineservice.model.Kline;
import com.example.crypto.klineservice.service.BinanceService;
import com.example.crypto.klineservice.service.InputValidationService;
import com.example.crypto.klineservice.service.KlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import java.math.BigDecimal;
// TODO LOG4J
@RestController
@RequestMapping("/kline")
public class KlineController {

    @Autowired
    private KlineService klineService;

    @Autowired
    private InputValidationService validationService;

    @Autowired
    private BinanceService binanceService;

    /**
     * return a list of Kline object for response to
     * the HTTP get method with request param
     * @param startTime long for example, 1735689600000 is 2025-01-01 0:0:0 unix timestamp in ms
     * @param endTime long for example, 1735693200000 is 2025-01-01 1:0:0  unix timestamp in ms
     * @param symbol for example, "BTCUSDT" is for Bitcon
     * @param freq for example, "5m" will aggregate 5 1-min kline
     * @return A list of Kline objs
     */
    @GetMapping()
    public List<Kline> getKlines(@RequestParam Long startTime,
                                 @RequestParam Long endTime,
                                 @RequestParam String symbol,
                                 @RequestParam String freq) {
        // we are given at most 100 kline per each time request
        Interval interval = Interval.fromCode(freq);
        endTime = Math.min(endTime, startTime + 100L * interval.getMinutes() * 60000);
        // enum
        validationService.validateTimeRange(startTime, endTime);
        validationService.validateSymbol(symbol);
        return klineService.getKlineByPrimaryKey(symbol, startTime, endTime, interval);
    }


    /**
     * response to the HTTP method to find a list of Kline object
     * in Binance Data Endpoints and insert into our own database
     * @param symbol for example, "BTCUSDT" is for Bitcon
     * @param startTime long for example, 1735689600000 is 2025-01-01 0:0:0 unix timestamp in ms
     * @param endTime long for example, 1735693200000 is 2025-01-01 1:0:0  unix timestamp in ms
     * @return
     */
    @PostMapping("/range") // Get/Post/Put/Delete Mapping
    public ResponseEntity insertKlineByRange(@RequestParam String symbol, @RequestParam Long startTime, @RequestParam Long endTime) {
        validationService.validateTimeRange(startTime, endTime);
        validationService.validateSymbol(symbol);
        klineService.loadKlines(symbol, startTime, endTime, null);

        return new ResponseEntity(HttpStatus.CREATED);

    }


}
