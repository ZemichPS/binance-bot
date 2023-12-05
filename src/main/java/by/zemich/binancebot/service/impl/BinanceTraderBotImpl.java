package by.zemich.binancebot.service.impl;


import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.EventDto;
import by.zemich.binancebot.core.dto.IndicatorValuesDto;
import by.zemich.binancebot.core.dto.binance.KlineQueryDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.dto.binance.SymbolDto;
import by.zemich.binancebot.core.enums.*;
import by.zemich.binancebot.service.api.*;
import com.binance.connector.client.exceptions.BinanceClientException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;


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

        blackList.add("DODOUSDT");
        blackList.add("VETUSDT");



    }


    @Override
    public void registerStrategy(String name, IStrategy strategyManager) {
        strategyMap.put(name, strategyManager);
    }


    @Scheduled(fixedDelay = 10_000_000, initialDelay = 1_000)
    private void updateSymbols(){

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
                                                .filter(iStrategy -> iStrategy.getInterval().toString().equals(stringInterval))
                                                .forEach(iStrategy -> {
                                                    if (iStrategy.getEnterRule(series).isSatisfied(series.getEndIndex())) {

                                                        log.info(indicatorReader.getValues(series));

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

                                                        if (counter <= 0) return;

                                                        IndicatorValuesDto indicatorValues = indicatorReader.getValues(series);
                                                        createBargain(symbol);


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
            bargainService.checkOnFillBuyOrder().ifPresent(
                    entities -> {
                        entities.stream()
                                .map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                                .filter(Objects::nonNull)
                                .forEach(bargainDto -> {
                                    bargainDto.getOrders().stream()
                                            .filter(orderDto -> orderDto.getSide().equals(ESide.BUY))
                                            .findFirst()
                                            .ifPresent(buyOrderDto -> {
                                                OrderDto sellOrderDto = tradeManager.createSellLimitOrder(buyOrderDto.getOrderId());
                                                bargainDto.getOrders().add(sellOrderDto);
                                                bargainDto.setStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED);
                                                bargainService.update(bargainDto);

                                                EventDto eventDto = EventDto.builder()
                                                        .eventType(EEventType.SELL_LIMIT_ORDER)
                                                        .text("sell limit order was placed. Symbol: " + sellOrderDto.getSymbol())
                                                        .build();

                                                notifier.notify(eventDto);

                                            });
                                });
                    });


            // установка временных результатов
            bargainService.setTemporaryResult();

            //проверка на окончание сделки
            bargainService.checkOnFinish().ifPresent(bargainEntities -> {
                bargainEntities.forEach(bargainEntity -> {
                    BargainDto bargainDto = conversionService.convert(bargainEntity, BargainDto.class);
                    bargainService.end(bargainDto);
                });
            });

            //проверка на истёкший ордер
            bargainService.checkOnExpired().ifPresent(bargainEntities -> {
                bargainEntities.forEach(bargainEntity -> {
                    BargainDto bargainDto = conversionService.convert(bargainEntity, BargainDto.class);
                    bargainService.endByReasonExpired(bargainDto);
                });

            });
    }

    private void createBargain(SymbolDto symbol) {
        try {

            counter = counter - 1;

            OrderDto buyOrder = tradeManager.createBuyLimitOrderByBidPrice(symbol);


            BargainDto newBargain = new BargainDto();
            newBargain.setUuid(UUID.randomUUID());
            newBargain.setStatus(EBargainStatus.OPEN_BUY_ORDER_CREATED);
            List<OrderDto> orderDtoList = new ArrayList<>();
            orderDtoList.add(buyOrder);


            if (buyOrder.getStatus().equals(EOrderStatus.FILLED)) {
                OrderDto sellOrderDto = tradeManager.createSellLimitOrder(buyOrder.getOrderId());
                orderDtoList.add(sellOrderDto);
                newBargain.setStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED);
            }

            newBargain.setOrders(orderDtoList);

            newBargain.setSymbol(symbol.getSymbol());

            BargainEntity newBargainEntity = bargainService.create(newBargain).orElseThrow();
            BargainDto createdBargain = conversionService.convert(newBargainEntity, BargainDto.class);

            EventDto event = eventManager.get(EEventType.BARGAIN_WAS_CREATED, createdBargain);
            notifier.notify(event);


        } catch (BinanceClientException binanceClientException) {
            log.error(binanceClientException.getErrMsg(), binanceClientException.getMessage());
            EventDto event = eventManager.get(EEventType.ERROR, binanceClientException);

            notifier.notify(event);
        }
    }



}








