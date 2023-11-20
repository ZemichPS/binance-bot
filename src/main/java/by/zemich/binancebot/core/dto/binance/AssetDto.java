package by.zemich.binancebot.core.dto.binance;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssetDto {
    private String asset;
    private boolean marginAvailable;
    private BigDecimal autoAssetExchange;


}
