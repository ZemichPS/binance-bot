package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.util.List;
@Data
public class ExchangeInfoQueryDto {
    private String symbol;
    private List<String> symbols;
    private List<String> permissions;

}
