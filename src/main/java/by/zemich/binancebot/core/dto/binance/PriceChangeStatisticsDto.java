package by.zemich.binancebot.core.dto.binance;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PriceChangeStatisticsDto {
    private String symbol;
    private BigDecimal priceChange;
    private double priceChangePercent;
    private BigDecimal weightedAvgPrice;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal lastPrice;
    private BigDecimal volume;
    private BigDecimal quoteVolume;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Integer firstId;
    private Integer lastId;
    private Integer count;
}
