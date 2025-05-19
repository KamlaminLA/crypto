package com.example.crypto.klineservice.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Kline {

    @NotBlank(message = "Symbol must not be blank")
    private String symbol;

    @NotNull(message = "Open time is required")
    @Positive(message = "Open time must be positive")
    private Long openTime;

    @NotNull(message = "Open price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Open price must be greater than 0")
    private BigDecimal openPrice;

    @NotNull(message = "High price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "High price must be greater than 0")
    private BigDecimal highPrice;

    @NotNull(message = "Low price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Low price must be greater than 0")
    private BigDecimal lowPrice;

    @NotNull(message = "Close price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Close price must be greater than 0")
    private BigDecimal closePrice;

    @NotNull(message = "Volume is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Volume must be greater than 0")
    private BigDecimal volume;

    @NotNull(message = "Close time is required")
    @Positive(message = "Close time must be positive")
    private Long closeTime;

    @NotNull(message = "Quote Asset Volume is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quote Asset Volume must be greater than 0")
    private BigDecimal quoteAssetVolume;

    @NotNull(message = "Number of trades is required")
    @Min(value = 1, message = "Number of trades must be at least 1")
    private Integer numberOfTrades;
}
