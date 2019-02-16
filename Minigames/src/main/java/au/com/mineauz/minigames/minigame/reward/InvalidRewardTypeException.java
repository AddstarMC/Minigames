package au.com.mineauz.minigames.minigame.reward;

public class InvalidRewardTypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidRewardTypeException(String message) {
        super(message);
    }

}
