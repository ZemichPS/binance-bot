package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainCreateDto;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EBargainStatus;

import java.util.List;
import java.util.Optional;

public interface BargainFacade {
    BargainDto create(BargainCreateDto bargainCreateDto);
    BargainDto addBuyOrder(BargainDto bargainDto, OrderDto buyOrder);
    BargainDto addSellOrder(BargainDto bargainDto, OrderDto sellOrder);
    BargainDto endByReasonExpired(BargainDto bargainDto);
    BargainDto finalize(BargainDto bargainDto, EBargainStatus status);
    BargainDto updateResult(BargainDto bargainDto);
    List<BargainDto> checkOnFinish();
    List<BargainDto> getAllByStatus(EBargainStatus status);
    List<BargainDto> getAllWithFilledBuyOrders();
    BargainDto completeBargainByReasonTimeoutBuyOrder(BargainDto troubleBargain);




}
