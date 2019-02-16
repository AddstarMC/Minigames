package au.com.mineauz.minigames.tool;

public class InvalidToolModeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidToolModeException() {
        super();
    }

    public InvalidToolModeException(String message) {
        super(message);
    }

}
