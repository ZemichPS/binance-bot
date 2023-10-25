package by.zemich.binancebot.core.exeption;

public class NoSuchEntityException extends RuntimeException{
    public NoSuchEntityException() {
        super();
    }

    public NoSuchEntityException(String message) {
        super("Entity was not founded");
    }
}
