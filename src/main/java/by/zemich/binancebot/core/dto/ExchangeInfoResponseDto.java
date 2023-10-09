package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Data
public class ExchangeInfoResponseDto {
    private String timezone;
    private Long serverTime;
    private List<SymbolDto> symbols;

}
