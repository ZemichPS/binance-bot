package by.zemich.binancebot.endpoint.web.controller;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.service.api.*;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/spot")
public class SpotClientController {

    private final IStockMarketService stockMarketService;
    private final IOrderService orderService;
    private final IAccountService accountService;

    private final ITradeManager tradeManager;
    private final IBargainService bargainService;

    private final ConversionService conversionService;

    public SpotClientController(IStockMarketService stockMarketService, IOrderService orderService, IAccountService accountService, ITradeManager tradeManager, IBargainService bargainService, ConversionService conversionService) {
        this.stockMarketService = stockMarketService;
        this.orderService = orderService;
        this.accountService = accountService;
        this.tradeManager = tradeManager;
        this.bargainService = bargainService;
        this.conversionService = conversionService;
    }

    @GetMapping("/cancel_order")
    private ResponseEntity<CancelOrderResponseDto> cancelOrder(@RequestParam String symbol,
                                                               @RequestParam Long orderId) {

        return ResponseEntity.ok(stockMarketService.cancelOrder(CancelOrderRequestDto.builder()
                .orderId(orderId)
                .symbol(symbol)
                .build()));
    }

    @GetMapping("/cancel_bargain")
    private ResponseEntity<BargainDto> cancelBargain(@RequestParam UUID uuid) {

        BargainEntity foundedBargainEntity = bargainService.getByUuid(uuid).orElseThrow(RuntimeException::new);
        BargainDto foundedBargainDto = conversionService.convert(foundedBargainEntity, BargainDto.class);

        return ResponseEntity.ok(tradeManager.completeBargainInTheRed(foundedBargainDto));


    }

}