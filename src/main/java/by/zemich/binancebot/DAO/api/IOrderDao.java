package by.zemich.binancebot.DAO.api;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.enums.EOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOrderDao extends CrudRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByOrderId(Long id);
    Optional<List<OrderEntity>> findBySymbol(@Param("symbol") String symbol);
    Optional<OrderEntity> findBySymbolAndOrderId(String symbol,  Long orderId);
    Optional<OrderEntity> findByUuid(UUID uuid);


    Optional<List<OrderEntity>> findByStatus(@Param("status") EOrderStatus orderStatus);

    List<OrderEntity> findAll();


}
