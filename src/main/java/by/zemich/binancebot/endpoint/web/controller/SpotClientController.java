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
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
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

        /*
         * Creating indicators
         */
        // Close price
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        // Typical price
        TypicalPriceIndicator typicalPrice = new TypicalPriceIndicator(series);
        // Price variation
      //  ClosePriceRatioIndicator closePriceRatioIndicator = new ClosePriceRatioIndicator(series);
        // Simple moving averages
        SMAIndicator shortSma = new SMAIndicator(closePrice, 8);
        SMAIndicator longSma = new SMAIndicator(closePrice, 20);
        // Exponential moving averages
        EMAIndicator shortEma = new EMAIndicator(closePrice, 8);
        EMAIndicator longEma = new EMAIndicator(closePrice, 20);
        // Percentage price oscillator
        PPOIndicator ppo = new PPOIndicator(closePrice, 12, 26);
        // Rate of change
        ROCIndicator roc = new ROCIndicator(closePrice, 100);
        // Relative strength index
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
        // Williams %R
        WilliamsRIndicator williamsR = new WilliamsRIndicator(series, 20);
        // Average true range
        ATRIndicator atr = new ATRIndicator(series, 20);
        // Standard deviation
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 14);


        Map<String, String> indicatorResultMap = new HashMap<>();
        indicatorResultMap.put("ClosePriceIndicator", closePrice.getValue(series.getEndIndex()).toString());
        indicatorResultMap.put("TypicalPriceIndicator", typicalPrice.getValue(series.getEndIndex()).toString());
        indicatorResultMap.put("SMAIndicator", shortSma.getValue(series.getEndIndex()).toString());
        indicatorResultMap.put("SMAIndicator", longSma.getValue(series.getEndIndex()).toString());
        indicatorResultMap.put("PPOIndicator", ppo.getValue(series.getEndIndex()).toString());
        indicatorResultMap.put("ROCIndicator", roc.getValue(series.getEndIndex()).toString());
        indicatorResultMap.put("RSIIndicator", rsi.getValue(series.getEndIndex()).toString());
        indicatorResultMap.put("WilliamsRIndicator", williamsR.getValue(series.getEndIndex()).toString());
        indicatorResultMap.put("ATRIndicator", atr.getValue(series.getEndIndex()).toString());
        indicatorResultMap.put("StandardDeviationIndicator", sd.getValue(series.getEndIndex()).toString());

        return ResponseEntity.ok(indicatorResultMap.toString());
    }


}
