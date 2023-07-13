package au.com.mineauz.minigamesregions.triggers;

import java.io.Serial;

public class InvalidTriggerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidTriggerException() {
        super();
    }

    public InvalidTriggerException(String message) {
        super(message);
    }

}
