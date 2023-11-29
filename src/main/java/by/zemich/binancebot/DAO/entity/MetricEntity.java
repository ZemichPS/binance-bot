package by.zemich.binancebot.DAO.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity(name = "metrics")
public class MetricEntity {
    @Id
    @Column(name = "uuid")
    private UUID uuid;
    @Version
    @UpdateTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_update")
    private Timestamp dtUpdate;
    @CreationTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_create")
    private Timestamp dtCreate;

    @Column(name = "uuid_bargain")
    private UUID bargainUuid;

    @Column(name = "ema_slope")
    private String emaSlope;
    private String bbw;
    private String rsi;
    private String adx20;
    private String adx14;
    private String cmf;

    public MetricEntity() {
    }

    public MetricEntity(UUID uuid, Timestamp dtUpdate, Timestamp dtCreate, UUID bargainUuid, String emaSlope, String bbw, String rsi, String adx20, String adx14, String cmf) {
        this.uuid = uuid;
        this.dtUpdate = dtUpdate;
        this.dtCreate = dtCreate;
        this.bargainUuid = bargainUuid;
        this.emaSlope = emaSlope;
        this.bbw = bbw;
        this.rsi = rsi;
        this.adx20 = adx20;
        this.adx14 = adx14;
        this.cmf = cmf;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Timestamp getDtUpdate() {
        return dtUpdate;
    }

    public void setDtUpdate(Timestamp dtUpdate) {
        this.dtUpdate = dtUpdate;
    }

    public Timestamp getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(Timestamp dtCreate) {
        this.dtCreate = dtCreate;
    }

    public String getEmaSlope() {
        return emaSlope;
    }

    public void setEmaSlope(String emaSlope) {
        this.emaSlope = emaSlope;
    }

    public String getBbw() {
        return bbw;
    }

    public void setBbw(String bbw) {
        this.bbw = bbw;
    }

    public String getRsi() {
        return rsi;
    }

    public void setRsi(String rsi) {
        this.rsi = rsi;
    }

    public String getAdx20() {
        return adx20;
    }

    public void setAdx20(String adx20) {
        this.adx20 = adx20;
    }

    public String getAdx14() {
        return adx14;
    }

    public void setAdx14(String adx14) {
        this.adx14 = adx14;
    }

    public String getCmf() {
        return cmf;
    }

    public void setCmf(String cmf) {
        this.cmf = cmf;
    }

    public UUID getBargainUuid() {
        return bargainUuid;
    }

    public void setBargainUuid(UUID bargainUuid) {
        this.bargainUuid = bargainUuid;
    }
}
