package by.zemich.binancebot.DAO.api;

import by.zemich.binancebot.DAO.entity.FakeOrderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface IFakeOrderDao extends CrudRepository<FakeOrderEntity, UUID> {
}
