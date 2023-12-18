package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigamesregions.triggers.Trigger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ExecutableScriptObject extends ScriptObject {
    void execute(@NotNull Trigger trigger, @Nullable MinigamePlayer player);
}
