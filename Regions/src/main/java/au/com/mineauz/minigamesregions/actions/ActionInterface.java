package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ActionInterface {
    @NotNull String getName();

    @NotNull String getCategory();

    void describe(@NotNull Map<@NotNull String, @NotNull Object> out);

    boolean useInRegions();

    boolean useInNodes();

    void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region);

    void executeNodeAction(@NotNull MinigamePlayer mgPlayer, @NotNull Node node);

    void saveArguments(@NotNull FileConfiguration config, @NotNull String path);

    void loadArguments(@NotNull FileConfiguration config, @NotNull String path);

    boolean displayMenu(@NotNull MinigamePlayer mgPlayer, @Nullable Menu previous);

    void debug(MinigamePlayer mgPlayer, ScriptObject scriptObject);
}
