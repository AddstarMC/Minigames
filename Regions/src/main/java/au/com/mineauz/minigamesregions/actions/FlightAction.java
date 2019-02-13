package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class FlightAction extends AbstractAction{
    
    private BooleanFlag setFly = new BooleanFlag(true, "setFlying");
    private BooleanFlag startFly = new BooleanFlag(false, "startFly");

    @Override
    public String getName() {
        return "FLIGHT";
    }

    @Override
    public String getCategory() {
        return "Player Actions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
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
    public void executeRegionAction(MinigamePlayer player, Region region) {
        debug(player,region);
        execute(player);
    }

    @Override
    public void executeNodeAction(MinigamePlayer player, Node node) {
        debug(player,node);
        execute(player);
    }
    
    private void execute(MinigamePlayer player){
        player.setCanFly(setFly.getFlag());
        if(setFly.getFlag())
            player.getPlayer().setFlying(startFly.getFlag());
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        setFly.saveValue(path, config);
        startFly.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        setFly.loadValue(path, config);
        startFly.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Flight", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(setFly.getMenuItem("Set Flight Mode", Material.FEATHER));
        m.addItem(startFly.getMenuItem("Set Flying", Material.FEATHER, MinigameUtils.stringToList("Set Flight Mode must be;true to use this")));
        m.displayMenu(player);
        return true;
    }

}
