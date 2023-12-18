package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SetEnabledAction extends AbstractAction {
    private final BooleanFlag state = new BooleanFlag(false, "state");

    @Override
    public String getName() {
        return "SET_ENABLED";
    }

    @Override
    public String getCategory() {
        return "Region/Node Actions";
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Enabled", state.getFlag());
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
        region.setEnabled(state.getFlag());
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {
        debug(mgPlayer, node);
        node.setEnabled(state.getFlag());
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        state.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        state.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Set Enabled", mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.addItem(state.getMenuItem("Set Enabled", Material.ENDER_PEARL));
        m.displayMenu(mgPlayer);
        return true;
    }

}
