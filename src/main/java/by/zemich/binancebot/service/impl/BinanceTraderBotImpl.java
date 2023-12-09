package by.zemich.binancebot.service.impl;


import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.KlineQueryDto;
import by.zemich.binancebot.core.dto.binance.SymbolDto;
import by.zemich.binancebot.core.enums.*;
import by.zemich.binancebot.service.api.*;
import com.binance.connector.client.exceptions.BinanceClientException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;


import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;


@Service
@EnableScheduling
@Log4j2
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final ITradeManager tradeManager;
    private final INotifier notifier;
    private final IEventManager eventManager;
    private final IBargainService bargainService;
    private final ConversionService conversionService;
    private final IIndicatorReader indicatorReader;
    private final Map<String, IStrategy> strategyMap = new HashMap<>();
    private final List<String> blackList = new ArrayList<>();
    private Integer counter = 1;

    private final List<SymbolDto> symbolsList;

    public BinanceTraderBotImpl(IStockMarketService stockMarketService,
                                ITradeManager tradeManager,
                                INotifier notifier,
                                IEventManager eventManager,
                                IBargainService bargainService,
                                ConversionService conversionService, IIndicatorReader indicatorReader, List<SymbolDto> symbolsList) {
        this.stockMarketService = stockMarketService;
        this.tradeManager = tradeManager;
        this.notifier = notifier;
        this.eventManager = eventManager;
        this.bargainService = bargainService;
        this.conversionService = conversionService;
        this.indicatorReader = indicatorReader;
        this.symbolsList = symbolsList;

        blackList.add("BUSDUSDT");
        blackList.add("TUSDUSDT");
        blackList.add("USDTUSDT");
        blackList.add("TUSDTUSDT");
        blackList.add("USDCUSDT");
        blackList.add("BTTCUSDT");
        blackList.add("PEPEUSDT");

    }


    @Override
    public void registerStrategy(String name, IStrategy strategyManager) {
        strategyMap.put(name, strategyManager);
    }


    @Scheduled(fixedDelay = 10_000_000, initialDelay = 1_000)
    private void updateSymbols() {

        symbolsList.clear();
        stockMarketService.getSymbols().orElseThrow().stream()
                .filter(symbolDto -> symbolDto.getStatus().equals("TRADING"))
                .filter(symbolDto -> symbolDto.getQuoteAsset().equals("USDT"))
                .forEach(symbolsList::add);
    }

    @Scheduled(fixedDelay = 40_000, initialDelay = 5_000)
    @Override
    public void lookForEnterPosition() {

        KlineQueryDto queryDto = new KlineQueryDto();
        queryDto.setLimit(500);

        //TODO удалить
        if (counter <= 0) return;


        strategyMap.values().stream()
                .map(IStrategy::getInterval)
                .map(EInterval::toString)
                .distinct()
                .forEach(
                        stringInterval -> {
                            symbolsList.forEach(
                                    symbol -> {

                                        if (blackList.contains(symbol)) return;

                                        queryDto.setSymbol(symbol.getSymbol());
                                        queryDto.setInterval(stringInterval);

                                        BarSeries series = stockMarketService.getBarSeries(queryDto).orElseThrow(RuntimeException::new);

                                        if (series.getBarCount() < 500) return;

                                        strategyMap.values().stream()
                                                .filter(strategy -> strategy.getInterval().toString().equals(stringInterval))
                                                .forEach(strategy -> {
                                                    if (strategy.getEnterRule(series).isSatisfied(series.getEndIndex())) {

                                                        //TODO удалить
                                                        if (counter <= 0) return;

                                                        log.info(indicatorReader.getValues(series));

                                                        if (Objects.nonNull(strategy.getAdditionalStrategy())) {

                                                            IStrategy additionalStrategy = strategy.getAdditionalStrategy();
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

                                                        BargainCreateDto bargainCreateDto = BargainCreateDto.builder()
                                                                .strategy(strategy.getName())
                                                                .symbol(symbol)
                                                                .build();

                                                        BargainDto createdBargain = createBargain(bargainCreateDto);

                                                        if (Objects.nonNull(createdBargain)) {
                                                            EventDto eventDto = EventDto.builder()
                                                                    .eventType(EEventType.BARGAIN_WAS_CREATED)
                                                                    .text(MessageFormat.format("""
                                                                                    Bargain was created.
                                                                                    Asset: {0}
                                                                                    Strategy : {1},
                                                                                    """,
                                                                            createdBargain.getSymbol(),
                                                                            strategy.getName()))
                                                                    .build();

                                                            notifier.notify(eventDto);
                                                        }

                                                    }
                                                });
                                    });
                        });
    }


    @Scheduled(fixedDelay = 20_000, initialDelay = 5_000)
    // @Async
    @Override
    public void checkBargain() {

        //проверка на исполнение ордера на покупку
        bargainService.getAllWithFilledBuyOrders().ifPresent(
                entities -> {
                    entities.stream()
                            .map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                            .filter(Objects::nonNull)
                            .filter(bargainDto -> bargainDto.getBuyOrder() != null)
                            .forEach(bargainDto -> {

                                OrderDto buyOrderDto = bargainDto.getBuyOrder();
                                OrderDto sellOrderDto = tradeManager.createSellLimitOrder(buyOrderDto.getOrderId());
                                bargainDto.setSellOrder(sellOrderDto);
                                bargainDto.setStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED);
                                bargainService.update(bargainDto);

                                EventDto eventDto = EventDto.builder()
                                        .eventType(EEventType.SELL_LIMIT_ORDER)
                                        .text(String.format("Sell limit order was placed. Symbol: %s", sellOrderDto.getSymbol()))
                                        .build();

                                notifier.notify(eventDto);
                            });
                });


        //проверка на исполнение ордера на продажу
        bargainService.checkOnFinish().orElseThrow()
                .stream()
                .map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                .forEach(bargainDto -> {

                    BargainEntity finalizedBargainEntity = bargainService.finalize(bargainDto);
                    BargainDto finalizedBargainDto = conversionService.convert(finalizedBargainEntity, BargainDto.class);


                    EventDto eventDto = EventDto.builder()
                            .eventType(EEventType.ASSET_WAS_SOLD)
                            .text(MessageFormat.format("""
                                            Bargain was finished successfully.
                                            Asset: {0}
                                            Finance result: {1},
                                            Percentage result: {2}
                                            """, finalizedBargainDto.getSymbol(),
                                    finalizedBargainDto.getFinanceResult().setScale(3, RoundingMode.HALF_UP),
                                    finalizedBargainDto.getPercentageResult().setScale(3, RoundingMode.HALF_UP)))
                            .build();

                    notifier.notify(eventDto);

                });


        // установка временных результатов
      //  bargainService.setTemporaryResult();

        //проверка на окончание сделки

        //проверка на истёкший ордер
      /*  bargainService.getAllWithExpiredBuyOrders().

                ifPresent(bargainEntities ->

                {
                    bargainEntities.forEach(bargainEntity -> {
                        BargainDto bargainDto = conversionService.convert(bargainEntity, BargainDto.class);
                        bargainService.endByReasonExpired(bargainDto);
                    });

                });*/
    }

    private BargainDto createBargain(BargainCreateDto bargainCreateDto) {
        try {

            // TODO удалить
            counter = counter - 1;

            OrderDto buyOrder = tradeManager.createBuyLimitOrderByBidPrice(bargainCreateDto.getSymbol());

            BargainDto newBargain = bargainService.create(bargainCreateDto);
            bargainService.addBuyOrder(newBargain, buyOrder);
            newBargain.setStatus(EBargainStatus.OPEN_BUY_ORDER_CREATED);

            if (buyOrder.getStatus().equals(EOrderStatus.FILLED)) {
                OrderDto sellOrderDto = tradeManager.createSellLimitOrder(buyOrder.getOrderId());
                bargainService.addSellOrder(newBargain, sellOrderDto);
                newBargain.setStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED);
            }

            BargainEntity newBargainEntity = bargainService.save(newBargain).orElseThrow();
            return conversionService.convert(newBargainEntity, BargainDto.class);

        } catch (BinanceClientException binanceClientException) {
            log.error(binanceClientException.getErrMsg(), binanceClientException.getMessage());
            EventDto event = eventManager.get(EEventType.ERROR, binanceClientException);
            notifier.notify(event);
            return null;
        }

    }


}








