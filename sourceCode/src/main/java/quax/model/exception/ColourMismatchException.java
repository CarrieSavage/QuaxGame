package quax.model.exception;

public class ColourMismatchException extends RuntimeException {
    public ColourMismatchException() {super();}
    public ColourMismatchException(String message) {
        super(message);
    }
}
