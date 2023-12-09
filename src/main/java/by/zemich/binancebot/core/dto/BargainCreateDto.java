package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.dto.binance.SymbolDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BargainCreateDto {
    private String strategy;
    private SymbolDto symbol;
}
