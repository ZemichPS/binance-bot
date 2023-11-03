package by.zemich.binancebot.DAO.api;

import by.zemich.binancebot.DAO.entity.FakeOrderEntity;
import by.zemich.binancebot.core.enums.EOrderStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFakeOrderDao extends CrudRepository<FakeOrderEntity, UUID> {

    Optional<List<FakeOrderEntity>> findAllByStatus(EOrderStatus status);
}
