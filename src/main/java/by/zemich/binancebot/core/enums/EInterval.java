package by.zemich.binancebot.core.enums;

import java.util.Arrays;

public enum EInterval {

    S1("1s"),
    M1("1m"),
    M3("3m"),
    M5("5m"),
    M10("10m"),
    M15("15m"),
    M30("30m"),
    H1("1h"),
    H2("2h"),
    H4("4h"),
    H6("6h"),
    H8("8h"),
    H12("h12"),
    D1("1d"),
    D3("3d"),
    W1("1w"),
    MONTH_1("1M");

    private String parameterName;


    EInterval(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public String toString() {
        return parameterName;
    }

    public static EInterval getInterval(String value){
        return Arrays.stream(EInterval.values()).filter(e-> e.parameterName.equals(value)).findFirst().orElse(null);
    }


}
