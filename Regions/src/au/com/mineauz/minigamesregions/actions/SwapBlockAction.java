package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemEnum;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.EnumProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.TriggerArea;

public class SwapBlockAction extends ActionInterface {
	
	private final EnumProperty<Material> matchType = new EnumProperty<Material>(Material.STONE, "matchtype");
	private final BooleanProperty matchData = new BooleanProperty(false, "matchdata");
	private final IntegerProperty matchDataValue = new IntegerProperty(0, "matchdatavalue");
	private final EnumProperty<Material> toType = new EnumProperty<Material>(Material.COBBLESTONE, "totype");
	private final BooleanProperty toData = new BooleanProperty(false, "todata");
	private final IntegerProperty toDataValue = new IntegerProperty(0, "todatavalue");
	
	public SwapBlockAction() {
		properties.addProperty(matchType);
		properties.addProperty(matchData);
		properties.addProperty(matchDataValue);
		properties.addProperty(toType);
		properties.addProperty(toData);
		properties.addProperty(toDataValue);
	}

	@Override
	public String getName() {
		return "SWAP_BLOCK";
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
		return false;
	}
	
	@Override
	public boolean requiresPlayer() {
		return false;
	}

	@Override
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		if (area instanceof Region) {
			executeRegionAction(player, (Region)area);
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
					
					if(temp.getBlock().getType() == matchType.getValue() &&
							(!matchData.getValue() ||
									temp.getBlock().getData() == matchDataValue.getValue().byteValue())){
						byte b = 0;
						if(toData.getValue())
							b = toDataValue.getValue().byteValue();
						BlockState bs = temp.getBlock().getState();
						bs.setType(toType.getValue());
						bs.getData().setData(b);
						bs.update(true);
					}
				}
			}
		}
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Swap Block");
		MenuItemEnum<Material> matchTypeItem = new MenuItemEnum<Material>("Match Block", Material.COBBLESTONE, matchType, Material.class);
		
		m.addItem(matchTypeItem);
		m.addItem(new MenuItemBoolean("Match Block Use Data?", Material.ENDER_PEARL, matchData));
		m.addItem(new MenuItemInteger("Match Block Data Value", Material.EYE_OF_ENDER, matchDataValue, 0, 15));
		
		m.addItem(new MenuItemNewLine());
		
		MenuItemEnum<Material> toTypeItem = new MenuItemEnum<Material>("To Block", Material.STONE, toType, Material.class);
		m.addItem(toTypeItem);
		m.addItem(new MenuItemBoolean("To Block Use Data?", Material.ENDER_PEARL, toData));
		m.addItem(new MenuItemInteger("To Block Data Value", Material.EYE_OF_ENDER, toDataValue, 0, 15));
		m.displayMenu(player);
		return true;
	}

}
