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
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
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

        BarSeries series = stockMarketService.getBaseBars(query).get();

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, series.getBarCount());

        ROCIndicator roc = new ROCIndicator(closePrice, 100);

        WilliamsRIndicator williamsR = new WilliamsRIndicator(series, 20);

        PPOIndicator ppo = new PPOIndicator(closePrice, 12, 26);

        StochasticRSIIndicator stochasticRSIIndicator = new StochasticRSIIndicator(series, 500);

        SMAIndicator shortSma = new SMAIndicator(closePrice, 5);
        SMAIndicator longSma = new SMAIndicator(closePrice, 200);




        Num RSIResult = rsiIndicator.getValue(series.getEndIndex());
        Num rocResult = roc.getValue(series.getEndIndex());
        Num williamsRResult = williamsR.getValue(series.getEndIndex());
        Num ppoRezult = shortSma.getValue(series.getEndIndex());
        Num longSmaRezult = longSma.getValue(series.getEndIndex());
        Num stochasticRSIIndicatorRezult = stochasticRSIIndicator.getValue(series.getEndIndex());





        return ResponseEntity.ok(stochasticRSIIndicatorRezult.toString());
    }


}
