package by.zemich.binancebot.DAO.api;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface IOrderDao extends CrudRepository<OrderEntity, UUID> {

}
