package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemInteger;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemNewLine;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemString;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class SwapBlockAction implements ActionInterface {

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
			Map<String, Object> args, Region region, Event event) {
		Location temp = region.getFirstPoint();
		for(int y = region.getFirstPoint().getBlockY(); y <= region.getSecondPoint().getBlockY(); y++){
			temp.setY(y);
			for(int x = region.getFirstPoint().getBlockX(); x <= region.getSecondPoint().getBlockX(); x++){
				temp.setX(x);
				for(int z = region.getFirstPoint().getBlockZ(); z <= region.getSecondPoint().getBlockZ(); z++){
					temp.setZ(z);
					
					if(temp.getBlock().getType() == Material.getMaterial((String)args.get("a_swapblockmatch")) &&
							(!(Boolean)args.get("a_swapblockmatchdata") ||
									temp.getBlock().getData() == (Byte)args.get("a_swapblockmatchdatavalue"))){
						byte b = 0;
						if((Boolean)args.get("a_swapblocktodata"))
							b = (Byte)args.get("a_swapblocktodatavalue");
						BlockState bs = temp.getBlock().getState();
						bs.setType(Material.getMaterial((String)args.get("a_swapblockto")));
						bs.getData().setData(b);
						bs.update(true);
					}
				}
			}
		}
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_swapblockmatch", "STONE");
		args.put("a_swapblockmatchdata", false);
		args.put("a_swapblockmatchdatavalue", (byte)0);
		args.put("a_swapblockto", "STONE");
		args.put("a_swapblocktodata", false);
		args.put("a_swapblocktodatavalue", (byte)0);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_swapblockmatch", args.get("a_swapblockmatch"));
		config.set(path + ".a_swapblockmatchdata", args.get("a_swapblockmatchdata"));
		config.set(path + ".a_swapblockmatchdatavalue", args.get("a_swapblockmatchdatavalue"));
		config.set(path + ".a_swapblockto", args.get("a_swapblockto"));
		config.set(path + ".a_swapblocktodata", args.get("a_swapblocktodata"));
		config.set(path + ".a_swapblocktodatavalue", args.get("a_swapblocktodatavalue"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_swapblockmatch", config.getString(path + ".a_swapblockmatch"));
		args.put("a_swapblockmatchdata", config.getBoolean(path + ".a_swapblockmatchdata"));
		args.put("a_swapblockmatchdatavalue", Integer.valueOf(config.getInt(path + ".a_swapblockmatchdatavalue")).byteValue());
		args.put("a_swapblockto", config.getString(path + ".a_swapblockto"));
		args.put("a_swapblocktodata", config.getBoolean(path + ".a_swapblocktodata"));
		args.put("a_swapblocktodatavalue", Integer.valueOf(config.getInt(path + ".a_swapblocktodatavalue")).byteValue());
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Swap Block", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		final MinigamePlayer fply = player;
		m.addItem(new MenuItemString("Match Block", Material.COBBLESTONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					fargs.put("a_swapblockmatch", value.toUpperCase());
				else
					fply.sendMessage("Invalid block type!", "error");
			}
			
			@Override
			public String getValue() {
				return (String)fargs.get("a_swapblockmatch");
			}
		}));
		m.addItem(new MenuItemBoolean("Match Block Use Data?", Material.ENDER_PEARL, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fargs.put("a_swapblockmatchdata", value);
			}

			@Override
			public Boolean getValue() {
				return (Boolean)fargs.get("a_swapblockmatchdata");
			}
		}));
		m.addItem(new MenuItemInteger("Match Block Data Value", Material.EYE_OF_ENDER, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				fargs.put("a_swapblockmatchdatavalue", value.byteValue());
			}

			@Override
			public Integer getValue() {
				return ((Byte) fargs.get("a_swapblockmatchdatavalue")).intValue();
			}
		}, 0, 15));
		
		m.addItem(new MenuItemNewLine());
		
		m.addItem(new MenuItemString("To Block", Material.STONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					fargs.put("a_swapblockto", value.toUpperCase());
				else
					fply.sendMessage("Invalid block type!", "error");
			}
			
			@Override
			public String getValue() {
				return (String)fargs.get("a_swapblockto");
			}
		}));
		m.addItem(new MenuItemBoolean("To Block Use Data?", Material.ENDER_PEARL, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fargs.put("a_swapblocktodata", value);
			}

			@Override
			public Boolean getValue() {
				return (Boolean)fargs.get("a_swapblocktodata");
			}
		}));
		m.addItem(new MenuItemInteger("To Block Data Value", Material.EYE_OF_ENDER, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				fargs.put("a_swapblocktodatavalue", value.byteValue());
			}

			@Override
			public Integer getValue() {
				return ((Byte) fargs.get("a_swapblocktodatavalue")).intValue();
			}
		}, 0, 15));
		m.displayMenu(player);
		return true;
	}

}
