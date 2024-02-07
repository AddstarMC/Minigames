package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.FloatFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;

public class ExplodeAction extends AbstractAction {

    private final FloatFlag power = new FloatFlag(4f, "power");
    private final BooleanFlag fire = new BooleanFlag(false, "fire");

    @Override
    public String getName() {
        return "EXPLODE";
    }

    @Override
    public String getCategory() {
        return "World Actions";
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Power", power.getFlag());
        out.put("With Fire", fire.getFlag());
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
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer,
                                    @NotNull Region region) {
        debug(mgPlayer, region);
        Random rand = new Random();
        double xrand = rand.nextDouble() *
                (region.getSecondPoint().getBlockX() - region.getFirstPoint().getBlockX()) +
                region.getFirstPoint().getBlockX();
        double yrand = rand.nextDouble() *
                (region.getSecondPoint().getBlockY() - region.getFirstPoint().getBlockY()) +
                region.getFirstPoint().getBlockY();
        double zrand = rand.nextDouble() *
                (region.getSecondPoint().getBlockZ() - region.getFirstPoint().getBlockZ()) +
                region.getFirstPoint().getBlockZ();

        Location loc = region.getFirstPoint();
        loc.setX(xrand);
        loc.setY(yrand);
        loc.setZ(zrand);
        loc.getWorld().createExplosion(loc, power.getFlag(), fire.getFlag());
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer,
                                  @NotNull Node node) {
        debug(mgPlayer, node);
        node.getLocation().getWorld().createExplosion(node.getLocation(), power.getFlag(), fire.getFlag());
    }

    @Override
    public void saveArguments(FileConfiguration config,
                              String path) {
        power.saveValue(path, config);
        fire.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        power.loadValue(path, config);
        fire.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Explode", mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.addItem(power.getMenuItem("Explosion Power", Material.TNT));
        m.addItem(fire.getMenuItem("Cause Fire", Material.FLINT_AND_STEEL));
        m.displayMenu(mgPlayer);
        return true;
    }

}
