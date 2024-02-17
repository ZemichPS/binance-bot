package by.zemich.binancebot.service.impl;


import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.config.properties.RealTradeProperties;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.KlineQueryDto;
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


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


@Service
@EnableScheduling
@Log4j2
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final ITradeManager tradeManager;
    private final INotifier notifier;
    private final IEventManager eventManager;
    private final BargainService bargainService;
    private final ConversionService conversionService;
    private final IIndicatorReader indicatorReader;
    private final IAssetService assetService;
    private final RealTradeProperties tradeProperties;
    private final Map<String, IStrategy> strategyMap = new HashMap<>();
    private final List<String> blackList = new ArrayList<>();

    // TODO удалить (счётчик разрешённых покупок актива)
    private Integer counter = 1_542_124;
    private boolean accountHasInsufficientBalance = true;

    private final String BALANCE_ERROR_MESSAGE = "Account has insufficient balance for requested action";
    private final String BARGAIN_EXISTS = "Bargain with such asset already exists";


    public BinanceTraderBotImpl(IStockMarketService stockMarketService,
                                ITradeManager tradeManager,
                                INotifier notifier,
                                IEventManager eventManager,
                                BargainService bargainService,
                                ConversionService conversionService,
                                IIndicatorReader indicatorReader,
                                IAssetService assetService, RealTradeProperties tradeProperties) {
        this.stockMarketService = stockMarketService;
        this.tradeManager = tradeManager;
        this.notifier = notifier;
        this.eventManager = eventManager;
        this.bargainService = bargainService;
        this.conversionService = conversionService;
        this.indicatorReader = indicatorReader;
        this.assetService = assetService;
        this.tradeProperties = tradeProperties;

        blackList.add("BNBUSDT");
        blackList.add("BUSDUSDT");
        blackList.add("TUSDUSDT");
        blackList.add("USDTUSDT");
        blackList.add("TUSDTUSDT");
        blackList.add("USDCUSDT");
        blackList.add("BTTCUSDT");
        blackList.add("PEPEUSDT");
        blackList.add("MEMEUSDT");
        blackList.add("1000SATSUSDT");

    }


    @Override
    public void registerStrategy(String name, IStrategy strategyManager) {
        strategyMap.put(name, strategyManager);
    }

    @Scheduled(fixedDelay = 40_000, initialDelay = 10_000)
    @Async
    @Override
    public void lookForEnterPosition() {

        KlineQueryDto klinequeryDto = new KlineQueryDto();
        klinequeryDto.setLimit(500);

        //TODO удалить для реального трейдинга (позволяет совершить ограниченное количество сделок)
        if (counter <= 0) return;
        if (!accountHasInsufficientBalance) return;

        strategyMap.values().stream()
                .filter(strategy -> strategy.getStrategyType().equals(EStrategyType.BASIC))
                .map(IStrategy::getInterval)
                .sorted()
                .map(EInterval::toString)
                .distinct()
                .forEach(
                        stringInterval -> {
                            assetService.getListToUSDTForSpotTrading().forEach(
                                    symbol -> {

                                        if (blackList.contains(symbol.getSymbol().trim())) return;

                                        klinequeryDto.setSymbol(symbol.getSymbol());
                                        klinequeryDto.setInterval(stringInterval);
                                        BarSeries series = stockMarketService.getBarSeries(klinequeryDto).orElseThrow(RuntimeException::new);

                                        if (series.getBarCount() < 500) return;

                                        strategyMap.values().stream()
                                                .filter(strategy -> strategy.getInterval().toString().equals(stringInterval))
                                                .forEach(strategy -> {

                                                    //TODO удалить для реального трейдинга (позволяет совершить ограниченное количество сделок)
                                                    if (counter <= 0) return;
                                                    if (!accountHasInsufficientBalance) return;

                                                    if (strategy.getEnterRule(series).isSatisfied(series.getEndIndex())) {
                                                        log.info(indicatorReader.getValues(series));

//                                                        if (bargainService.existsIncompleteBySymbol(symbol.getSymbol())) {
//                                                            log.warn("Bargain with such asset already exists");
//                                                            return;
//                                                        }


                                                        if (bargainService.existsBySymbolAndStatusLike(symbol.getSymbol(), EBargainStatus.OPEN_BUY_ORDER_CREATED)) {
                                                            log.warn(BARGAIN_EXISTS);
                                                            return;
                                                        }

                                                        if (bargainService.existsBySymbolAndStatusLike(symbol.getSymbol(), EBargainStatus.OPEN_BUY_ORDER_FILLED)) {
                                                            log.warn(BARGAIN_EXISTS);
                                                            return;
                                                        }


                                                        if (bargainService.existsBySymbolAndStatusLike(symbol.getSymbol(), EBargainStatus.OPEN_SELL_ORDER_CREATED)) {
                                                            log.warn(BARGAIN_EXISTS);
                                                            return;
                                                        }


                                                        if (Objects.nonNull(strategy.getAdditionalStrategy())) {
                                                            List<IStrategy> strategyList = strategy.getAdditionalStrategy();

                                                            for (IStrategy additionalStrategy : strategyList) {
                                                                EInterval intervalForAdditionalStrategy = additionalStrategy.getInterval();
                                                                BarSeries additionalSeries = series;

                                                                if (!klinequeryDto.getInterval().equals(intervalForAdditionalStrategy.toString())) {
                                                                    KlineQueryDto additionalKlineQuery = KlineQueryDto.builder()
                                                                            .interval(intervalForAdditionalStrategy.toString())
                                                                            .symbol(klinequeryDto.getSymbol())
                                                                            .limit(klinequeryDto.getLimit())
                                                                            .build();
                                                                    additionalSeries = stockMarketService.getBarSeries(additionalKlineQuery).orElseThrow(RuntimeException::new);
                                                                }

                                                                Rule additionalRule = additionalStrategy.getEnterRule(additionalSeries);
                                                                if (!additionalRule.isSatisfied(additionalSeries.getEndIndex()))
                                                                    return;
                                                            }


                                                        }

                                                        //         IndicatorValuesDto indicatorValues = indicatorReader.getValues(series);

                                                        BargainCreateDto bargainCreateDto = BargainCreateDto.builder()
                                                                .strategy(strategy.getName())
                                                                .symbol(symbol)
                                                                .percentageAim(strategy.getInterest())
                                                                .build();
                                                        BargainDto createdBargain = null;

                                                        try {
                                                            createdBargain = createBargain(bargainCreateDto);
                                                        } catch (Exception exception) {
                                                            log.error(exception);
                                                        }

                                                    }
                                                });
                                    });
                        });
    }


    @Scheduled(fixedDelay = 5_000, initialDelay = 8_000)
    @Async
    public void setSellOrder() {
        //проверка на исполнение ордера на покупку, если исполнен то создаём ордер на продажу
        bargainService.getAllWithFilledBuyOrders().ifPresent(
                entities -> {
                    entities.stream()
                            .map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                            .filter(Objects::nonNull)
                            .filter(bargainDto -> bargainDto.getBuyOrder() != null)
                            .forEach(bargainDto -> {

                                OrderDto buyOrderDto = bargainDto.getBuyOrder();
                                BigDecimal percentageAim = strategyMap.get(bargainDto.getStrategy()).getInterest();

                                OrderDto sellOrderDto = tradeManager.createSellLimitOrder(buyOrderDto.getUuid(), percentageAim);
                                bargainDto.setSellOrder(sellOrderDto);
                                bargainDto.setStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED);
                                BargainEntity savedBargainEntity = bargainService.update(bargainDto).orElseThrow(RuntimeException::new);
                                BargainDto savedBargainDto = conversionService.convert(savedBargainEntity, BargainDto.class);
                                EventDto eventDto = eventManager.get(EEventType.SELL_LIMIT_ORDER_WAS_PLACED, savedBargainDto.getSellOrder());
                                notifier.notify(eventDto);
                            });
                });
    }


    @Scheduled(fixedDelay = 30_000, initialDelay = 9_000)
    @Async
    @Override
    public void checkBargains() {

        //проверка на исполнение ордера на продажу
        bargainService.checkOnFinish().orElseThrow()
                .stream()
                .map(this::convertBargainEntityToDto)
                .forEach(bargainDto -> {

                    BargainEntity finalizedBargainEntity = bargainService.finalize(bargainDto, EBargainStatus.FINISHED);
                    BargainDto finalizedBargainDto = conversionService.convert(finalizedBargainEntity, BargainDto.class);
                    notifyAboutEvent(EEventType.BARGAIN_WAS_COMPLETED_IN_THE_BLACK, finalizedBargainDto);
                    if (!accountHasInsufficientBalance) accountHasInsufficientBalance = true;

                });


        // установка временных результатов
        bargainService.getAllByStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED).ifPresent(
                listOfBargainEntities -> listOfBargainEntities.stream()
                        .filter(Objects::nonNull)
                        .map(this::convertBargainEntityToDto)
                        .forEach(bargainService::updateResult));


        //проверка на слишком долгое время ожидание покупки актива
        bargainService.getAllByStatus(EBargainStatus.OPEN_BUY_ORDER_CREATED).orElseThrow()
                .stream().map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                .forEach(bargainDto -> {
                    LocalDateTime bargainCreateDateTime = bargainDto.getDtCreate().toLocalDateTime();

                    long duration = Duration.between(bargainCreateDateTime, LocalDateTime.now()).toMinutes();
                    long criticalTime = tradeProperties.getCriticalLostTimeForBuying();

                    if (duration > criticalTime) {
                        BargainDto canceledBargainDto = tradeManager.completeBargainByReasonTimeoutBuyOrder(bargainDto);
                        EventDto eventDto = eventManager.get(EEventType.BARGAIN_WAS_CANCELED, canceledBargainDto);
                        notifier.notify(eventDto);
                        accountHasInsufficientBalance = true;
                    }

                });
    }

    @Scheduled(cron = "* */30 * * * *")
    @Async
    public void cancelTroubleBargain() {
        // продать актив если актив провалился в цене
        bargainService.getAllByStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED).orElseThrow()
                .stream().map(this::convertBargainEntityToDto)
                .filter(bargainDto -> bargainDto.getPercentageResult().doubleValue() <= -3)
                .filter(bargainDto -> bargainDto.getTimeInWork() > 30)
                .min(Comparator.comparingDouble(bargain -> bargain.getPercentageResult().doubleValue()))
                .ifPresent(
                        bargainToCancel -> {
                            OrderDto canceledOrder = tradeManager.cancelOrder(bargainToCancel.getSellOrder());
                            OrderDto createdSellOrderByMarketPrice = tradeManager.createSellOrderByAscPrice(canceledOrder);
                            bargainToCancel.setSellOrder(createdSellOrderByMarketPrice);
                            bargainToCancel.setStatus(EBargainStatus.CANCELED_IN_THE_RED);

                            BargainEntity finalizedBargainEntity = bargainService.finalize(bargainToCancel, EBargainStatus.CANCELED_IN_THE_RED);
                            BargainDto finalizedBargainDto = convertBargainEntityToDto(finalizedBargainEntity);

                            notifyAboutEvent(EEventType.BARGAIN_WAS_COMPLETED_IN_THE_RED, finalizedBargainDto);

                            accountHasInsufficientBalance = true;
                        }

                );

