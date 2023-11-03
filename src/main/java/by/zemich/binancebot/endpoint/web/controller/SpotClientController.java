package by.zemich.binancebot.endpoint.web.controller;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.service.api.IAccountService;
import by.zemich.binancebot.service.api.IOrderService;
import by.zemich.binancebot.service.api.IStockMarketService;

import by.zemich.binancebot.core.dto.TickerSymbolShortQuery;

import by.zemich.binancebot.service.strategy.BollingerBasedSecondStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ta4j.core.*;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.CorrelationCoefficientIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/spot")
public class SpotClientController {
    private final Map<String, Object> parameters = new HashMap<>();

    private final IStockMarketService stockMarketService;
    private final IOrderService orderService;
    private final IAccountService accountService;
    private final BollingerBasedSecondStrategy strategyManager;

    public SpotClientController(IStockMarketService stockMarketService, IOrderService orderService, IAccountService accountService, BollingerBasedSecondStrategy strategyManager) {
        this.stockMarketService = stockMarketService;
        this.orderService = orderService;
        this.accountService = accountService;
        this.strategyManager = strategyManager;
    }


    /*
                @GetMapping("/history")
                private ResponseEntity<List<HistoricalOrderResponseDto>> getHistory() {
                    HistoricalOrderQueryDto historicalOrderQuery = new HistoricalOrderQueryDto();
                    historicalOrderQuery.setSymbol("RUNEUSDT");
                    List<HistoricalOrderResponseDto> list = orderService.getAll(historicalOrderQuery).get();
                    return ResponseEntity.ok(list);
                }
            */
    @GetMapping("/account")
    private ResponseEntity<AccountInformationResponseDto> accountInf() {
        AccountInformationQueryDto query = new AccountInformationQueryDto();
        query.setTimestamp(new Date().getTime());

        return ResponseEntity.ok(accountService.getInformation(query).get());
    }

    @GetMapping("/rule_test")
    private ResponseEntity<String> rule(@RequestParam String symbol,
                                        @RequestParam String interval,
                                        @RequestParam Integer limit,
                                        @RequestParam Integer nthPrevious,
                                        @RequestParam DecimalNum minStrenght,
                                        @RequestParam DecimalNum maxSlope) {


        KlineQueryDto query = KlineQueryDto.builder()
                .symbol(symbol)
                .interval(interval)
                .limit(limit)
                .build();

        BarSeries series = stockMarketService.getBarSeries(query).get();

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        EMAIndicator longEma = new EMAIndicator(closePrice, 20);
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(longEma);
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd);
        BollingerBandWidthIndicator bbw = new BollingerBandWidthIndicator(bbu, bbm, bbl);
        PercentBIndicator percentB = new PercentBIndicator(closePrice, 20, 2.0);

        Rule rule = new InSlopeRule(longEma, nthPrevious, DecimalNum.valueOf(maxSlope));

        Rule risingRule = new IsRisingRule(longEma, nthPrevious, minStrenght.doubleValue());
        Rule fallingRule = new IsFallingRule(longEma, nthPrevious, minStrenght.doubleValue());

        OnBalanceVolumeIndicator balanceVolumeIndicator = new OnBalanceVolumeIndicator(series);
        CorrelationCoefficientIndicator correlation = new CorrelationCoefficientIndicator(rsiIndicator, balanceVolumeIndicator, 14);

