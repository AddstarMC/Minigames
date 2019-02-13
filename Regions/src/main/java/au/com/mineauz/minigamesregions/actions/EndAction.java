package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class EndAction extends AbstractAction {

    @Override
    public String getName() {
        return "END";
    }

    @Override
    public String getCategory() {
        return "Minigame Actions";
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
    public void executeNodeAction(MinigamePlayer player,
            Node node) {
        execute(player);
        debug(player,node);
    }

    @Override
    public void executeRegionAction(MinigamePlayer player, Region region) {
        debug(player,region);
        execute(player);
    }
    
    private void execute(MinigamePlayer player){
        if(player == null || !player.isInMinigame()) return;
        setWinnersLosers(player);
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
