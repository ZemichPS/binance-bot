package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
public class ExchangeInfoDto {
    private String timezone;
    private LocalDateTime serverTime;
    private String futuresType;
    private List<RateLimitDto> rateLimits;
    private List<Objects> exchangeFilters;
    private List<AssetDto> assets;
    private List<SymbolDto> symbols;




}
