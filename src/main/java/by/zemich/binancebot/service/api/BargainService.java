package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainCreateDto;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.enums.EBargainStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BargainService {
    BargainEntity save(BargainDto bargainDto);
    Optional<BargainEntity> getByUuid(UUID uuid);
    List<BargainEntity>  getAll();
    List<BargainEntity> getAllByStatus(EBargainStatus status);
    boolean existsBySymbolAndStatusLike(String symbol, EBargainStatus bargainStatus);
    BargainEntity update(BargainDto bargainDto);
    void deleteByUuid(UUID uuid);




}
