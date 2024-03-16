package by.zemich.binancebot.DAO.api;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.enums.EBargainStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BargainDao extends JpaRepository<BargainEntity, UUID>, PagingAndSortingRepository<BargainEntity, UUID> {

    List<BargainEntity> findAll();
    boolean existsByStatusAndSymbol(EBargainStatus status, String symbol);
    boolean existsBySymbolAndStatusNotLike(String symbol, EBargainStatus status);
    boolean existsBySymbolAndStatusLike(String symbol, EBargainStatus bargainStatus);
    boolean existsBySymbolAndStatusStartsWith(String symbol, String beginningOfStatus);
    List<BargainEntity> findAllByStatus(EBargainStatus status);
    List<BargainEntity> findAllByPercentageResultGreaterThan(BigDecimal percentageResult);

}
