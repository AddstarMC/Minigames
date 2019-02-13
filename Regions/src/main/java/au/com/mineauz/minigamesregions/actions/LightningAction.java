package au.com.mineauz.minigamesregions.actions;

import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class LightningAction extends AbstractAction{
    
    private BooleanFlag effect = new BooleanFlag(false, "effect");

    @Override
    public String getName() {
        return "LIGHTNING";
    }

    @Override
    public String getCategory() {
        return "World Actions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        out.put("Effect Only", effect.getFlag());
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
        
        if(effect.getFlag())
            loc.getWorld().strikeLightningEffect(loc);
        else
            loc.getWorld().strikeLightning(loc);
    }

    @Override
    public void executeNodeAction(MinigamePlayer player, Node node) {
        debug(player,node);
        if(effect.getFlag())
            node.getLocation().getWorld().strikeLightningEffect(node.getLocation());
        else
            node.getLocation().getWorld().strikeLightning(node.getLocation());
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        effect.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        effect.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Lightning", player);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.addItem(effect.getMenuItem("Effect Only", Material.ENDER_PEARL));
        m.displayMenu(player);
        return true;
    }

}
