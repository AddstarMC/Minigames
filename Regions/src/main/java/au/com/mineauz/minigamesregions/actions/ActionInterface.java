package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ActionInterface {
    @NotNull String getName();

    @NotNull Component getDisplayname();

    @NotNull IActionCategory getCategory();

    void describe(Map<String, Object> out);

    boolean useInRegions();

    boolean useInNodes();

    void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region);

    void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node);

    void saveArguments(FileConfiguration config, String path);

    void loadArguments(FileConfiguration config, String path);

    boolean displayMenu(@NotNull MinigamePlayer mgPlayer, @Nullable Menu previous);

    void debug(MinigamePlayer mgPlayer, ScriptObject scriptObject);
}
