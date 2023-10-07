package by.zemich.binancebot.endpoint.web.controller;

import by.zemich.binancebot.core.dto.BarDto;
import by.zemich.binancebot.core.dto.ChangePriceDTO;
import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.core.enums.ESymbol;
import by.zemich.binancebot.service.impl.CryptoCalculator;
import by.zemich.binancebot.service.impl.chein.RSICalculator;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.WebSocketApiClient;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RestController
@RequestMapping("/spot")
public class SpotClientController {
    private final Map<String, Object> parameters = new HashMap<>();
    private final SpotClient spotClient;
    private final WebSocketApiClient webSocketApiClient;
    private final ConversionService conversionService;

    private final CryptoCalculator cryptoCalculator;


    public SpotClientController(SpotClient spotClient, WebSocketApiClient webSocketApiClient, ConversionService conversionService, CryptoCalculator cryptoCalculator) {
        this.spotClient = spotClient;
        this.webSocketApiClient = webSocketApiClient;
        this.conversionService = conversionService;
        this.cryptoCalculator = cryptoCalculator;
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
    private ResponseEntity<ChangePriceDTO> getPercentDifference(@RequestParam String symbol,
                                                                @RequestParam String interval,
                                                                @RequestParam Integer limit) {

        parameters.put("symbol", symbol);
        parameters.put("interval", interval);
        parameters.put("limit", limit);

        String result = spotClient.createMarket().klines(parameters);
        List<BarDto> barDtoList = conversionService.convert(result, List.class);

        List<Integer> indexesList = new ArrayList<>();
        int caseCounter = 0;
        List<BigDecimal> percentageResult = new ArrayList<>();

        for (int i = 0; i < barDtoList.size(); i++) {
            BigDecimal percentageDifference = cryptoCalculator.getPercentDifference(
                    barDtoList.get(i).openPrice(),
                    barDtoList.get(i).closePrice()
            );

            if (percentageDifference.doubleValue() < -2.0) {
                caseCounter++;


                try {
                    System.out.println("разница в % (open-close) свечи №" + i + ": " + percentageDifference);
                    BigDecimal nextElementPercentDifference = cryptoCalculator.getPercentDifference(
                            barDtoList.get(i + 1).openPrice(),
                            barDtoList.get(i + 1).closePrice()
                    );
                    System.out.println("разница в % (open-close) следующей свечи: " + nextElementPercentDifference);

                    BigDecimal highResult = cryptoCalculator.getPercentDifference(
                            barDtoList.get(i + 1).openPrice(),
                            barDtoList.get(i + 1).highPrice());

                    System.out.println("разница в % (open-high) следующей свечи достигала: " + highResult);

                    percentageResult.add(highResult);

                } catch (IndexOutOfBoundsException ex) {
                    System.out.println("достигнут предел набора. Анализа больше нет.");
                }

                System.out.println("----------------------------------");
            }
        }

        System.out.println("всего случаев: " + caseCounter);
        BigDecimal minimalElement = percentageResult.stream().min(Comparator.comparing(BigDecimal::doubleValue)).get();
        System.out.println("Минимальный результат: " + minimalElement);


        return ResponseEntity.ok(null);
    }

    @GetMapping("/ticker")
    private ResponseEntity<String> getTickers() {
        parameters.put("symbol", ESymbol.ADAUSDT.toString());

        String result = spotClient.createMarket().ticker(parameters);

        return ResponseEntity.ok(result);
    }


}
