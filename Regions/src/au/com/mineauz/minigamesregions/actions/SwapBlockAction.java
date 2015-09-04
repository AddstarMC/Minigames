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
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

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

	@SuppressWarnings("deprecation")
	@Override
	public void executeRegionAction(MinigamePlayer player,
			Region region) {
		Location temp = region.getFirstPoint();
		for(int y = region.getFirstPoint().getBlockY(); y <= region.getSecondPoint().getBlockY(); y++){
			temp.setY(y);
			for(int x = region.getFirstPoint().getBlockX(); x <= region.getSecondPoint().getBlockX(); x++){
				temp.setX(x);
				for(int z = region.getFirstPoint().getBlockZ(); z <= region.getSecondPoint().getBlockZ(); z++){
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
	public void executeNodeAction(MinigamePlayer player,
			Node node) {
		
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
