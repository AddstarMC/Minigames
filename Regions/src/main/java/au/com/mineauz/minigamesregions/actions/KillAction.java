package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class KillAction extends AbstractAction {

    @Override
    public String getName() {
        return "KILL";
    }

    @Override
    public String getCategory() {
        return "World Actions";
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
			debug(player,node);
			if(player == null || !player.isInMinigame()) return;
			if(!player.isLiving())
				player.getPlayer().setHealth(0.0);
		}

		@Override
		public void executeRegionAction(MinigamePlayer player, Region region) {
			debug(player,region);
			if(player == null || !player.isInMinigame()) return;
			if(player.isLiving())
				player.getPlayer().setHealth(0.0);
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
