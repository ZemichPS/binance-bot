package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.enums.EBargainStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBargainService {
    Optional<BargainEntity> create(BargainDto bargainDto);

    Optional<BargainEntity> update(BargainDto bargainDto);
    Optional<BargainEntity> end(BargainDto bargainDto);
    Optional<List<BargainEntity>>  getAll();
    Optional<List<BargainEntity>> updateOpenStatus();
    Optional<List<BargainEntity>> checkOnFinish();
    Optional<List<BargainEntity>> getAllByStatus(EBargainStatus status);
    Optional<BargainEntity> getByUuid(UUID uuid);
    void removeByUuid(UUID uuid);




}
