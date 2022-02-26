package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;

public class MinigamesBroadcastEvent extends AbstractMinigameEvent {
    private String message;
    private String prefix;
    private boolean cancelled = false;

    public MinigamesBroadcastEvent(String prefix, String message, Minigame minigame) {
        super(minigame);
        this.message = message;
        this.prefix = prefix;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageWithPrefix() {
        return prefix + " " + message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
