package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.material.MaterialData;

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
	public void executeRegionAction(MinigamePlayer player,
			Region region) {
		debug(player,region);
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
						MaterialData materialData = new MaterialData(temp.getBlock().getType());
						temp.getWorld().spawnFallingBlock(temp, materialData);
						temp.getBlock().setType(Material.AIR);
					}
				}
			}
		}
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Node node) {
		debug(player,node);
		if(node.getLocation().getBlock().getType() != Material.AIR){
			node.getLocation().getWorld().spawnFallingBlock(node.getLocation(),new MaterialData(
					node.getLocation().getBlock().getType()));
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
