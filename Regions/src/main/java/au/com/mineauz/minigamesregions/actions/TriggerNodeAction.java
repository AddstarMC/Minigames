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

public class TriggerNodeAction extends AbstractAction {
    
    private StringFlag node = new StringFlag("None", "node");

    @Override
    public String getName() {
        return "TRIGGER_NODE";
    }

    @Override
    public String getCategory() {
        return "Remote Trigger Actions";
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
    public void executeRegionAction(MinigamePlayer player,
            Region region) {
        debug(player,region);
        if(player == null || !player.isInMinigame()) return;
        Minigame mg = player.getMinigame();
        if(mg != null){
            RegionModule rmod = RegionModule.getMinigameModule(mg);
            if(rmod.hasNode(node.getFlag()))
                rmod.getNode(node.getFlag()).execute(Triggers.getTrigger("REMOTE"), player);
        }
    }

    @Override
    public void executeNodeAction(MinigamePlayer player, Node node) {
        debug(player,node);
        if(player == null || !player.isInMinigame()) return;
        Minigame mg = player.getMinigame();
        if(mg != null){
            RegionModule rmod = RegionModule.getMinigameModule(mg);
            if(rmod.hasNode(this.node.getFlag()))
                rmod.getNode(this.node.getFlag()).execute(Triggers.getTrigger("REMOTE"), player);
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
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Trigger Node", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(node.getMenuItem("Node Name", Material.ENDER_EYE));
        m.displayMenu(player);
        return true;
    }

}
