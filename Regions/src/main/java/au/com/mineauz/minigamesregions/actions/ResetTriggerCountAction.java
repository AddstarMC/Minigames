package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class ResetTriggerCountAction extends AbstractAction {

    @Override
    public String getName() {
        return "RESET_TRIGGER_COUNT";
    }

    @Override
    public String getCategory() {
        return "Region/Node Actions";
    }

    @Override
    public void describe(Map<String, Object> out) {
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
        debug(player, region);
        for (RegionExecutor ex : region.getExecutors())
            ex.setTriggerCount(0);
    }

    @Override
    public void executeNodeAction(MinigamePlayer player, Node node) {
        debug(player, node);
        for (NodeExecutor ex : node.getExecutors())
            ex.setTriggerCount(0);
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
