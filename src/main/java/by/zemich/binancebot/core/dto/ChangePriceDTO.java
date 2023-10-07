package by.zemich.binancebot.core.dto;

import java.math.BigDecimal;

public record ChangePriceDTO(BigDecimal difference, BigDecimal percentage) {
}
