package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class TeleportAction extends AbstractAction{

    @Override
    public String getName() {
        return "TELEPORT";
    }

    @Override
    public String getCategory() {
        return "Player Actions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
    }

    @Override
    public boolean useInRegions() {
        return false;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public void executeRegionAction(MinigamePlayer player, Region region) {
        debug(player,region);
    }

    @Override
    public void executeNodeAction(MinigamePlayer player, Node node) {
        debug(player,node);
        player.teleport(node.getLocation());
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        return false;
    }

}
