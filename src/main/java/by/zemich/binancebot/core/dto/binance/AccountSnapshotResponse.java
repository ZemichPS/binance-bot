package by.zemich.binancebot.core.dto.binance;

import java.sql.Timestamp;
import java.util.List;

public class AccountSnapshotResponse {
    private String code;
    private String msg;
    private List<SnapshotVosDto> snapshotVos;
    private String type;
    private Timestamp updateTime;


}
