package com.example.crypto.klineservice.service;

import com.example.crypto.klineservice.model.DataLimit;
import com.example.crypto.klineservice.model.Interval;
import com.example.crypto.klineservice.model.Kline;
import com.example.crypto.klineservice.repo.KlineRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Service
public class KlineService {
    // component, manage by Spring container
    @Autowired
    private KlineRepo klineRepo;

    @Autowired
    BinanceService binanceService;

    @Value("${default_interval}")
    private String defaultIntervalStr;

    @Value("${default_data_limit}")
    private String defaultDataLimitStr;

    @Value("${one_min_in_millisecond}")
    private Integer ONE_MIN_TO_MILLS;

    private Interval DEFAULT_INTERVAL;
    private DataLimit DEFAULT_DATA_LIMIT;

    @PostConstruct
    public void init() {
        this.DEFAULT_INTERVAL = Interval.fromCode(defaultIntervalStr);
        this.DEFAULT_DATA_LIMIT = DataLimit.valueOf(defaultDataLimitStr);
    }

    /**
     * return a list of Kline object for response to
     * @param openTime long for example, 1735689600000 is 2025-01-01 0:0:0 unix timestamp in ms
     * @param closeTime long for example, 1735693200000 is 2025-01-01 1:0:0  unix timestamp in ms
     * @param symbol for example, "BTCUSDT" is for Bitcon
     * @param freq for example, "5m" will aggregate 5 1-min kline
     * @return a list of Kline objs
     */
    public List<Kline> getKlineByPrimaryKey(String symbol, Long openTime, Long closeTime, Interval freq) {
        // MAX NUMBER SPLIT
        List<Kline> klinesList = klineRepo.findBySymbolAndTime(symbol, openTime, closeTime);

        int endIndex = klinesList.size();
        return IntStream.range(0, endIndex)
                .parallel()
                .filter(s -> s % freq.getMinutes() == 0)
                .mapToObj(s -> aggregatePerBatch(klinesList, s, Math.min(s + freq.getMinutes() - 1, endIndex - 1)))
                //.sorted()
                .toList();
    }

    /**
     * aggregate all Kline obj in the given range to 1 Kline obj
     * @param klinesList Kline lists
     * @param startIndex index to retrieve the first Kline obj
     * @param endIndex index to retrieve the last Kline obj
     * @return 1 aggregate Kline obj
     */
    private Kline aggregatePerBatch(List<Kline> klinesList, int startIndex, int endIndex) {
        Kline firstKline = klinesList.get(startIndex);
        Kline lastKline = klinesList.get(endIndex);
        Kline res = new Kline();
        res.setSymbol(firstKline.getSymbol());
        // After aggregate, we have update the field of the Kline obj
        res.setClosePrice(lastKline.getClosePrice());
        res.setCloseTime((lastKline.getCloseTime()));
        res.setOpenPrice(firstKline.getOpenPrice());
        res.setOpenTime(firstKline.getOpenTime());

        BigDecimal highPrice = firstKline.getHighPrice();
        BigDecimal lowPrice = firstKline.getLowPrice();
        BigDecimal volume = firstKline.getVolume();
        BigDecimal quoteAssetVolume = firstKline.getQuoteAssetVolume();
        int numberOfTrades = firstKline.getNumberOfTrades();

        // start from the second Kline object
        startIndex = startIndex + 1;
        while (startIndex < endIndex) {
            Kline currKline = klinesList.get(startIndex);

            // compare the highPrice
            if (currKline.getHighPrice().compareTo(highPrice) < 0) {
                highPrice = currKline.getHighPrice();
            }

            // compare the lowPrice
            if (currKline.getLowPrice().compareTo(lowPrice) > 0) {
                highPrice = currKline.getLowPrice();
            }

            // add all kline's volume together
            volume = volume.add(currKline.getVolume());

            // add all Kline's quoteAssetVolume together
            quoteAssetVolume = quoteAssetVolume.add(currKline.getQuoteAssetVolume());

            // add all Kline's trades together
            numberOfTrades += currKline.getNumberOfTrades();

            startIndex += 1;
        }

        return res.setVolume(volume)
                .setQuoteAssetVolume(quoteAssetVolume)
                .setLowPrice(lowPrice)
                .setHighPrice(highPrice)
                .setNumberOfTrades(numberOfTrades);

    }


    /**
     * fetch kline and insert them into the database
     * @param startTime long for example, 1735689600000 is 2025-01-01 0:0:0 unix timestamp in ms
     * @param endTime long for example, 1735693200000 is 2025-01-01 1:0:0  unix timestamp in ms
     * @param symbol for example, "BTCUSDT" is for Bitcon
     * @param exchangeInfo information that tell us which service to call
     */
    public void loadKlines(String symbol, Long startTime, Long endTime, Enum exchangeInfo) {
        int maximumTime = DEFAULT_DATA_LIMIT.getValue() * ONE_MIN_TO_MILLS;
        LongStream.range(startTime, endTime)
                .parallel()
                .filter(s-> (s-startTime)%maximumTime == 0)
                .forEach(s->{
                    var tempEndTime = Math.min(s + maximumTime, endTime);
                    var res = binanceService.fetchKline(symbol, s, tempEndTime, DEFAULT_INTERVAL);
                    klineRepo.batchInsert(res);
                });
    }

}
