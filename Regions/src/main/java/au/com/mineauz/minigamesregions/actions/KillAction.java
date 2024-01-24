package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class KillAction extends AbstractAction {

    @Override
    public @NotNull String getName() {
        return "KILL";
    }

    @Override
    public @NotNull String getCategory() {
        return "World Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public void executeNodeAction(MinigamePlayer player,
                                  @NotNull Node node) {
        debug(player, node);
        if (player == null || !player.isInMinigame()) return;
        if (!player.isLiving())
            player.getPlayer().setHealth(0.0);
    }

    @Override
    public void executeRegionAction(MinigamePlayer player, @NotNull Region region) {
        debug(player, region);
        if (player == null || !player.isInMinigame()) return;
        if (player.isLiving())
            player.getPlayer().setHealth(0.0);
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        return false;
    }
}
