package by.zemich.binancebot.DAO.impl;

import by.zemich.binancebot.DAO.api.IAssetDao;
import by.zemich.binancebot.core.dto.EventDto;
import by.zemich.binancebot.core.dto.binance.Asset;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.service.api.INotifier;
import by.zemich.binancebot.service.api.IStockMarketService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class IAssetDaoImpl implements IAssetDao {
    private final String MESSAGE_ERROR = "Failed to update asset list";
    private final IStockMarketService stockMarketService;
    private final INotifier notifier;
    private final List<Asset>  assetList = new ArrayList();

    public IAssetDaoImpl(IStockMarketService stockMarketService, INotifier notifier) {
        this.stockMarketService = stockMarketService;
        this.notifier = notifier;
    }

    @Override
    public Asset getBySymbol(String symbol) {

        return assetList.stream()
                .filter(asset -> asset.getSymbol().equals(symbol))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public List<Asset> getListToUSDTForSpotTrading() {
        return assetList.stream()
                .filter(symbolDto -> symbolDto.getStatus().equals("TRADING"))
                .filter(symbolDto -> symbolDto.getQuoteAsset().equals("USDT"))
                .toList();
    }


    @Scheduled(fixedDelay = 6_00_000, initialDelay = 1_000)
    private void updateAssetList() {
        List<Asset> receivedAssetList = stockMarketService.getSymbols().orElseThrow(RuntimeException::new);

        if(receivedAssetList.isEmpty() ) {
            EventDto eventDto = EventDto.builder()
                    .eventType(EEventType.ERROR)
                    .text(MESSAGE_ERROR)
                    .build();

            notifier.notify(eventDto);
            throw new RuntimeException(MESSAGE_ERROR);
        }
        assetList.clear();
        assetList.addAll(receivedAssetList);
    }
}
