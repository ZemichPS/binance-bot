package by.zemich.binancebot.DAO.api;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOrderDao extends CrudRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByOrderId(Long id);

    List<OrderEntity> findAll();

}
