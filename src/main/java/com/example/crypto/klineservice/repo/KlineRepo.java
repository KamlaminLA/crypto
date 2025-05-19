package com.example.crypto.klineservice.repo;
import com.example.crypto.klineservice.model.Kline;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Mapper
public interface KlineRepo {
    @Select("SELECT * FROM klines WHERE open_time >= #{openTime} AND close_time <= #{closeTime} AND symbol = #{symbol}")
    public @NotEmpty List<@Valid @NotNull Kline> findBySymbolAndTime(String symbol, Long openTime, Long closeTime);

    @Delete("DELETE FROM klines WHERE open_time = #{openTime} AND close_time = #{closeTime} AND symbol = #{symbol}")
    public int deleteById(@NotNull String symbol, @NotNull Long openTime, @NotNull Long closeTime);

    @Insert("INSERT INTO klines(open_time, close_time, open_price, close_price, high_price, low_price, volume, " +
            "quote_asset_volume, number_of_trades, symbol) " +
            " VALUES (#{openTime}, #{closeTime}, #{openPrice}, #{closePrice}, #{highPrice}, #{lowPrice}, " +
            "#{volume}, #{quoteAssetVolume}, #{numberOfTrades}, #{symbol})")
    public int insert(@NotNull @Valid Kline kline);

    @Update("UPDATE klines SET open_time=#{openTime}, close_time=#{closeTime}, open_price=#{openPrice}, close_price=#{closePrice}," +
            "high_price=#{highPrice}, low_price=#{lowPrice}, volume=#{volume}, quote_asset_volume=#{quoteAssetVolume}, number_of_trades=#{numberOfTrades}," +
            "symbol=#{symbol} WHERE open_time = #{openTime} AND close_time = #{closeTime} AND symbol = #{symbol}")
    public int update(@NotNull @Valid Kline kline);

    @Insert({
            "<script>",
            "INSERT INTO klines (open_time, close_time, open_price, close_price, high_price, low_price, volume, " +
                    "quote_asset_volume, number_of_trades, symbol)",
            "VALUES",
            "<foreach  collection='klinesList' item='kline' separator=','>",
            "( #{kline.openTime}, #{kline.closeTime}, #{kline.openPrice}, #{kline.closePrice}, #{kline.highPrice}, #{kline.lowPrice}, " +
                    "#{kline.volume}, #{kline.quoteAssetVolume}, #{kline.numberOfTrades}, #{kline.symbol})",
            "</foreach>",
            "</script>"
    })
    public void batchInsert(List<@NotNull @Valid Kline> klinesList);
}
