package himluck;

public class RuntimeError extends RuntimeException {
    final Token token;

    RuntimeError(Token Token, String message) {
        super(message);
        this.token = Token;
    }
}
