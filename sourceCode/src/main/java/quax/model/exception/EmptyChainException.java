package quax.model.exception;

public class EmptyChainException extends RuntimeException {
    public EmptyChainException() {super();}
    public EmptyChainException(String message) {
        super(message);
    }
}
