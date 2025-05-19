package com.example.crypto.klineservice.service;

import com.example.crypto.klineservice.model.Interval;
import com.example.crypto.klineservice.model.Kline;
import com.example.crypto.klineservice.repo.KlineRepo;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BinanceService {

    protected Set<String> validSymbols = new HashSet<>();

    @Value("${binance_kline_api_url}")
    private String klines_url;

    @Value("${binance_symbol_api_url}")
    private String exchange_info_url;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KlineRepo klineRepo;

    @PostConstruct
    public void postInit(){
        loadAllSymbols();
    }

    /**
     * helper method to load all symbols by calling the binance API
     */
    private void loadAllSymbols() {
        // return the response as JsonNode then extract to get the "symbol" information
        // for every crypto that in binance
        JsonNode root = restTemplate.getForObject(exchange_info_url, JsonNode.class);
        if (root != null && root.has("symbols")) {
            for (JsonNode node : root.path("symbols")) {
                JsonNode symbolNode = node.get("symbol");
                if (symbolNode != null) {
                    validSymbols.add(symbolNode.asText());
                }
            }
        }
    }

    public Boolean isValidSymbol(String symbol){
        return validSymbols.contains(symbol);
    }

    /** insert kline datas to database based on the provided symbol and intervals
     * @param startTime long for example, 1735689600000 is 2025-01-01 0:0:0 unix timestamp in ms
     * @param endTime long for example, 1735693200000 is 2025-01-01 1:0:0  unix timestamp in ms
     * @param symbol for example, "BTCUSDT" is for Bitcon
     * @param interval an Enum, include the interval we need to fetch the kline
     * @return a list of kline that fetch from the klines_url
     */
    public List<Kline> fetchKline(String symbol, Long startTime, Long endTime, Interval interval) {
        String resourceUrl = String.format(klines_url, symbol, interval.getCode(), startTime, endTime);
        ResponseEntity<String[][]> response = restTemplate.getForEntity(resourceUrl, String[][].class);
        String[][] klineData = response.getBody();
        return Arrays.stream(klineData)
                .parallel()
                .map(data -> this.convert(data, symbol))
                .toList();
    }

    /**
     * Convert klineInfo to a Kline object
     * @param klineInfo a string array with associate with a Kline obj
     * @param symbol symbol for this Kline obj
     * @return a Kline object
     */
    private Kline convert(String[] klineInfo, String symbol){
        return new Kline()
                .setSymbol(symbol)
                .setOpenTime(Long.parseLong(klineInfo[0]))
                .setOpenPrice(new BigDecimal(klineInfo[1]))
                .setHighPrice(new BigDecimal(klineInfo[2]))
                .setLowPrice(new BigDecimal(klineInfo[3]))
                .setClosePrice(new BigDecimal(klineInfo[4]))
                .setVolume(new BigDecimal(klineInfo[5]))
                .setCloseTime(Long.parseLong(klineInfo[6]))
                .setQuoteAssetVolume(new BigDecimal(klineInfo[7]))
                .setNumberOfTrades(Integer.parseInt(klineInfo[8]));
    }
}
