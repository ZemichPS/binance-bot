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
    BargainDto update(BargainDto bargainDtoForUpdate);
    BargainDto addBuyOrder(BargainDto bargainDto, OrderDto buyOrder);
    BargainDto addSellOrder(BargainDto bargainDto, OrderDto sellOrder);
    BargainDto endByReasonExpired(BargainDto bargainDto);
    BargainDto finalize(BargainDto bargainDto, EBargainStatus status);
    BargainDto updateResult(BargainDto bargainDto);
    Optional<List<BargainDto>> checkOnFinish();
    Optional<List<BargainDto>> getAllByStatus(EBargainStatus status);
    Optional<List<BargainDto>> getAllWithFilledBuyOrders();
    BargainDto completeBargainByReasonTimeoutBuyOrder(BargainDto troubleBargain);




}
