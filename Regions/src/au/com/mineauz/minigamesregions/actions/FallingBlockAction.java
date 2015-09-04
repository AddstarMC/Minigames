package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.TriggerArea;

public class FallingBlockAction extends ActionInterface {

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
	
	@Override
	public boolean requiresPlayer() {
		return false;
	}

	@Override
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		if (area instanceof Node) {
			executeNodeAction(player, (Node)area);
		} else if (area instanceof Region) {
			executeRegionAction(player, (Region)area);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void executeRegionAction(MinigamePlayer player, Region region) {
		Location temp = region.getMinCorner();
		for(int y = region.getMinCorner().getBlockY(); 
				y <= region.getMaxCorner().getBlockY();
				y++){
			temp.setY(y);
			for(int x = region.getMinCorner().getBlockX();
					x <= region.getMaxCorner().getBlockX();
					x++){
				temp.setX(x);
				for(int z = region.getMinCorner().getBlockZ();
						z <= region.getMaxCorner().getBlockZ();
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
	private void executeNodeAction(MinigamePlayer player, Node node) {
		if(node.getLocation().getBlock().getType() != Material.AIR){
			node.getLocation().getWorld().spawnFallingBlock(node.getLocation(), 
					node.getLocation().getBlock().getType(), 
					node.getLocation().getBlock().getData());
			node.getLocation().getBlock().setType(Material.AIR);
		}
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		return false;
	}

}
