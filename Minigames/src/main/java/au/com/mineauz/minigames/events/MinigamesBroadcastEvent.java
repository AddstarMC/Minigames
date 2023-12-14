package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class MinigamesBroadcastEvent extends AbstractCancellableMinigameEvent {
    private final @NotNull Component prefix;
    private @NotNull Component message;
    private boolean cancelled = false;

    public MinigamesBroadcastEvent(@NotNull Component prefix, @NotNull Component message, Minigame minigame) {
        super(minigame);
        this.message = message;
        this.prefix = prefix;
    }

    public @NotNull Component getMessage() {
        return message;
    }

    public void setMessage(@NotNull Component message) {
        this.message = message;
    }

    public Component getMessageWithPrefix() {
        return prefix.append(Component.space()).append(message);
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
