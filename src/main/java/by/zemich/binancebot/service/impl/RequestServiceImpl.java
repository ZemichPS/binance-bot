package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.config.properties.RealTradeProperties;
import by.zemich.binancebot.core.NewBuyOrderRequestDto;
import by.zemich.binancebot.core.dto.NewSellOrderByAskPriceRequestDto;
import by.zemich.binancebot.core.dto.NewSellOrderRequestDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.enums.ENewOrderRespType;
import by.zemich.binancebot.core.enums.EOrderType;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.core.enums.ETimeInForce;
import by.zemich.binancebot.service.api.AssetBrokerService;
import by.zemich.binancebot.service.api.RequestService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RequestServiceImpl implements RequestService {

    private final AssetBrokerService assetBrokerService;

    private final RealTradeProperties tradeProperties;

    public RequestServiceImpl(AssetBrokerService assetBrokerService, RealTradeProperties tradeProperties) {
        this.assetBrokerService = assetBrokerService;
        this.tradeProperties = tradeProperties;
    }


    @Override
    public RequestForNewOrderDto getRequestForBuyLimitOrder(NewBuyOrderRequestDto buyRequest) {

        Asset assetForBuying = buyRequest.getAssetForBuying();
        PriceFilter priceFilter = assetForBuying.getPriceFilter();
        LotSizeFilter lotSizeBinanceFilter = assetForBuying.getLotSizeFilter();

        BigDecimal stepSize = lotSizeBinanceFilter.getStepSize();

        BigDecimal currentPrice = assetBrokerService.getCurrentPrice(assetForBuying.getSymbol());
        BigDecimal quantityForBuying = buyRequest.getDeposit().divide(currentPrice, 10, RoundingMode.DOWN);
        BigDecimal computedQuantity = getAssetQuantityUsingStepSize(quantityForBuying, stepSize)
                .setScale(priceFilter.getTickSize().scale(), RoundingMode.HALF_UP);

        return RequestForNewOrderDto.builder()
                .symbol(assetForBuying.getSymbol())
                .price(currentPrice)
                .side(ESide.BUY)
                .type(EOrderType.LIMIT)
                .quantity(computedQuantity)
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();
    }

    @Override
    public RequestForNewOrderDto getRequestForSellLimitOrder(NewSellOrderRequestDto sellRequest) {
        Asset asset = assetBrokerService.getBySymbol(sellRequest.getAssetSymbol())

        PriceFilter priceFilter = asset.getPriceFilter();
        LotSizeFilter lotSizeFilter = asset.getLotSizeFilter();
        BigDecimal takerFee = tradeProperties.getTakerFee();
        BigDecimal interest = getValueFromPercentage(buyOrder.getPrice(), price);

        BigDecimal currentQuantity = buyOrder.getOrigQty();
        BigDecimal stepSize = lotSizeFilter.getStepSize();
        BigDecimal sellQuantity = currentQuantity.subtract(getValueFromPercentage(currentQuantity, takerFee));

        // ПАРАМЕТР НУЖЕН ЕСЛИ НЕТ BMB ДЛЯ ОПЛАТЫ КОММИССИИ
        BigDecimal computedQuantity = getAssetQuantityUsingStepSize(sellQuantity, stepSize).setScale(priceFilter.getTickSize().scale(), RoundingMode.UNNECESSARY);

        BigDecimal sellPrice = buyOrder.getPrice().add(interest);

        BigDecimal computingSellPrice = getAssetPriceUsingStepSize(sellPrice, priceFilter.getTickSize()).setScale(priceFilter.getTickSize().scale(), RoundingMode.UNNECESSARY);

        RequestForNewOrderDto requestForNewSellOrder = RequestForNewOrderDto.builder()
                .symbol(sellRequest.getAssetSymbol())
                .price(sellRequest.getPrice())
                .side(ESide.SELL)
                .type(EOrderType.LIMIT)
                .quantity(sellRequest.getQuantity())
                .timeInForce(sellRequest.getTimeInForce())
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();


        return requestForNewSellOrder;
    }

    @Override
    public RequestForNewOrderDto getRequestForSellLimitOrderByAscPrice(NewSellOrderByAskPriceRequestDto request) {
        BigDecimal ascPrice = assetBrokerService.getAskPriceBySymbol(request.getAssetSymbol());
        return RequestForNewOrderDto.builder()
                .symbol(request.getAssetSymbol())
                .price(ascPrice)
                .side(ESide.SELL)
                .type(EOrderType.LIMIT)
                .quantity(request.getQuantity())
                .timeInForce(request.getTimeInForce())
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

    }

    @Override
    public CancelOrderRequestDto getRequestForCancelOrder(OrderDto order) {
        return CancelOrderRequestDto.builder()
                .symbol(order.getSymbol())
                .orderId(order.getOrderId())
                .build();

    }

    private BigDecimal getAssetQuantityUsingStepSize(BigDecimal quantity, BigDecimal stepSize) {
        BigDecimal rest = quantity.remainder(stepSize);
        return quantity.subtract(rest);
    }

    private BigDecimal getValueFromPercentage(BigDecimal value, BigDecimal percent) {
        return value.multiply(percent).divide(new BigDecimal(100), RoundingMode.DOWN);
    }

    private BigDecimal getAssetPriceUsingStepSize(BigDecimal price, BigDecimal tickSize) {
        BigDecimal rest = price.remainder(tickSize);
        return price.subtract(rest);
    }


}
