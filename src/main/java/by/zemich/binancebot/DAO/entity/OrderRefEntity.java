package by.zemich.binancebot.DAO.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "orders_ref")
public class OrderRefEntity {
    @Id
    private UUID uuid;


    public OrderRefEntity() {
    }

    public OrderRefEntity(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
