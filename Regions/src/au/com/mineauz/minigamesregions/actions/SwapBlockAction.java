package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class SwapBlockAction extends ActionInterface {
	
	private StringFlag matchType = new StringFlag("STONE", "matchtype");
	private BooleanFlag matchData = new BooleanFlag(false, "matchdata");
	private IntegerFlag matchDataValue = new IntegerFlag(0, "matchdatavalue");
	private StringFlag toType = new StringFlag("COBBLESTONE", "totype");
	private BooleanFlag toData = new BooleanFlag(false, "todata");
	private IntegerFlag toDataValue = new IntegerFlag(0, "todatavalue");

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
					
					if(temp.getBlock().getType() == Material.getMaterial(matchType.getFlag()) &&
							(!matchData.getFlag() ||
									temp.getBlock().getData() == matchDataValue.getFlag().byteValue())){
						byte b = 0;
						if(toData.getFlag())
							b = toDataValue.getFlag().byteValue();
						BlockState bs = temp.getBlock().getState();
						bs.setType(Material.getMaterial(toType.getFlag()));
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
	public void saveArguments(FileConfiguration config,
			String path) {
		matchType.saveValue(path, config);
		matchData.saveValue(path, config);
		matchDataValue.saveValue(path, config);
		toType.saveValue(path, config);
		toData.saveValue(path, config);
		toDataValue.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		matchType.loadValue(path, config);
		matchData.loadValue(path, config);
		matchDataValue.loadValue(path, config);
		toType.loadValue(path, config);
		toData.loadValue(path, config);
		toDataValue.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Swap Block", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final MinigamePlayer fply = player;
		m.addItem(new MenuItemString("Match Block", Material.COBBLESTONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					matchType.setFlag(value.toUpperCase());
				else
					fply.sendMessage("Invalid block type!", "error");
			}
			
			@Override
			public String getValue() {
				return matchType.getFlag();
			}
		}));
		m.addItem(matchData.getMenuItem("Match Block Use Data?", Material.ENDER_PEARL));
		m.addItem(matchDataValue.getMenuItem("Match Block Data Value", Material.EYE_OF_ENDER, 0, 15));
		
		m.addItem(new MenuItemNewLine());
		
		m.addItem(new MenuItemString("To Block", Material.STONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					toType.setFlag(value.toUpperCase());
				else
					fply.sendMessage("Invalid block type!", "error");
			}
			
			@Override
			public String getValue() {
				return toType.getFlag();
			}
		}));
		m.addItem(toData.getMenuItem("To Block Use Data?", Material.ENDER_PEARL));
		m.addItem(toDataValue.getMenuItem("To Block Data Value", Material.EYE_OF_ENDER, 0, 15));
		m.displayMenu(player);
		return true;
	}

}
