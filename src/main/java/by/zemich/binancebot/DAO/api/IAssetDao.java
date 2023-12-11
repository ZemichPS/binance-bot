package by.zemich.binancebot.DAO.api;

import by.zemich.binancebot.core.dto.binance.Asset;

import java.util.List;

public interface IAssetDao {

    Asset getBySymbol(String symbol);

    List<Asset> getListToUSDTForSpotTrading();
}