        return ResponseEntity.ok(correlation.getValue(series.getEndIndex()).toString());

    }


    @GetMapping("/create_oder")
    private ResponseEntity<OrderEntity> createOrder(){
/*
        NewOrderRequestDto request = new NewOrderRequestDto();

        request.setSymbol("LINKUSDT");
        request.setSide(ESide.BUY.name());
        request.setType(EOrderType.LIMIT.name());
        request.setQuantity(new BigDecimal(1));
        request.setPrice(new BigDecimal("9.204"));
        request.setTimeInForce(ETimeInForce.GTC.name());

        OrderEntity orderEntity = orderService.create(request).get();
*/
  //      return ResponseEntity.ok(orderEntity);
       return ResponseEntity.ok(null);
    }

    @GetMapping("/report")
    private ResponseEntity<Map<String, List<Position>>> getReport() {

        List<String> symbolsList = stockMarketService.getSpotSymbols().get();


        Map<String, List<Position>> reports = findAndReport(symbolsList);

        return ResponseEntity.ok(reports);
    }

    @GetMapping("/exchange")
    private ResponseEntity<ExchangeInfoResponseDto> getExchangeInfo() {
        ExchangeInfoQueryDto exchangeInfoQuery = new ExchangeInfoQueryDto();

        exchangeInfoQuery.setPermissions(new ArrayList<>(List.of("SPOT")));
        ExchangeInfoResponseDto exchangeInfo = stockMarketService.getExchangeInfo(exchangeInfoQuery).get();

        return ResponseEntity.ok(exchangeInfo);
    }

    @GetMapping("/symbols")
    private ResponseEntity<List<String>> getSymbols() {
        List<String> symbolsList = stockMarketService.getSpotSymbols().get();

        return ResponseEntity.ok(symbolsList.stream().filter(s -> !s.startsWith("USDT"))
                .filter(s -> s.contains("USDT"))
                .collect(Collectors.toList()));
    }




    @GetMapping("/correlation")
    private ResponseEntity<String> getCorrelation(@RequestParam String symbol,
                                                  @RequestParam String interval,
                                                  @RequestParam Integer limit,
                                                  @RequestParam Integer period) {
        KlineQueryDto query = KlineQueryDto.builder().build();
        query.setLimit(limit);
        query.setInterval(interval);
        query.setInterval(symbol);

        BarSeries series = stockMarketService.getBarSeries(query).get();

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);
        ADXIndicator adxIndicator = new ADXIndicator(series, 20);

        CorrelationCoefficientIndicator correlationCoefficient = new CorrelationCoefficientIndicator(rsiIndicator, adxIndicator, 20);
        Num correlation = correlationCoefficient.getValue(series.getEndIndex());

        return ResponseEntity.ok(correlation.toString());
    }


    @GetMapping("/other")
    private ResponseEntity<String> getInfo() {
       /* TODO
       BollingerBand(20) + Volume + RSI STRATEGY.
        */

        List<SymbolShortDto> response = stockMarketService.getAllSymbols(new TickerSymbolShortQuery()).get();
        return ResponseEntity.ok(response.toString());
    }

    private Map<String, List<Position>> findAndReport(List<String> symbols) {

        KlineQueryDto query = KlineQueryDto.builder().build();
        query.setLimit(96);
        query.setInterval("15m");

        List<BollingerStrategyReport> reports = new ArrayList<>();
        Map<String, List<Position>> positionMap = new HashMap<>();
        Integer amountPosition = 0;


        for (int i = 0; i < symbols.size(); i++) {

            String symbol = symbols.get(i);
            query.setSymbol(symbol);

            BarSeries series = stockMarketService.getBarSeries(query).get();

            BarSeriesManager seriesManager = new BarSeriesManager(series);


            try {

                TradingRecord tradingRecord = seriesManager.run(strategyManager.get(series));
                List<Position> positionList = tradingRecord.getPositions();
                System.out.println("Position count is: " + tradingRecord.getPositionCount());
                amountPosition = amountPosition + tradingRecord.getPositionCount();
                positionMap.put(symbol, positionList);


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        positionMap.entrySet().stream().forEach(stringListEntry ->
                {
                    System.out.println("---------------------------------------------");
                    System.out.println("symbol is: " + stringListEntry.getKey().toString());
                    System.out.println("position count: " + stringListEntry.getValue().size());
                    stringListEntry.getValue().stream().forEach(position -> {
                        System.out.println("{");
                        Trade trade = position.getEntry();
                        System.out.println("   entry:" + position.getEntry());
                        System.out.println("}");
                    });
                    System.out.println("---------------------------------------------");
                }
        );


        System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
        System.out.println("ВСЕГО ПОЗИЦИЙ: " + amountPosition);
        System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
        return null;
    }


}

/*
    ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
    SMAIndicator longSma = new SMAIndicator(closePrice, 20);

    // Standard deviation
    StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
    RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);


    BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(longSma);
    BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd);
    BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd);
    BollingerBandWidthIndicator bbw = new BollingerBandWidthIndicator(bbu, bbm, bbl);
    PercentBIndicator percentB = new PercentBIndicator(closePrice, 20, 3.0);

    ADXIndicator adxIndicator = new ADXIndicator(series, 20);
    int endIndex = series.getEndIndex();


    // Правило перепроданности по RSI
    Rule underRsiRule = new UnderIndicatorRule(rsiIndicator, 30);
    // Правило пробития нижнего уровня BB
    Rule underPercentB = new UnderIndicatorRule(percentB, 0);
    // Правило MAX width канала Боллиджера
    Rule isHighestRule = new IsHighestRule(bbw, 7);

    Rule waitCountBarRule = new WaitForRule(Trade.TradeType.BUY, 4);


    // Resulted enter rule
    Rule enterRule = underRsiRule.and(underPercentB);

    // ResultedExitRule
    Rule exitRule = new StopGainRule(closePrice, DecimalNum.valueOf("0.8"));


    Strategy firstStrategy = new BaseStrategy(enterRule, exitRule);
            firstStrategy.getName();

                    BarSeriesManager seriesManager = new BarSeriesManager(series);
*/