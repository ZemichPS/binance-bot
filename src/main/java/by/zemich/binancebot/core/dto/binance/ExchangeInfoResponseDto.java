package by.zemich.binancebot.core.dto.binance;

import lombok.Data;

import java.util.List;

@Data
public class ExchangeInfoResponseDto {
    private String timezone;
    private Long serverTime;
    private List<Asset> symbols;


}
