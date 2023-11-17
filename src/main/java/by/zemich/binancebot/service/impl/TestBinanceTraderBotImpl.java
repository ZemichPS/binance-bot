package by.zemich.binancebot.service.impl;


import by.zemich.binancebot.DAO.api.IFakeOrderDao;
import by.zemich.binancebot.DAO.entity.FakeBargainEntity;
import by.zemich.binancebot.config.properties.TestTradingProperties;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.service.api.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


@Component
@EnableScheduling
@Log4j2
public class TestBinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final INotifier notifier;
    private final TestTradingProperties testTradingProperties;

    private final IFakeOrderDao fakeOrderDao;
    private final Map<String, IStrategy> strategyMap = new HashMap<>();

    private final IBalanceManager balanceManager;

    private final BigDecimal percentMakerFee = new BigDecimal("0.1");
    private final BigDecimal percentTakerFee = new BigDecimal("0.1");

    private final List<String> blackList = new ArrayList<>();


    public TestBinanceTraderBotImpl(IStockMarketService stockMarketService, INotifier notifier, TestTradingProperties testTradingProperties, IFakeOrderDao fakeOrderDao, IBalanceManager balanceManager) {
        this.stockMarketService = stockMarketService;
        this.notifier = notifier;
        this.testTradingProperties = testTradingProperties;
        this.fakeOrderDao = fakeOrderDao;
        this.balanceManager = balanceManager;

        blackList.add("BUSDUSDT");
        blackList.add("USDTUSDT");
    }


    @Override
    public void registerStrategy(String name, IStrategy strategyManager) {
        strategyMap.put(name, strategyManager);
    }

    @Override
    public void checkBargain() {

    }


    @Scheduled(fixedDelay = 40_000, initialDelay = 1_000)
    @Async
    @Override
    public void lookForEnterPosition() {
        log.info("Balance is: " + balanceManager.getBalance());

        if (!balanceManager.bargainPossible()) return;

        KlineQueryDto queryDto = new KlineQueryDto();
        queryDto.setLimit(500);


        stockMarketService.getSpotSymbols().ifPresentOrElse(
                symbolList -> {
                    symbolList.forEach(symbol -> {

                        if (blackList.contains(symbol)) return;

                        queryDto.setSymbol(symbol);
                        queryDto.setInterval("15m");
                        BarSeries series = stockMarketService.getBarSeries(queryDto).orElse(null);

                        if (series.getBarCount() < 400) return;

                        Strategy strategy = strategyMap.get("BOLLINGER_BAND_MAIN_STRATEGY").get(series);
                        if (strategy.shouldEnter(series.getEndIndex())) {
                            queryDto.setInterval("1h");
                            BarSeries secondSeries = stockMarketService.getBarSeries(queryDto).orElse(null);
                            Strategy sureStrategy = strategyMap.get("BOLLINGER_BAND_OLDER_TIMEFRAME_STRATEGY").get(secondSeries);
                       //     if (sureStrategy.shouldEnter(secondSeries.getEndIndex())) {
                            if (true) {
                                FakeBargainEntity createdFakeBargain = createFakeBargain(symbol);
                                notifyToTelegram(createdFakeBargain, EEventType.ENDPOINT_WAS_FOUNDED);
                                blackList.add(symbol);
                            }
                        }
                    });
                }, () -> log.warn("Symbol list is empty"));
    }

    @Scheduled(fixedDelay = 30_000, initialDelay = 2_000)
    @Async
    public void checkOrder() {
        fakeOrderDao.findAllByStatus(EOrderStatus.NEW).ifPresent(list -> {
            list.forEach(this::check);
        });
    }

    private FakeBargainEntity createFakeBargain(String symbol) {
        BigDecimal assetPrice = getSymbolPrice(symbol);
        BigDecimal deposit = balanceManager.allocateFundsForTransaction();
        BigDecimal makerFee = deposit.divide(new BigDecimal("100")).multiply(this.percentMakerFee);
        BigDecimal depositWithoutFee = deposit.subtract(makerFee);
        BigDecimal assetAmount = depositWithoutFee.divide(assetPrice, 1, RoundingMode.DOWN);


        FakeBargainEntity fakeOrderEntity = new FakeBargainEntity();
        fakeOrderEntity.setUuid(UUID.randomUUID());
        fakeOrderEntity.setBuyTime(LocalDateTime.now());
        fakeOrderEntity.setSymbol(symbol);
        fakeOrderEntity.setStatus(EOrderStatus.NEW);
        fakeOrderEntity.setBuyPrice(assetPrice);
        fakeOrderEntity.setAssetAmount(assetAmount);
        fakeOrderEntity.setMakerFee(makerFee);
        fakeOrderEntity.setSpendOnPurchase(deposit);

        return fakeOrderDao.save(fakeOrderEntity);
    }

    private FakeBargainEntity check(FakeBargainEntity fakeOrderEntity) {

        BigDecimal currentPrice = getSymbolPrice(fakeOrderEntity.getSymbol());
        BigDecimal assetAmount = fakeOrderEntity.getAssetAmount();
        BigDecimal spent = fakeOrderEntity.getSpendOnPurchase();
        BigDecimal buyPrice = fakeOrderEntity.getBuyPrice();

        BigDecimal percentDifference = getPercentDifference(buyPrice, currentPrice);
        BigDecimal currentFinanceResult = currentPrice.multiply(assetAmount).subtract(spent);

        fakeOrderEntity.setPricePercentDifference(percentDifference);
        fakeOrderEntity.setCurrentFinanceResult(currentFinanceResult);
        fakeOrderEntity.setDuration(Duration.between(fakeOrderEntity.getBuyTime(),
                LocalDateTime.now()).toMinutes());


        if (percentDifference.doubleValue() >= testTradingProperties.getGain().doubleValue()) {

            BigDecimal takerFee = currentFinanceResult.divide(new BigDecimal("100")).multiply(percentTakerFee);

            BigDecimal financeResultWithOutTakerFee = currentFinanceResult.subtract(takerFee);
            fakeOrderEntity.setTakerFee(takerFee);
            fakeOrderEntity.setFinanceResult(financeResultWithOutTakerFee);
            fakeOrderEntity.setSellTime(LocalDateTime.now());
            fakeOrderEntity.setSellPrice(currentPrice);
            fakeOrderEntity.setStatus(EOrderStatus.SOLD);

            notifyToTelegram(fakeOrderEntity, EEventType.ASSET_WAS_SOLD);
            balanceManager.accumulateFounds(financeResultWithOutTakerFee);
            blackList.remove(fakeOrderEntity.getSymbol());
        }

        return fakeOrderDao.save(fakeOrderEntity);
    }

    private BigDecimal getSymbolPrice(String symbol) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol);
        SymbolPriceTickerDto symbolPriceTicker = stockMarketService.getSymbolPriceTicker(params).orElseThrow();

        return symbolPriceTicker.getPrice();

    }

    private void notifyToTelegram(FakeBargainEntity fakeOrderEntity, EEventType eventType) {
        EventDto event = EventDto
                .builder()
                .eventType(eventType)
                .text("Symbol: " + fakeOrderEntity.getSymbol())
                .build();

        notifier.notify(event);

    }


    private BigDecimal getPercentDifference(BigDecimal buyPrice, BigDecimal sellPrice) {

        BigDecimal difference = sellPrice.subtract(buyPrice);

        BigDecimal resultPercent = difference.multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .divide(buyPrice, 2, RoundingMode.HALF_UP);

        return resultPercent;
    }

}








