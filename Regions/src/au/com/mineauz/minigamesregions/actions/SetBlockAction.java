package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class SetBlockAction extends ActionInterface {
	
	private StringFlag type = new StringFlag("STONE", "type");
	private BooleanFlag usedur = new BooleanFlag(false, "usedur");
	private IntegerFlag dur = new IntegerFlag(0, "dur");

	@Override
	public String getName() {
		return "SET_BLOCK";
	}

	@Override
	public String getCategory() {
		return "Block Actions";
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
			Region region, Event event) {
		Location temp = region.getFirstPoint();
		for(int y = region.getFirstPoint().getBlockY(); y <= region.getSecondPoint().getBlockY(); y++){
			temp.setY(y);
			for(int x = region.getFirstPoint().getBlockX(); x <= region.getSecondPoint().getBlockX(); x++){
				temp.setX(x);
				for(int z = region.getFirstPoint().getBlockZ(); z <= region.getSecondPoint().getBlockZ(); z++){
					temp.setZ(z);
					
					BlockState bs = temp.getBlock().getState();
					bs.setType(Material.getMaterial(type.getFlag()));
					if(usedur.getFlag()){
						bs.getData().setData(dur.getFlag().byteValue());
					}
					bs.update(true);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void executeNodeAction(MinigamePlayer player,
			Node node, Event event) {
		BlockState bs = node.getLocation().getBlock().getState();
		bs.setType(Material.getMaterial(type.getFlag()));
		if(usedur.getFlag()){
			bs.getData().setData(dur.getFlag().byteValue());
		}
		bs.update(true);
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		type.saveValue(path, config);
		usedur.saveValue(path, config);
		dur.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		type.loadValue(path, config);
		usedur.loadValue(path, config);
		dur.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		//TODO: Set Block menu
		return false;
	}

}
