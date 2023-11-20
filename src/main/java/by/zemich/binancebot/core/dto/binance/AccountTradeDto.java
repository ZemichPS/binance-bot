package by.zemich.binancebot.core.dto.binance;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountTradeDto {
    private String symbol;
    private Long id;
    private Long orderId;
    private Long orderListId;
    private BigDecimal price;
    private BigDecimal qty;
    private BigDecimal quoteQty;
    private BigDecimal commission;
    private String commissionAsset;
    private Long time;
    private boolean isBuyer;
    private boolean isMaker;
    private boolean isBestMatch;


}