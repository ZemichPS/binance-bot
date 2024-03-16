package by.zemich.binancebot.core.exeption;

public class AssetNotAvailableException extends RuntimeException{
    public AssetNotAvailableException() {
    }

    public AssetNotAvailableException(String message) {
        super("Asset %s doesnt available".formatted(message));
    }


}
