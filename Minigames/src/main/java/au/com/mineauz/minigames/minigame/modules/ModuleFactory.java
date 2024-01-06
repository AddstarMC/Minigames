package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.minigame.Minigame;
import org.jetbrains.annotations.NotNull;

public interface ModuleFactory {
    @NotNull MinigameModule makeNewModule(Minigame minigame);

    @NotNull String getName();
}
