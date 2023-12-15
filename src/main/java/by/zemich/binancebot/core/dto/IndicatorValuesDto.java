package by.zemich.binancebot.core.dto;

import lombok.Builder;
import lombok.Data;

import java.text.MessageFormat;

@Data
@Builder
public class IndicatorValuesDto {
    private String emaSlope;
    private String bbw;
    private String rsi;
    private String adx20;
    private String adx14;
    private String cmf;
    private String nvi;
    private String ema;

    public String toString(){
        return MessageFormat.format("""
                EMASlope(20): {0} 
                BBW: {1} 
                RSI(14): {2} 
                ADX(20): {3} 
                ADX(14): {4} 
                CMF(20): {5} 
                NVI(20): {6} 
                EMA(20): {7} 
                """, emaSlope, bbw, rsi, adx20, adx14, cmf, nvi, ema);
    }


}
