package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.config.FloatFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigamesregions.Main;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class VelocityAction extends AbstractAction{
    
    private FloatFlag x = new FloatFlag(0f, "xv");
    private FloatFlag y = new FloatFlag(5f, "yv");
    private FloatFlag z = new FloatFlag(0f, "zv");

    @Override
    public String getName() {
        return "VELOCITY";
    }

    @Override
    public String getCategory() {
        return "Player Actions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
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
    public void executeRegionAction(MinigamePlayer player, Region region) {
        debug(player,region);
        execute(player);
    }

    @Override
    public void executeNodeAction(MinigamePlayer player, Node node) {
        debug(player,node);
        execute(player);
    }
    
    private void execute(final MinigamePlayer player){
        if(player == null) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> player.getPlayer().setVelocity(new Vector(x.getFlag(), y.getFlag(), z.getFlag())));
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        x.saveValue(path, config);
        y.saveValue(path, config);
        z.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        x.loadValue(path, config);
        y.loadValue(path, config);
        z.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Velocity", player);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.addItem(x.getMenuItem("X Velocity", Material.STONE, 0.5d, 1d, null, null));
        m.addItem(y.getMenuItem("Y Velocity", Material.STONE, 0.5d, 1d, null, null));
        m.addItem(z.getMenuItem("Z Velocity", Material.STONE, 0.5d, 1d, null, null));
        m.displayMenu(player);
        return true;
    }

}