/*
                .forEach(bargainToCancel -> {

                    if (bargainToCancel.getPercentageResult().doubleValue() <= -7) {

                        String messageText = MessageFormat.format("""
                            🚩 {0}
                            Asset: {1}
                            Percentage result: {2},
                            Finance result: {3},
                            Note! bargain was not canceled until now. 
                            """, EEventType.TROUBLE_BARGAIN_WAS_DETECTED.toString()
                                        .replace("_", " "),
                                bargainToCancel.getSymbol(),
                                bargainToCancel.getPercentageResult(),
                                bargainToCancel.getFinanceResult()
                        );


                        EventDto event = EventDto.builder()
                                .text(messageText)
                                .build();

                        notifier.notify(event);
                        return;
                    }
*/
    }


    private BargainDto createBargain(BargainCreateDto bargainCreateDto) {
        try {
            //TODO удалить для реального трейдинга (позволяет совершить ограниченное количество сделок)
            counter = counter - 1;

            OrderDto buyOrder = tradeManager.createBuyLimitOrderByCurrentPrice(bargainCreateDto.getSymbol());
            OrderDto sellOrderDto = null;

            BargainDto newBargain = bargainService.save(bargainCreateDto);
            bargainService.addBuyOrder(newBargain, buyOrder);
            newBargain.setStatus(EBargainStatus.OPEN_BUY_ORDER_CREATED);
            newBargain.setInterest(bargainCreateDto.getPercentageAim());

            if (buyOrder.getStatus().equals(EOrderStatus.FILLED)) {
                BigDecimal percentageAim = bargainCreateDto.getPercentageAim();
                sellOrderDto = tradeManager.createSellLimitOrder(buyOrder.getUuid(), percentageAim);
                newBargain.setSellOrder(sellOrderDto);
                newBargain.setStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED);
            }

            BargainEntity newBargainEntity = bargainService.save(newBargain).orElseThrow();
            BargainDto newBargainDto = conversionService.convert(newBargainEntity, BargainDto.class);

            notifyAboutEvent(EEventType.BARGAIN_WAS_CREATED, newBargainDto);
            notifyAboutEvent(EEventType.BUY_LIMIT_ORDER_WAS_PLACED, buyOrder);

            if (Objects.nonNull(sellOrderDto)) {
                notifyAboutEvent(EEventType.SELL_LIMIT_ORDER_WAS_PLACED, sellOrderDto);
            }
            return newBargainDto;

        } catch (BinanceClientException binanceClientException) {
            log.error(binanceClientException.getErrMsg(), binanceClientException.getMessage());
            EventDto event = eventManager.get(EEventType.ERROR, binanceClientException);
            notifier.notify(event);
            if (binanceClientException.getMessage().contains(BALANCE_ERROR_MESSAGE))
                accountHasInsufficientBalance = false;

            throw new RuntimeException(binanceClientException.getCause());
        }

    }


    private BargainDto convertBargainEntityToDto(BargainEntity source) {
        return conversionService.convert(source, BargainDto.class);
    }

    void notifyAboutEvent(EEventType eventType, OrderDto order) {
        EventDto eventDto = eventManager.get(eventType, order);
        notifier.notify(eventDto);

    }

    void notifyAboutEvent(EEventType eventType, BargainDto bargain) {
        EventDto eventDto = eventManager.get(eventType, bargain);
        notifier.notify(eventDto);

    }

    private void checkOnCriticalLoss() {

    }


}








