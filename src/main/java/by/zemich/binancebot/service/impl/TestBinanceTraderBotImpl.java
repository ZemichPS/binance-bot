package by.zemich.binancebot.service.impl;


import by.zemich.binancebot.DAO.api.IFakeOrderDao;
import by.zemich.binancebot.DAO.api.IMetricDao;
import by.zemich.binancebot.DAO.entity.FakeBargainEntity;
import by.zemich.binancebot.DAO.entity.MetricEntity;
import by.zemich.binancebot.config.properties.TestTradingProperties;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.KlineQueryDto;
import by.zemich.binancebot.core.dto.binance.SymbolPriceTickerDto;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.service.api.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


//@Component
@EnableScheduling
@Log4j2
public class TestBinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final INotifier notifier;
    private final TestTradingProperties testTradingProperties;

    private final IFakeOrderDao fakeOrderDao;

    private final IMetricDao metricDao;
    private final IBalanceManager balanceManager;
    private final IIndicatorReader indicatorReader;
    private final Map<String, IStrategy> stragegyMap = new HashMap<>();

    private final BigDecimal percentMakerFee = new BigDecimal("0.1");
    private final BigDecimal percentTakerFee = new BigDecimal("0.1");

    private final List<String> blackList = new ArrayList<>();



    public TestBinanceTraderBotImpl(IStockMarketService stockMarketService, INotifier notifier, TestTradingProperties testTradingProperties, IFakeOrderDao fakeOrderDao, IMetricDao metricDao, IBalanceManager balanceManager, IIndicatorReader indicatorReader) {
        this.stockMarketService = stockMarketService;
        this.notifier = notifier;
        this.testTradingProperties = testTradingProperties;
        this.fakeOrderDao = fakeOrderDao;
        this.metricDao = metricDao;
        this.balanceManager = balanceManager;
        this.indicatorReader = indicatorReader;

        blackList.add("BUSDUSDT");
        blackList.add("USDTUSDT");
        blackList.add("USDCUSDT");
        blackList.add("BTTCUSDT");
        blackList.add("PEPEUSDT");
    }


    @Override
    public void registerStrategy(String name, IStrategy strategyManager) {
        stragegyMap.put(name, strategyManager);
    }

    @Override
    public void checkBargain() {

    }


    @Scheduled(fixedDelay = 40_000, initialDelay = 1_000)
    @Override
    public void lookForEnterPosition() {
        log.info("Balance is: " + balanceManager.getBalance());

        if (!balanceManager.bargainPossible()) return;

        KlineQueryDto queryDto = new KlineQueryDto();
        queryDto.setLimit(500);

        List<String> symbolsList = stockMarketService.getSpotSymbols().orElseThrow();

        log.info("Количество элементов в списке:"  + symbolsList.size());


        stragegyMap.values().stream()
                .map(IStrategy::getInterval)
                .map(EInterval::toString)
                .distinct()
                .forEach(
                        stringInterval -> {
                            symbolsList.forEach(
                                    symbol -> {
                                        if (blackList.contains(symbol)) return;

                                        queryDto.setSymbol(symbol);
                                        queryDto.setInterval(stringInterval);

                                        BarSeries series = stockMarketService.getBarSeries(queryDto).orElseThrow(RuntimeException::new);

                                        if (series.getBarCount() < 500) return;

                                       // log.info(series.getName() + "\n" + indicatorReader.getValues(series).toString());
                                        //log.info(series.getName());

                                        stragegyMap.values().stream()
                                                .filter(iStrategy -> iStrategy.getInterval().toString().equals(stringInterval))
                                                .forEach(iStrategy -> {
                                                    if (iStrategy.getEnterRule(series).isSatisfied(series.getEndIndex())) {

                                                        if (Objects.nonNull(iStrategy.getAdditionalStrategy())) {

                                                            IStrategy additionalStrategy = iStrategy.getAdditionalStrategy();
                                                            EInterval intervalForAdditionalStrategy = additionalStrategy.getAdditionalStrategy().getInterval();
                                                            BarSeries additionalSeries = series;

                                                            if (!queryDto.getInterval().equals(intervalForAdditionalStrategy.toString())) {
                                                                queryDto.setInterval(intervalForAdditionalStrategy.toString());
                                                                additionalSeries = stockMarketService.getBarSeries(queryDto).orElseThrow(RuntimeException::new);
                                                            }

                                                            Rule additionalRule = additionalStrategy.getEnterRule(additionalSeries);
                                                            if (!additionalRule.isSatisfied(additionalSeries.getEndIndex()))
                                                                return;
                                                        }

                                                        IndicatorValuesDto indicatorValues = indicatorReader.getValues(series);
                                                        FakeBargainEntity createdFakeBargain = createFakeBargain(symbol, iStrategy.getName(), indicatorValues);
                                                        notifyToTelegram(createdFakeBargain, EEventType.ASSET_WAS_BOUGHT);
                                                    }
                                                });
                                    });
                        });
    }


    private FakeBargainEntity createFakeBargain(String symbol, String ruleName, IndicatorValuesDto indicatorValues) {
        BigDecimal assetPrice = getSymbolPrice(symbol);
        BigDecimal deposit = balanceManager.allocateFundsForTransaction();

        BigDecimal assetAmount = deposit.divide(assetPrice, 1, RoundingMode.DOWN);
        BigDecimal costsForAsset = assetAmount.multiply(assetPrice);

        BigDecimal makerFee = costsForAsset.divide(new BigDecimal("100")).multiply(this.percentMakerFee);

        BigDecimal totalCosts = costsForAsset.add(makerFee);

        BigDecimal differenceBetweenDepositAndCosts = deposit.subtract(totalCosts);

        if (differenceBetweenDepositAndCosts.doubleValue() > 0) {
            balanceManager.accumulateFounds(differenceBetweenDepositAndCosts);
        } else if (differenceBetweenDepositAndCosts.doubleValue() < 0) {
            balanceManager.allocateAdditionalFunds(differenceBetweenDepositAndCosts.abs());
        }

        FakeBargainEntity fakeOrderEntity = new FakeBargainEntity();
        fakeOrderEntity.setUuid(UUID.randomUUID());
        fakeOrderEntity.setBuyTime(LocalDateTime.now());
        fakeOrderEntity.setSymbol(symbol);
        fakeOrderEntity.setStatus(EOrderStatus.NEW);
        fakeOrderEntity.setBuyPrice(assetPrice);
        fakeOrderEntity.setAssetAmount(assetAmount);
        fakeOrderEntity.setMakerFee(makerFee);
        fakeOrderEntity.setTotalSpent(totalCosts);
        fakeOrderEntity.setStrategyName(ruleName);
        fakeOrderEntity.setIndicatorValue(indicatorValues.toString());

        FakeBargainEntity savedFakeOrderEntity =  fakeOrderDao.save(fakeOrderEntity);
        MetricEntity metricEntity = new MetricEntity();
        metricEntity.setUuid(UUID.randomUUID());
        metricEntity.setBargainUuid(savedFakeOrderEntity.getUuid());
        BeanUtils.copyProperties(indicatorValues, metricEntity);

        metricDao.save(metricEntity);

        blackList.add(symbol);

        return fakeOrderDao.save(fakeOrderEntity);
    }

    @Scheduled(fixedDelay = 30_000, initialDelay = 2_000)
    @Async
    public void checkOrder() {
        fakeOrderDao.findAllByStatus(EOrderStatus.NEW).ifPresent(list -> {
            list.forEach(this::check);
        });
    }

    private FakeBargainEntity check(FakeBargainEntity fakeOrderEntity) {
        synchronized (TestBinanceTraderBotImpl.class) {
            BigDecimal currentPrice = getSymbolPrice(fakeOrderEntity.getSymbol());
            BigDecimal assetAmount = fakeOrderEntity.getAssetAmount();
            BigDecimal spent = fakeOrderEntity.getTotalSpent();
            BigDecimal buyPrice = fakeOrderEntity.getBuyPrice();

            BigDecimal percentDifference = getPercentDifference(buyPrice, currentPrice);

            BigDecimal takerFee = currentPrice.multiply(assetAmount).divide(new BigDecimal("100")).multiply(percentTakerFee);

            BigDecimal currentFinanceResult = currentPrice.multiply(assetAmount).subtract(takerFee).subtract(spent);

            fakeOrderEntity.setPricePercentDifference(percentDifference);
            fakeOrderEntity.setCurrentFinanceResult(currentFinanceResult);
            fakeOrderEntity.setDuration(Duration.between(fakeOrderEntity.getBuyTime(),
                    LocalDateTime.now()).toMinutes());


            String ruleName = fakeOrderEntity.getStrategyName();
            BigDecimal goalPercent = stragegyMap.get(ruleName).getInterest();

            if (percentDifference.doubleValue() >= goalPercent.doubleValue()) {

                fakeOrderEntity.setTakerFee(takerFee);
                fakeOrderEntity.setFinanceResult(currentFinanceResult);
                fakeOrderEntity.setSellTime(LocalDateTime.now());
                fakeOrderEntity.setSellPrice(currentPrice);
                fakeOrderEntity.setStatus(EOrderStatus.SOLD);

                notifyToTelegram(fakeOrderEntity, EEventType.ASSET_WAS_SOLD);
                balanceManager.accumulateFounds(currentPrice.multiply(assetAmount.subtract(takerFee)));
                blackList.remove(fakeOrderEntity.getSymbol());
            }

            return fakeOrderDao.save(fakeOrderEntity);
        }
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








