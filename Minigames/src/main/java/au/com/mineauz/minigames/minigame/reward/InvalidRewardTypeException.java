package au.com.mineauz.minigames.minigame.reward;

import java.io.Serial;

public class InvalidRewardTypeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidRewardTypeException(String message) {
        super(message);
    }

}
