package au.com.mineauz.minigames.sounds;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public enum MGSounds { //todo undo making this a plain enum and allow registeration of sounds again
    TIMER_TICK(new MGSound(Sound.BLOCK_NOTE_BLOCK_HAT, 10f, 1.5f)),
    WIN(new MGSound(Sound.ENTITY_PLAYER_LEVELUP)),
    LOSE(new MGSound(Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 0.2f, 3, 5L)),
    GAME_START(new MGSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP));

    private final @NotNull MGSound sound;

    MGSounds(@NotNull MGSound sound) {
        this.sound = sound;
    }

    public @NotNull MGSound getSound() {
        return sound;
    }

}
