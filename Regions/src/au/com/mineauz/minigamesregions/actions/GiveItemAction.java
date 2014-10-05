package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class GiveItemAction extends ActionInterface{
	
	private StringFlag type = new StringFlag("STONE", "type");
	private IntegerFlag count = new IntegerFlag(1, "count");
	private IntegerFlag damage = new IntegerFlag(0, "damage");

	@Override
	public String getName() {
		return "GIVE_ITEM";
	}

	@Override
	public String getCategory() {
		return "Player Actions";
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
		execute(player);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		execute(player);
	}
	
	private void execute(MinigamePlayer player){
		Map<Integer, ItemStack> unadded = player.getPlayer().getInventory().addItem(
				new ItemStack(Material.getMaterial(type.getFlag()), count.getFlag(), damage.getFlag().shortValue()));
		
		if(!unadded.isEmpty()){
			for(ItemStack i : unadded.values()){
				player.getLocation().getWorld().dropItem(player.getLocation(), i);
			}
		}
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		type.saveValue(path, config);
		count.saveValue(path, config);
		damage.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		type.loadValue(path, config);
		count.loadValue(path, config);
		damage.loadValue(path, config);
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
		
		m.displayMenu(player);
		return true;
	}

}
