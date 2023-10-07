package by.zemich.binancebot.endpoint.web.controller;

import by.zemich.binancebot.core.dto.BarDto;
import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.core.enums.ESymbol;
import by.zemich.binancebot.service.impl.chein.RSICalculator;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.WebSocketApiClient;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spot")
public class SpotClientController {
    private final Map<String, Object> parameters = new HashMap<>();
    private final SpotClient spotClient;
    private final WebSocketApiClient webSocketApiClient;
    private final ConversionService conversionService;


    public SpotClientController(SpotClient spotClient, WebSocketApiClient webSocketApiClient, ConversionService conversionService) {
        this.spotClient = spotClient;
        this.webSocketApiClient = webSocketApiClient;
        this.conversionService = conversionService;
    }

    @GetMapping
    private ResponseEntity<Double> someMethod() {


        parameters.put("symbol", ESymbol.ADAUSDT.name());
        parameters.put("interval", EInterval.M15.toString());
        parameters.put("limit", "500");

        String result = spotClient.createMarket().klines(parameters);

        List<BarDto> barDtoList = conversionService.convert(result, List.class);

        RSICalculator rsiCalculator = new RSICalculator(15, barDtoList);
        Double calculatedResult = rsiCalculator.calculate();
        return ResponseEntity.ok(calculatedResult);

    }

    @GetMapping("/percent")
    private ResponseEntity<BigDecimal> getPercentDifference(@RequestParam String symbol,
                                                            @RequestParam String interval,
                                                            @RequestParam Integer limit) {

        parameters.put("symbol", symbol);
        parameters.put("interval", interval);
        parameters.put("limit", limit);

        String result = spotClient.createMarket().klines(parameters);
        List<BarDto> barDtoList = conversionService.convert(result, List.class);

        BigDecimal currentPrice = barDtoList.get(barDtoList.size() - 1).closePrice();
        BigDecimal firstBarHighPrice = barDtoList.get(0).highPrice();

        BigDecimal difference = currentPrice.subtract(firstBarHighPrice);

        BigDecimal x = difference.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal resultPercent = x.divide(currentPrice, 2, RoundingMode.HALF_UP);



        System.out.println("high price: " + firstBarHighPrice);
        System.out.println("current price: " + currentPrice);
        System.out.println("difference: " + difference);
        System.out.println("x = " + x);
        System.out.println("result = " + resultPercent);

        return ResponseEntity.ok(resultPercent);
    }

    @GetMapping("/ticker")
    private ResponseEntity<String> getTickers() {
        parameters.put("symbol", ESymbol.ADAUSDT.toString());

        String result = spotClient.createMarket().ticker(parameters);

        return ResponseEntity.ok(result);
    }


}
