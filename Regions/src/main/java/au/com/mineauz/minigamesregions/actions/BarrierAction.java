package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BarrierAction extends AbstractAction {

    @Override
    public @NotNull String getName() {
        return "BARRIER";
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
        return false;
    }

    @Override
    public void executeNodeAction(MinigamePlayer player,
                                  @NotNull Node node) {
        debug(player, node);
    }

    @Override
    public void executeRegionAction(MinigamePlayer player, @NotNull Region region) {
        debug(player, region);
        if (player == null || !player.isInMinigame()) return;
        Location o = player.getLocation().clone();
        Location[] locs = {region.getFirstPoint(), region.getSecondPoint()};
        double xdis1 = Math.abs(o.getX() - locs[0].getX());
        double ydis1 = Math.abs(o.getY() - locs[0].getY());
        double zdis1 = Math.abs(o.getZ() - locs[0].getZ());
        double xdis2 = Math.abs(o.getX() - (locs[1].getX() + 1));
        double ydis2 = Math.abs(o.getY() - (locs[1].getY() + 1));
        double zdis2 = Math.abs(o.getZ() - (locs[1].getZ() + 1));
        boolean isMinX = false;
        boolean isMinY = false;
        boolean isMinZ = false;
        double xval;
        double yval;
        double zval;
        if (xdis1 < xdis2) {
            isMinX = true;
            xval = xdis1;
        } else
            xval = xdis2;
        if (ydis1 < ydis2) {
            isMinY = true;
            yval = ydis1;
        } else
            yval = ydis2;
        if (zdis1 < zdis2) {
            isMinZ = true;
            zval = zdis1;
        } else
            zval = zdis2;
        if (xval < yval && xval < zval) {
            if (region.getPlayers().contains(player)) {
                if (isMinX)
                    o.setX(o.getX() - 0.5);
                else
                    o.setX(o.getX() + 0.5);
            } else {
                if (isMinX)
                    o.setX(o.getX() + 0.5);
                else
                    o.setX(o.getX() - 0.5);
            }
        } else if (yval < xval && yval < zval) {
            if (region.getPlayers().contains(player)) {
                if (isMinY)
                    o.setY(o.getY() - 0.5);
                else
                    o.setY(o.getY() + 0.5);
            } else {
                if (isMinY)
                    o.setY(o.getY() + 0.5);
                else
                    o.setY(o.getY() - 0.5);
            }
        } else if (zval < xval && zval < yval) {
            if (region.getPlayers().contains(player)) {
                if (isMinZ)
                    o.setZ(o.getZ() - 0.5);
                else
                    o.setZ(o.getZ() + 0.5);
            } else {
                if (isMinZ)
                    o.setZ(o.getZ() + 0.5);
                else
                    o.setZ(o.getZ() - 0.5);
            }
        }
        player.teleport(o);
        if (region.getPlayers().contains(player))
            region.removePlayer(player);
        else
            region.addPlayer(player);
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        // None
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        //None
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        return false;
    }
}
