package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainCreateDto;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EBargainStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBargainService {
    Optional<BargainEntity> save(BargainDto bargainDto);
    BargainDto addBuyOrder(BargainDto bargainDto, OrderDto buyOrder);
    BargainDto addSellOrder(BargainDto bargainDto, OrderDto sellOrder);
    boolean existsByStatusAndSymbol(EBargainStatus status, String symbol);
    boolean existsBySymbolAndStatusNotLike(String symbol, EBargainStatus status);

    BargainDto create(BargainCreateDto bargainCreateDto);
    Optional<BargainEntity> update(BargainDto bargainDto);
    Optional<BargainEntity> endByReasonExpired(BargainDto bargainDto);
    BargainEntity finalize(BargainDto bargainDto, EBargainStatus status);
    BargainEntity updateResult(BargainDto bargainDto);
    Optional<List<BargainEntity>>  getAll();
    Optional<List<BargainEntity>> getAllWithFilledBuyOrders();
    Optional<List<BargainEntity>> getAllWithExpiredBuyOrders();
    Optional<List<BargainEntity>> checkOnFinish();
    Optional<List<BargainEntity>> getAllByStatus(EBargainStatus status);
    Optional<BargainEntity> getByUuid(UUID uuid);
    void removeByUuid(UUID uuid);




}
