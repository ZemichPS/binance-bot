package by.zemich.binancebot.core.dto;

import java.math.BigDecimal;

public record BarRangeDto(Integer firstBarIndex, Integer lastBarIndex, BigDecimal percentage) {}
