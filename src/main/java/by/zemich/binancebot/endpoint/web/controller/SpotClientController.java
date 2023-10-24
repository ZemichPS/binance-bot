package by.zemich.binancebot.endpoint.web.controller;

import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.service.api.IAccountService;
import by.zemich.binancebot.service.api.IOrderService;
import by.zemich.binancebot.service.api.IStockMarketService;

import by.zemich.binancebot.core.dto.TickerSymbolShortQuery;
import by.zemich.binancebot.service.strategy.RSI2Strategy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ta4j.core.*;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.CorrelationCoefficientIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.ChaikinMoneyFlowIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.IsRisingRule;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/spot")
public class SpotClientController {
    private final Map<String, Object> parameters = new HashMap<>();
    private final RSI2Strategy strategy;
    private final IStockMarketService stockMarketService;
    private final IOrderService orderService;
    private final IAccountService accountService;


    public SpotClientController(RSI2Strategy strategy, IStockMarketService stockMarketService, IOrderService orderService, IAccountService accountService) {
        this.strategy = strategy;
        this.stockMarketService = stockMarketService;
        this.orderService = orderService;
        this.accountService = accountService;
    }


    @GetMapping("/history")
    private ResponseEntity<List<HistoricalOrderResponseDto>> getHistory() {
        HistoricalOrderQueryDto historicalOrderQuery = new HistoricalOrderQueryDto();
        historicalOrderQuery.setSymbol("RUNEUSDT");
        List<HistoricalOrderResponseDto> list = orderService.getAll(historicalOrderQuery).get();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/account")
    private ResponseEntity<AccountInformationResponseDto> accountInf() {
        AccountInformationQueryDto query = new AccountInformationQueryDto();
        query.setTimestamp(new Date().getTime());

        return ResponseEntity.ok(accountService.getInformation(query).get());
    }

    @GetMapping("/indicators")
    private ResponseEntity<List<BollingerStrategyReport>> exchangeInfo(@RequestParam String symbol,
                                                                       @RequestParam String interval,
                                                                       @RequestParam Integer limit,
                                                                       @RequestParam Integer period) {

        List<String> symbolsList = stockMarketService.getSpotSymbols().get();


        List<BollingerStrategyReport> reports = findAndReport(symbolsList.stream()
                .filter(s -> !s.startsWith("USDT"))
                .filter(s -> s.contains("USDT"))
                .collect(Collectors.toList()));

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
        // getter fuck
        Rule rule;
        // symbolsList.stream().filter(s -> s.startsWith("USDT")).collect(Collectors.toList());
        return ResponseEntity.ok(symbolsList.stream().filter(s -> !s.startsWith("USDT"))
                .filter(s -> s.contains("USDT"))
                .collect(Collectors.toList()));
    }


    @GetMapping("/correlation")
    private ResponseEntity<String> getCorrelation(@RequestParam String symbol,
                                                  @RequestParam String interval,
                                                  @RequestParam Integer limit,
                                                  @RequestParam Integer period) {
        KlineQueryDto query = new KlineQueryDto();
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

    private List<BollingerStrategyReport> findAndReport(List<String> symbols) {

        KlineQueryDto query = new KlineQueryDto();
        query.setLimit(100);
        query.setInterval("15m");

        List<BollingerStrategyReport> reports = new ArrayList<>();


        for (int i = 0; i < symbols.size(); i++) {

            String symbol = symbols.get(i);
            query.setSymbol(symbol);

            BarSeries series = stockMarketService.getBarSeries(query).get();

            ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
            SMAIndicator longSma = new SMAIndicator(closePrice, 20);

            // Standard deviation
            StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
            RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);


            BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(longSma);
            BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd);
            BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd);
            BollingerBandWidthIndicator bbw = new BollingerBandWidthIndicator(bbu, bbm, bbl);
            PercentBIndicator percentB = new PercentBIndicator(closePrice, 20, 2.0);

            ADXIndicator adxIndicator = new ADXIndicator(series, 20);
            int endIndex = series.getEndIndex();


            boolean bbwRising = new IsRisingRule(bbw, 14).isSatisfied(endIndex);
            boolean longSmaRising = new IsRisingRule(longSma, 7).isSatisfied(endIndex);

            try {
                if (longSmaRising) { // средняя растёт
                    System.out.println("средняя растёт");
                    if(percentB.getValue(endIndex).doubleValue() <= 0.17) // BBP Condition
                    if (rsiIndicator.getValue(endIndex).doubleValue() <= 50) {// RSI <=50
                        BollingerStrategyReport report = BollingerStrategyReport.builder()

                                .percentBIndicatorValue(new BigDecimal(percentB.getValue(endIndex).toString()))
                                .bollingerBandWidthValue(new BigDecimal(bbw.getValue(endIndex).toString()))
                                .bollingerBandsUpperValue(new BigDecimal(bbu.getValue(endIndex).toString()))
                                .bollingerBandsMiddleValue(new BigDecimal(bbm.getValue(endIndex).toString()))
                                .bollingerBandsLowerValue(new BigDecimal(bbl.getValue(endIndex).toString()))
                                .rsiValue(new BigDecimal(rsiIndicator.getValue(endIndex).toString()))
                                .currentPriceValue(new BigDecimal(series.getBar(endIndex).getClosePrice().toString()))
                                .adxValue(new BigDecimal(adxIndicator.getValue(endIndex).toString()))
                                .symbolName(symbol)
                                .build();

                        reports.add(report);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return reports;
    }
}
