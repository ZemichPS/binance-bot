package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.api.IAssetDao;
import by.zemich.binancebot.core.dto.binance.Asset;
import by.zemich.binancebot.service.api.IAssetService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetServiceImpl implements IAssetService {
   private final IAssetDao assetDao;

    public AssetServiceImpl(IAssetDao assetDao) {
        this.assetDao = assetDao;
    }

    @Override
    public Asset getBySymbol(String symbol) {
        return assetDao.getBySymbol(symbol);
    }

    @Override
    public List<Asset> getListToUSDTForSpotTrading() {
        return assetDao.getListToUSDTForSpotTrading();
    }
}
