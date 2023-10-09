package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CommissionRateDto {
    private BigDecimal maker;
    private BigDecimal taker;
    private BigDecimal buyer;
    private BigDecimal seller;

}
