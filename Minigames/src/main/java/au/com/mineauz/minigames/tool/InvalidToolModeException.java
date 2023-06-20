package au.com.mineauz.minigames.tool;

import java.io.Serial;

public class InvalidToolModeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidToolModeException() {
        super();
    }

    public InvalidToolModeException(String message) {
        super(message);
    }

}
