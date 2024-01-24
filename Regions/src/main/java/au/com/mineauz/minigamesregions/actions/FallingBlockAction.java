package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FallingBlockAction extends AbstractAction {

    @Override
    public @NotNull String getName() {
        return "FALLING_BLOCK";
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
    public void executeRegionAction(MinigamePlayer player,
                                    @NotNull Region region) {
        debug(player, region);
        Location temp = region.getFirstPoint();
        for (int y = region.getFirstPoint().getBlockY();
             y <= region.getSecondPoint().getBlockY();
             y++) {
            temp.setY(y);
            for (int x = region.getFirstPoint().getBlockX();
                 x <= region.getSecondPoint().getBlockX();
                 x++) {
                temp.setX(x);
                for (int z = region.getFirstPoint().getBlockZ();
                     z <= region.getSecondPoint().getBlockZ();
                     z++) {
                    temp.setZ(z);
                    if (temp.getBlock().getType() != Material.AIR) {
                        temp.getWorld().spawnFallingBlock(temp, temp.getBlock().getBlockData());
                        temp.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    @Override
    public void executeNodeAction(MinigamePlayer player,
                                  @NotNull Node node) {
        debug(player, node);
        if (node.getLocation().getBlock().getType() != Material.AIR) {
            node.getLocation().getWorld().spawnFallingBlock(node.getLocation(),
                    node.getLocation().getBlock().getBlockData());
            node.getLocation().getBlock().setType(Material.AIR);
        }
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        return false;
    }
}
