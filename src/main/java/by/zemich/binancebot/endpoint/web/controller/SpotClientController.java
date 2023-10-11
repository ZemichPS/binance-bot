package by.zemich.binancebot.endpoint.web.controller;

import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.service.api.IAccountService;
import by.zemich.binancebot.service.api.IConverter;
import by.zemich.binancebot.service.api.IOrderService;
import by.zemich.binancebot.service.api.IStockMarketService;
import by.zemich.binancebot.service.impl.CryptoCalculator;

import by.zemich.binancebot.service.strategy.RSI2Strategy;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.WebSocketApiClient;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ta4j.core.*;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.*;

@RestController
@RequestMapping("/spot")
public class SpotClientController {
    private final Map<String, Object> parameters = new HashMap<>();
    private final SpotClient spotClient;
    private final WebSocketApiClient webSocketApiClient;
    private final ConversionService conversionService;

    private final CryptoCalculator cryptoCalculator;
    private final IAccountService accountService;
    private final IConverter converter;
    private final IStockMarketService stockMarketService;
    private final RSI2Strategy rsi2Strategy;
    private final IOrderService orderService;

    public SpotClientController(SpotClient spotClient, WebSocketApiClient webSocketApiClient, ConversionService conversionService, CryptoCalculator cryptoCalculator, IAccountService accountService, IConverter converter, IStockMarketService stockMarketService, RSI2Strategy rsi2Strategy, IOrderService orderService) {
        this.spotClient = spotClient;
        this.webSocketApiClient = webSocketApiClient;
        this.conversionService = conversionService;
        this.cryptoCalculator = cryptoCalculator;

        this.accountService = accountService;
        this.converter = converter;
        this.stockMarketService = stockMarketService;
        this.rsi2Strategy = rsi2Strategy;
        this.orderService = orderService;
    }

    @GetMapping("/history")
    private ResponseEntity<List<HistoricalOrderResponseDto>> getHistory() {
        HistoricalOrderQueryDto historicalOrderQuery = new HistoricalOrderQueryDto();
        historicalOrderQuery.setSymbol("RUNEUSDT");
        List<HistoricalOrderResponseDto> list = orderService.getAll(historicalOrderQuery).get();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/rsi")
    private ResponseEntity<String> exchangeInfo(@RequestParam String symbol,
                                                @RequestParam String interval,
                                                @RequestParam Integer limit) {

        KlineQueryDto query = new KlineQueryDto();
        query.setInterval(interval);
        query.setSymbol(symbol);
        query.setLimit(limit);
        // List<BarDto> barList = stockMarketService.getBars(query).get();

        List<BaseBar> baseBarList = stockMarketService.getBaseBars(query).get();

        //DecimalNum
        BarSeries series = new BaseBarSeries();
        for (BaseBar bar : baseBarList) {
            series.addBar(bar);
        }

        Strategy strategy = rsi2Strategy.buildStrategy(series);
        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord = seriesManager.run(strategy);

        System.out.println("Number of positions for the strategy: " + tradingRecord.getPositionCount());

        // Analysis
       // System.out.println("Total return for the strategy: " + new ReturnCriterion().calculate(series, tradingRecord));



        return ResponseEntity.ok(series.toString());
    }


}
