package by.zemich.binancebot.DAO.api;

import by.zemich.binancebot.DAO.entity.MetricEntity;
import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.enums.EOrderStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IMetricDao extends CrudRepository<MetricEntity, UUID> {


}
