package by.zemich.binancebot.core.dto.binance;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceDto {
    private String asset;
    private BigDecimal free;
    private BigDecimal locked;

}
