package by.zemich.binancebot.core.dto.binance;

import java.math.BigDecimal;

public record ChangePriceDTO(BigDecimal difference, BigDecimal percentage) {
}
