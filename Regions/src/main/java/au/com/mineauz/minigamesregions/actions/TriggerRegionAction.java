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

public class TriggerRegionAction extends AbstractAction {
    private final StringFlag region = new StringFlag("None", "region");

    @Override
    public String getName() {
        return "TRIGGER_REGION";
    }

    @Override
    public String getCategory() {
        return "Remote Trigger Actions";
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Region", region.getFlag());
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
            if (rmod.hasRegion(this.region.getFlag()))
                rmod.getRegion(this.region.getFlag()).execute(Triggers.getTrigger("REMOTE"), mgPlayer);
        }
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {
        debug(mgPlayer, node);
        if (mgPlayer == null || !mgPlayer.isInMinigame()) return;
        Minigame mg = mgPlayer.getMinigame();
        if (mg != null) {
            RegionModule rmod = RegionModule.getMinigameModule(mg);
            if (rmod.hasRegion(region.getFlag()))
                rmod.getRegion(region.getFlag()).execute(Triggers.getTrigger("REMOTE"), mgPlayer);
        }
    }

    @Override
    public void saveArguments(FileConfiguration config,
                              String path) {
        region.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        region.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Trigger Node", mgPlayer);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(region.getMenuItem("Region Name", Material.ENDER_EYE));
        m.displayMenu(mgPlayer);
        return true;
    }

}
