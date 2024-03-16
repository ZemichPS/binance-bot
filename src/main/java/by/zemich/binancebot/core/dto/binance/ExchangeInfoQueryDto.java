package by.zemich.binancebot.core.dto.binance;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class ExchangeInfoQueryDto {
    private String symbol;
    private List<String> symbols;
    private List<String> permissions;

}
