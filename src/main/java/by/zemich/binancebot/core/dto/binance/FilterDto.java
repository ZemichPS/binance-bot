package by.zemich.binancebot.core.dto.binance;

import by.zemich.binancebot.core.enums.EFilterType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FilterDto {
    private EFilterType filterType;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private BigDecimal tickSize;
    private BigDecimal minQty;
    private BigDecimal maxQty;
    private BigDecimal stepSize;
    private Integer limit;
    private BigDecimal notional;
    private BigDecimal multiplierDecimal;
    private BigDecimal multiplierUp;
    private BigDecimal multiplierDown;




}
