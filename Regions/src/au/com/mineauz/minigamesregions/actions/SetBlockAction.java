package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuItemValue;
import au.com.mineauz.minigames.menu.MenuItemValue.IMenuItemChange;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.TriggerArea;

public class SetBlockAction extends ActionInterface {
	
	private final StringProperty type = new StringProperty("STONE", "type");
	private final BooleanProperty usedur = new BooleanProperty(false, "usedur");
	private final IntegerProperty dur = new IntegerProperty(0, "dur");
	
	public SetBlockAction() {
		properties.addProperty(type);
		properties.addProperty(usedur);
		properties.addProperty(dur);
	}

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

	@Override
	public boolean requiresPlayer() {
		return false;
	}
	
	@Override
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		if (area instanceof Region) {
			executeRegionAction(player, (Region)area);
		} else if (area instanceof Node) {
			executeNodeAction(player, (Node)area);
		}
	}
	@SuppressWarnings("deprecation")
	private void executeRegionAction(MinigamePlayer player, Region region) {
		Location temp = region.getMinCorner();
		for(int y = region.getMinCorner().getBlockY(); y <= region.getMaxCorner().getBlockY(); y++){
			temp.setY(y);
			for(int x = region.getMinCorner().getBlockX(); x <= region.getMaxCorner().getBlockX(); x++){
				temp.setX(x);
				for(int z = region.getMinCorner().getBlockZ(); z <= region.getMaxCorner().getBlockZ(); z++){
					temp.setZ(z);
					
					BlockState bs = temp.getBlock().getState();
					bs.setType(Material.getMaterial(type.getValue()));
					if(usedur.getValue()){
						bs.getData().setData(dur.getValue().byteValue());
					}
					bs.update(true);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void executeNodeAction(MinigamePlayer player, Node node) {
		BlockState bs = node.getLocation().getBlock().getState();
		bs.setType(Material.getMaterial(type.getValue()));
		if(usedur.getValue()){
			bs.getData().setData(dur.getValue().byteValue());
		}
		bs.update(true);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Set Block");
		MenuItemString typeItem = new MenuItemString("Type", Material.STONE, type);
		typeItem.setChangeHandler(new IMenuItemChange<String>() {
			@Override
			public void onChange(MenuItemValue<String> menuItem, MinigamePlayer player, String previous, String current) {
				if(Material.matchMaterial(current.toUpperCase()) == null) {
					player.sendMessage("Invalid block type!", MessageType.Error);
					
					type.setValue(previous);
				}
			}
		});
		
		m.addItem(new MenuItemBoolean("Use Durability Value", Material.ENDER_PEARL, usedur));
		m.addItem(new MenuItemInteger("Durability Value", Material.STONE, dur, 0, 15));
		m.displayMenu(player);
		return true;
	}

}
