package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ActionInterface {
    @NotNull String getName();

    @NotNull String getCategory();

    void describe(@NotNull Map<@NotNull String, @NotNull Object> out);

    boolean useInRegions();

    boolean useInNodes();

    void executeRegionAction(MinigamePlayer player, @NotNull Region region);

    void executeNodeAction(MinigamePlayer player, @NotNull Node node);

    void saveArguments(@NotNull FileConfiguration config, @NotNull String path);

    void loadArguments(@NotNull FileConfiguration config, @NotNull String path);

    boolean displayMenu(MinigamePlayer player, Menu previous);

    void debug(MinigamePlayer p, ScriptObject obj);
}
