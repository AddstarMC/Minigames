package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class TakeItemAction extends AbstractAction{
	
	private StringFlag type = new StringFlag("STONE", "type");
	private BooleanFlag matchDamage = new BooleanFlag(true, "matchdamage");
	private IntegerFlag damage = new IntegerFlag(0, "damage");
	private IntegerFlag count = new IntegerFlag(1, "count");

	@Override
	public String getName() {
		return "TAKE_ITEM";
	}

	@Override
	public String getCategory() {
		return "Player Actions";
	}
	
	@Override
	public void describe(Map<String, Object> out) {
		if (matchDamage.getFlag()) {
			out.put("Item", type.getFlag() + ":" + damage.getFlag());
		} else {
			out.put("Item", type.getFlag() + ":all");
		}
		
		out.put("Count", count);
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
	public void executeRegionAction(MinigamePlayer player, Region region) {
		debug(player,region);
		execute(player);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		debug(player,node);
		execute(player);
	}
	
	private void execute(MinigamePlayer player){
		ItemStack match = new ItemStack(Material.getMaterial(type.getFlag()), count.getFlag(), damage.getFlag().shortValue());
		ItemStack matched = null;
		boolean remove = false;
		int slot = 0;
		
		for(ItemStack i : player.getPlayer().getInventory().getContents()){
			if(i != null && i.getType() == match.getType()){
				if(!matchDamage.getFlag() || match.getDurability() == i.getDurability()){
					if(match.getAmount() >= i.getAmount()){
						matched = i.clone();
						remove = true;
					}
					else{
						matched = i.clone();
						matched.setAmount(matched.getAmount() - match.getAmount());
					}
					break;
				}
			}
			slot++;
		}
		
		if(remove)
			player.getPlayer().getInventory().removeItem(matched);
		else{
			player.getPlayer().getInventory().getItem(slot).setAmount(matched.getAmount());
		}
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		type.saveValue(path, config);
		matchDamage.saveValue(path, config);
		damage.saveValue(path, config);
		count.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		type.loadValue(path, config);
		matchDamage.loadValue(path, config);
		damage.loadValue(path, config);
		count.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(final MinigamePlayer player, Menu previous) {
Menu m = new Menu(3, "Give Item", player);
		
		m.addItem(new MenuItemBack(previous), m.getSize() - 9);
		m.addItem(new MenuItemString("Type", Material.STONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.getMaterial(value.toUpperCase()) != null){
					type.setFlag(value.toUpperCase());
				}
				else
					player.sendMessage("Invalid item type!", "error");
			}
			
			@Override
			public String getValue() {
				return type.getFlag();
			}
		}));
		m.addItem(count.getMenuItem("Count", Material.STEP, 1, 64));
		m.addItem(damage.getMenuItem("Damage", Material.COBBLESTONE, 0, null));
		m.addItem(matchDamage.getMenuItem("Match Damage", Material.ENDER_PEARL));
		
		m.displayMenu(player);
		return true;
	}

}
