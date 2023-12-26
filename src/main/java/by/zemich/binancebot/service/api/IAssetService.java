package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.binance.Asset;

import java.math.BigDecimal;
import java.util.List;

public interface IAssetService {
    Asset getBySymbol(String symbol);

    List<Asset> getListToUSDTForSpotTrading();


}
