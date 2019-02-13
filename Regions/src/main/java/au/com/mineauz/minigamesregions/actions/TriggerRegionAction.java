package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.triggers.Triggers;

public class TriggerRegionAction extends AbstractAction {
    
    private StringFlag region = new StringFlag("None", "region");

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
    public void executeRegionAction(MinigamePlayer player,
            Region region) {
        debug(player,region);
        if(player == null || !player.isInMinigame()) return;
        Minigame mg = player.getMinigame();
        if(mg != null){
            RegionModule rmod = RegionModule.getMinigameModule(mg);
            if(rmod.hasRegion(this.region.getFlag()))
                rmod.getRegion(this.region.getFlag()).execute(Triggers.getTrigger("REMOTE"), player);
        }
    }

    @Override
    public void executeNodeAction(MinigamePlayer player, Node node) {
        debug(player,node);
        if(player == null || !player.isInMinigame()) return;
        Minigame mg = player.getMinigame();
        if(mg != null){
            RegionModule rmod = RegionModule.getMinigameModule(mg);
            if(rmod.hasRegion(region.getFlag()))
                rmod.getRegion(region.getFlag()).execute(Triggers.getTrigger("REMOTE"), player);
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
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Trigger Node", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(region.getMenuItem("Region Name", Material.ENDER_EYE));
        m.displayMenu(player);
        return true;
    }

}
