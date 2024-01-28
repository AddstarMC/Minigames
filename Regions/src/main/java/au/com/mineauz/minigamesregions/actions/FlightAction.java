package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class FlightAction extends AbstractAction {

    private final BooleanFlag setFly = new BooleanFlag(true, "setFlying");
    private final BooleanFlag startFly = new BooleanFlag(false, "startFly");

    @Override
    public @NotNull String getName() {
        return "FLIGHT";
    }

    @Override
    public @NotNull String getCategory() {
        return "Player Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        out.put("Allow Flight", setFly.getFlag());
        out.put("Flight On", startFly.getFlag());
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

    private void execute(MinigamePlayer player) {
        player.setCanFly(setFly.getFlag());
        if (setFly.getFlag())
            player.getPlayer().setFlying(startFly.getFlag());
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        setFly.saveValue(path, config);
        startFly.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
        setFly.loadValue(path, config);
        startFly.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Flight", mgPlayer);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(setFly.getMenuItem("Set Flight Mode", Material.FEATHER));
        m.addItem(startFly.getMenuItem("Set Flying", Material.FEATHER, List.of("Set Flight Mode must be", "true to use this")));
        m.displayMenu(mgPlayer);
        return true;
    }

}
