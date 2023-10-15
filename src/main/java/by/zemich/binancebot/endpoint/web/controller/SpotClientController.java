package by.zemich.binancebot.endpoint.web.controller;

import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.service.api.IAccountService;
import by.zemich.binancebot.service.api.IOrderService;
import by.zemich.binancebot.service.api.IStockMarketService;

import by.zemich.binancebot.core.dto.TickerSymbolShortQuery;
import by.zemich.binancebot.service.strategy.RSI2Strategy;
import com.binance.connector.client.exceptions.BinanceClientException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ta4j.core.*;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

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
    private ResponseEntity<String> exchangeInfo(@RequestParam String symbol,
                                                @RequestParam String interval,
                                                @RequestParam Integer limit,
                                                @RequestParam Integer period) {

        KlineQueryDto query = new KlineQueryDto();
        query.setLimit(limit);

        BarSeries series15m = null;
        BarSeries series4h = null;

        AccountInformationQueryDto accountInformationQueryDto = new AccountInformationQueryDto();
        accountInformationQueryDto.setTimestamp(new Date().getTime());

        List<SymbolShortDto> symbols = stockMarketService.getAllSymbols(new TickerSymbolShortQuery()).get();
        String rsiResult = null;

        for (int i = 0; i < symbols.size(); i++) {

            String symbolQuery = symbols.get(i).getSymbol();

            if(!symbolQuery.contains("USDT")){
                continue;
            }

            query.setSymbol(symbolQuery);
            query.setInterval(EInterval.M15.toString());

            try {
                series15m = stockMarketService.getBaseBars(query).get();
            } catch (BinanceClientException exception) {
                System.out.println(exception.getErrMsg());
                continue;
            }


            query.setInterval(EInterval.H4.toString());
            series4h = stockMarketService.getBaseBars(query).get();


            ClosePriceIndicator closePrice15m = new ClosePriceIndicator(series15m);
            RSIIndicator rsi15m = new RSIIndicator(closePrice15m, 6);

            ClosePriceIndicator closePrice4h = new ClosePriceIndicator(series4h);
            RSIIndicator rsi4h = new RSIIndicator(closePrice4h, 6);

            Num rsi15mRes = rsi15m.getValue(series15m.getEndIndex());
            Num rsi4hRes = rsi4h.getValue(series4h.getEndIndex());

            if (rsi4hRes.doubleValue() <= 10 && rsi15mRes.doubleValue() <= 15) {
                rsiResult = rsiResult + "Symbol: " + symbolQuery + ". RSI 15m: " + rsi15m.getValue(series15m.getEndIndex()).toString() + "RSI 4H: " + rsi4h.getValue(series4h.getEndIndex()) + "\n";
            }


        }

        return ResponseEntity.ok(rsiResult);
    }

    @GetMapping("/other")
    private ResponseEntity<String> getInfo() {
        List<SymbolShortDto> response = stockMarketService.getAllSymbols(new TickerSymbolShortQuery()).get();


        return ResponseEntity.ok(response.toString());
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
