package au.com.mineauz.minigamesregions.triggers;

import java.io.Serial;

public class TriggerAlreadyRegisteredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public TriggerAlreadyRegisteredException() {
        super();
    }

    public TriggerAlreadyRegisteredException(String message) {
        super(message);
    }
}
