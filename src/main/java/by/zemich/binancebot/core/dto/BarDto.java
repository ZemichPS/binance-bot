package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;



@Data
public class BarDto{
        private Timestamp openTime;
        private BigDecimal openPrice;
        private BigDecimal highPrice;
        private BigDecimal lowPrice;
        private BigDecimal closePrice;
        private BigDecimal volume;
        private Timestamp closeTime;
        private BigDecimal quoteAssetVolume;
        private Integer numberOfTrades;
        private BigDecimal takerBuyBaseAssetVolume;
        private BigDecimal takerBuyQuoteAssetVolume;

}
