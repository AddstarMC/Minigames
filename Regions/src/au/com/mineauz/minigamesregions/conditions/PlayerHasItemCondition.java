package au.com.mineauz.minigamesregions.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class PlayerHasItemCondition implements ConditionInterface {

	@Override
	public String getName() {
		return "PLAYER_HAS_ITEM";
	}

	@Override
	public String getCategory() {
		return "Player Conditions";
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
	public boolean checkRegionCondition(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
		return check(player, args);
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		return check(player, args);
	}
	
	private boolean check(MinigamePlayer player, Map<String, Object> args){
		boolean inv = (Boolean)args.get("c_playerhasiteminvert");
		if(player.getPlayer().getInventory().contains(Material.getMaterial((String)args.get("c_playerhasitem")))){
			if((Boolean)args.get("c_playerhasitemusedata")){
				short dam = (Short) args.get("c_playerhasitemdata");
				for(ItemStack i : player.getPlayer().getInventory().getContents()){
					if(i != null && i.getDurability() == dam){
						if(!inv) return true;
						else return false;
					}
				}
				if(!inv) return false;
				else return true;
			}
			if(!inv) return true;
			else return false;
		}
		if(!inv) return false;
		else return true;
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_playerhasitem", "STONE");
		args.put("c_playerhasitemusedata", false);
		args.put("c_playerhasitemdata", 0);
		args.put("c_playerhasiteminvert", false);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".c_playerhasitem", args.get("c_playerhasitem"));
		config.set(path + ".c_playerhasitemusedata", args.get("c_playerhasitemusedata"));
		config.set(path + ".c_playerhasitemdata", args.get("c_playerhasitemdata"));
		config.set(path + ".c_playerhasiteminvert", args.get("c_playerhasiteminvert"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_playerhasitem", config.getString(path + ".c_playerhasitem"));
		args.put("c_playerhasitemusedata", config.getBoolean(path + ".c_playerhasitemusedata"));
		args.put("c_playerhasitemdata", ((Integer)config.getInt(path + ".c_playerhasitemdata")).shortValue());
		args.put("c_playerhasiteminvert", config.getBoolean(path + ".c_playerhasiteminvert"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev,
			Map<String, Object> args) {
		Menu m = new Menu(3, "Player Has Item", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		final MinigamePlayer fply = player;
		m.addItem(new MenuItemString("Item", Material.STONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.getMaterial(value.toUpperCase()) != null)
					fargs.put("c_playerhasitem", value.toUpperCase());
				else
					fply.sendMessage("Invalid Item!", "error");
			}
			
			@Override
			public String getValue() {
				return (String)fargs.get("c_playerhasitem");
			}
		}));
		m.addItem(new MenuItemBoolean("Match Item Data", Material.ENDER_PEARL, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fargs.put("c_playerhasitemusedata", value);
			}

			@Override
			public Boolean getValue() {
				return (Boolean)fargs.get("c_playerhasitemusedata");
			}
		}));
		m.addItem(new MenuItemInteger("Data Value", Material.EYE_OF_ENDER, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				fargs.put("c_playerhasitemdata", value.shortValue());
			}

			@Override
			public Integer getValue() {
				return ((Short)fargs.get("c_playerhasitemdata")).intValue();
			}
		}, 0, null));
		m.addItem(new MenuItemBoolean("Invert Result", Material.ENDER_PEARL, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fargs.put("c_playerhasiteminvert", value);
			}

			@Override
			public Boolean getValue() {
				return (Boolean)fargs.get("c_playerhasiteminvert");
			}
		}));
		m.displayMenu(player);
		return true;
	}

}
