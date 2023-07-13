package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigamesregions.triggers.Trigger;

public interface ExecutableScriptObject extends ScriptObject {
    void execute(Trigger trigger, MinigamePlayer player);
}
