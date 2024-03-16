package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.dto.binance.Asset;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class BargainCreateDto {
    private String strategy;
    private Asset asset;
    private BigDecimal percentageAim;
}
