package by.zemich.binancebot.DAO.entity;

import by.zemich.binancebot.core.enums.EBargainStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "bargain")
public class BargainEntity {
    @Id
    private UUID uuid;

    @Column(name = "strategy")
    private String strategy;

    @CreationTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_create")
    private Timestamp dtCreate;
    @Version
    @UpdateTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_update")
    private Timestamp dtUpdate;

    @Column(name = "percentage_result")
    private BigDecimal percentageResult;
    @Column(name = "finance_result")
    private BigDecimal financeResult;

    @Column(name = "time_in_work")
    private Long timeInWork;

    @Column(name = "finish_time")
    private Timestamp finishTime;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EBargainStatus status;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "interest")
    private BigDecimal interest;

    @Column(name = "fee")
    private BigDecimal fee;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.MERGE,
            orphanRemoval = true,
            mappedBy = "bargain"
    )
    @JoinColumn(name = "bargain_uuid")
    private List<OrderEntity> orders;


}
