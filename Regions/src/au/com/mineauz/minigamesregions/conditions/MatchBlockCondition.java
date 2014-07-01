package au.com.mineauz.minigamesregions.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class MatchBlockCondition implements ConditionInterface {

	@Override
	public String getName() {
		return "MATCH_BLOCK";
	}
	
	@Override
	public String getCategory(){
		return "World Conditions";
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
		return check(args, event);
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		return check(args, event);
	}
	
	private boolean check(Map<String, Object> args, Event event){
		if(event instanceof BlockEvent){
			BlockEvent bev = (BlockEvent) event;
			if(bev.getBlock().getType() == Material.getMaterial((String)args.get("c_matchblocktype")) &&
					(!(Boolean) args.get("c_matchblockusedur") || 
							bev.getBlock().getState().getData().toItemStack().getDurability() == (Short) args.get("c_matchblockdur"))){
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_matchblocktype", "STONE");
		args.put("c_matchblockusedur", false);
		args.put("c_matchblockdur", (short)0);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".c_matchblocktype", args.get("c_matchblocktype"));
		config.set(path + ".c_matchblockusedur", args.get("c_matchblockusedur"));
		config.set(path + ".c_matchblockdur", args.get("c_matchblockdur"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_matchblocktype", config.getString(path + ".c_matchblocktype"));
		args.put("c_matchblockusedur", config.getBoolean(path + ".c_matchblockusedur"));
		args.put("c_matchblockdur", (short)config.getInt(path + ".c_matchblockdur"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev,
			Map<String, Object> args) {
		Menu m = new Menu(3, "Match Block", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		final MenuItemCustom c = new MenuItemCustom("Auto Set Block", 
				MinigameUtils.stringToList("Click here with a;block you wish to;match to."), Material.ITEM_FRAME);
		m.addItem(c, m.getSize() - 1);
		
		final Map<String, Object> fargs = args;
		
		final MenuItemString type = new MenuItemString("Block Type", Material.STONE, new Callback<String>() {

			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					fargs.put("c_matchblocktype", value.toUpperCase());
			}

			@Override
			public String getValue() {
				return (String) fargs.get("c_matchblocktype");
			}
		});
		m.addItem(type);
		final MenuItemBoolean usedur = new MenuItemBoolean("Use Data Values", Material.ENDER_PEARL, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fargs.put("c_matchblockusedur", value);
			}

			@Override
			public Boolean getValue() {
				return (Boolean) fargs.get("c_matchblockusedur");
			}
		});
		m.addItem(usedur);
		final MenuItemInteger dur = new MenuItemInteger("Data Value", Material.PAPER, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				fargs.put("c_matchblockdur", value.shortValue());
			}

			@Override
			public Integer getValue() {
				return ((Short)fargs.get("c_matchblockdur")).intValue();
			}
		}, 0, 16);
		m.addItem(dur);
		
		c.setClickItem(new InteractionInterface() {
			
			@Override
			public Object interact(Object object) {
				ItemStack i = (ItemStack) object;
				fargs.put("c_matchblocktype", i.getType().toString());
				fargs.put("c_matchblockusedur", true);
				fargs.put("c_matchblockdur", i.getDurability());
				dur.updateDescription();
				usedur.updateDescription();
				type.updateDescription();
				return c.getItem();
			}
		});
		m.displayMenu(player);
		return true;
	}

}
