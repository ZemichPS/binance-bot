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
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.ChaikinMoneyFlowIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.util.*;

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

        List<SymbolShortDto> response = stockMarketService.getAllSymbols(new TickerSymbolShortQuery()).get();




        List<BollingerStrategyReport> reports = findAndReport(response);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/other")
    private ResponseEntity<String> getInfo() {
       /* TODO
       BollingerBand(20) + Volume + RSI STRATEGY.
        */

        List<SymbolShortDto> response = stockMarketService.getAllSymbols(new TickerSymbolShortQuery()).get();
        return ResponseEntity.ok(response.toString());
    }

    private List<BollingerStrategyReport> findAndReport(List<SymbolShortDto> symbols) {

        KlineQueryDto query = new KlineQueryDto();
        query.setLimit(100);
        query.setInterval("15m");

        List<BollingerStrategyReport> reports = new ArrayList<>();


        for (int i = 0; i < symbols.size(); i++) {

            String symbol = symbols.get(i).getSymbol();
            query.setSymbol(symbol);

            if (!symbol.contains("USDT")) continue;
            if (symbol.startsWith("USDT")) continue;

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

            try {
                if (rsiIndicator.getValue(endIndex).doubleValue() <= 31) { //RSI condition
                    if (percentB.getValue(endIndex).doubleValue() <= 0) { // BB condition

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
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }

        }

        return reports;

    }


}


//        /*
//         * Creating indicators
//         */
//        // Close price
//        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
//        // Typical price
//        TypicalPriceIndicator typicalPrice = new TypicalPriceIndicator(series);
//        // Price variation
//      //  ClosePriceRatioIndicator closePriceRatioIndicator = new ClosePriceRatioIndicator(series);
//        // Simple moving averages
//        SMAIndicator shortSma = new SMAIndicator(closePrice, 8);
//        SMAIndicator longSma = new SMAIndicator(closePrice, 20);
//        // Exponential moving averages
//        EMAIndicator shortEma = new EMAIndicator(closePrice, 8);
//        EMAIndicator longEma = new EMAIndicator(closePrice, 20);
//        // Percentage price oscillator
//        PPOIndicator ppo = new PPOIndicator(closePrice, 12, 26);
//        // Rate of change
//        ROCIndicator roc = new ROCIndicator(closePrice, 100);
//        // Relative strength index
//        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
//        // Williams %R
//        WilliamsRIndicator williamsR = new WilliamsRIndicator(series, 20);
//        // Average true range
//        ATRIndicator atr = new ATRIndicator(series, 20);
//        // Standard deviation
//        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 14);
//
//
//        Map<String, String> indicatorResultMap = new HashMap<>();
//        indicatorResultMap.put("ClosePriceIndicator", closePrice.getValue(series.getEndIndex()).toString());
//        indicatorResultMap.put("TypicalPriceIndicator", typicalPrice.getValue(series.getEndIndex()).toString());
//        indicatorResultMap.put("SMAIndicator", shortSma.getValue(series.getEndIndex()).toString());
//        indicatorResultMap.put("SMAIndicator", longSma.getValue(series.getEndIndex()).toString());
//        indicatorResultMap.put("PPOIndicator", ppo.getValue(series.getEndIndex()).toString());
//        indicatorResultMap.put("ROCIndicator", roc.getValue(series.getEndIndex()).toString());
//        indicatorResultMap.put("RSIIndicator", rsi.getValue(series.getEndIndex()).toString());
//        indicatorResultMap.put("WilliamsRIndicator", williamsR.getValue(series.getEndIndex()).toString());
//        indicatorResultMap.put("ATRIndicator", atr.getValue(series.getEndIndex()).toString());
//        indicatorResultMap.put("StandardDeviationIndicator", sd.getValue(series.getEndIndex()).toString());
//
//
//
