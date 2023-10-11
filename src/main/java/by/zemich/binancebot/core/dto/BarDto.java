package by.zemich.binancebot.core.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;


public record BarDto(
        Timestamp openTime,
        BigDecimal openPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal closePrice,
        BigDecimal volume,
        Timestamp closeTime,
        BigDecimal quoteAssetVolume,
        Integer numberOfTrades,
        BigDecimal takerBuyBaseAssetVolume,
        BigDecimal takerBuyQuoteAssetVolume) {}
