package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.FloatFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Main;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class VelocityAction extends AbstractAction {

    private final FloatFlag x = new FloatFlag(0f, "xv");
    private final FloatFlag y = new FloatFlag(5f, "yv");
    private final FloatFlag z = new FloatFlag(0f, "zv");

    @Override
    public @NotNull String getName() {
        return "VELOCITY";
    }

    @Override
    public @NotNull String getCategory() {
        return "Player Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        out.put("Velocity", x.getFlag() + "," + y.getFlag() + "," + z.getFlag());
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
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        debug(mgPlayer, region);
        execute(mgPlayer);
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {
        debug(mgPlayer, node);
        execute(mgPlayer);
    }

    private void execute(final MinigamePlayer player) {
        if (player == null) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> player.getPlayer().setVelocity(new Vector(x.getFlag(), y.getFlag(), z.getFlag())));
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        x.saveValue(path, config);
        y.saveValue(path, config);
        z.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
        x.loadValue(path, config);
        y.loadValue(path, config);
        z.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Velocity", mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.addItem(x.getMenuItem("X Velocity", Material.STONE, 0.5d, 1d, null, null));
        m.addItem(y.getMenuItem("Y Velocity", Material.STONE, 0.5d, 1d, null, null));
        m.addItem(z.getMenuItem("Z Velocity", Material.STONE, 0.5d, 1d, null, null));
        m.displayMenu(mgPlayer);
        return true;
    }

}
