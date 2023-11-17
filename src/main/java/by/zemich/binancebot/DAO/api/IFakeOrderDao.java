package by.zemich.binancebot.DAO.api;

import by.zemich.binancebot.DAO.entity.FakeBargainEntity;
import by.zemich.binancebot.core.enums.EOrderStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFakeOrderDao extends CrudRepository<FakeBargainEntity, UUID> {

    Optional<List<FakeBargainEntity>> findAllByStatus(EOrderStatus status);
}
