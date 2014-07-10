package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class FallingBlockAction implements ActionInterface {

	@Override
	public String getName() {
		return "FALLING_BLOCK";
	}

	@Override
	public String getCategory() {
		return "World Actions";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void executeRegionAction(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
		Location temp = region.getFirstPoint();
		for(int y = region.getFirstPoint().getBlockY(); 
				y <= region.getSecondPoint().getBlockY();
				y++){
			temp.setY(y);
			for(int x = region.getFirstPoint().getBlockX();
					x <= region.getSecondPoint().getBlockX();
					x++){
				temp.setX(x);
				for(int z = region.getFirstPoint().getBlockZ();
						z <= region.getSecondPoint().getBlockZ();
						z++){
					temp.setZ(z);
					if(temp.getBlock().getType() != Material.AIR){
						temp.getWorld().spawnFallingBlock(temp, temp.getBlock().getType(), temp.getBlock().getData());
						temp.getBlock().setType(Material.AIR);
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		if(node.getLocation().getBlock().getType() != Material.AIR){
			node.getLocation().getWorld().spawnFallingBlock(node.getLocation(), 
					node.getLocation().getBlock().getType(), 
					node.getLocation().getBlock().getData());
			node.getLocation().getBlock().setType(Material.AIR);
		}
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		return null;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		return null;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		return false;
	}

}
