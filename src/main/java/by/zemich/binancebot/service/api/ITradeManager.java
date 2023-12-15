package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.Asset;

import java.math.BigDecimal;
import java.util.UUID;

public interface ITradeManager {
    OrderDto createBuyLimitOrderByAskPrice(Asset symbol);
    OrderDto createBuyLimitOrderByCurrentPrice(Asset symbol);
    OrderDto createSellLimitOrder(UUID buyOrderUuid, BigDecimal percentageAim);
    OrderDto createSellOrderByAscPrice(OrderDto orderDtoToSell);

    OrderDto createStopLimitOrder(Long buyOrderId);
    OrderDto cancelOrder(OrderDto troubleOrder);
    BargainDto completeBargainInTheRed(BargainDto bargainToCancel);

    BargainDto completeBargainByReasonTimeoutBuyOrder(BargainDto troubleBargain);









}
