package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FillsDto {
    private BigDecimal price;
    private BigDecimal qty;
    private BigDecimal commission;
    private String commissionAsset;
    private Long tradeId;

}
