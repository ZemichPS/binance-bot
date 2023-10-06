package by.zemich.binancebot.core.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BarDto(
        LocalDateTime openTime,
        BigDecimal openPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal closePrice,
        BigDecimal volumePrice,
        LocalDateTime closeTime,
        BigDecimal quoteAssetVolume,
        Integer numberOfTrades,
        BigDecimal takerBuyBaseAssetVolume,
        BigDecimal takerBuyQuoteAssetVolume) {}
