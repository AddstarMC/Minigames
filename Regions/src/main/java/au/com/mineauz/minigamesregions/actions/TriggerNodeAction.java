package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.triggers.Triggers;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TriggerNodeAction extends AbstractAction {

    private final StringFlag node = new StringFlag("None", "node");

    @Override
    public @NotNull String getName() {
        return "TRIGGER_NODE";
    }

    @Override
    public @NotNull String getCategory() {
        return "Remote Trigger Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        out.put("Node", node.getName());
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
        if (mgPlayer == null || !mgPlayer.isInMinigame()) return;
        Minigame mg = mgPlayer.getMinigame();
        if (mg != null) {
            RegionModule rmod = RegionModule.getMinigameModule(mg);
            if (rmod.hasNode(node.getFlag()))
                rmod.getNode(node.getFlag()).execute(Triggers.getTrigger("REMOTE"), mgPlayer);
        }
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {
        debug(mgPlayer, node);
        if (mgPlayer == null || !mgPlayer.isInMinigame()) return;
        Minigame mg = mgPlayer.getMinigame();
        if (mg != null) {
            RegionModule rmod = RegionModule.getMinigameModule(mg);
            if (rmod.hasNode(this.node.getFlag()))
                rmod.getNode(this.node.getFlag()).execute(Triggers.getTrigger("REMOTE"), mgPlayer);
        }
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        node.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        node.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Trigger Node", mgPlayer);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(node.getMenuItem("Node Name", Material.ENDER_EYE));
        m.displayMenu(mgPlayer);
        return true;
    }

}
