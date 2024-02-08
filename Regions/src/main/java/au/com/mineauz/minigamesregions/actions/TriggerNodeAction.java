package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import au.com.mineauz.minigamesregions.triggers.MgRegTrigger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TriggerNodeAction extends AAction { // todo merge with TriggerRegion
    private final StringFlag node = new StringFlag("None", "node");

    protected TriggerNodeAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_TRIGGERNODE_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.REMOTE;
    }

    @Override
    public void describe(Map<String, Object> out) {
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
                rmod.getNode(node.getFlag()).execute(MgRegTrigger.REMOTE, mgPlayer);
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
                rmod.getNode(this.node.getFlag()).execute(MgRegTrigger.REMOTE, mgPlayer);
        }
    }

    @Override
    public void saveArguments(FileConfiguration config,
                              String path) {
        node.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        node.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, getDisplayname(), mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.addItem(node.getMenuItem(Material.NAME_TAG, "Node Name"));
        m.displayMenu(mgPlayer);
        return true;
    }

}
