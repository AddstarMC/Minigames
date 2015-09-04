package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemEnum;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.properties.types.EnumProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.TriggerArea;

public class GiveItemAction extends ActionInterface{
	
	private final EnumProperty<Material> type = new EnumProperty<Material>(Material.STONE, "type");
	private final IntegerProperty count = new IntegerProperty(1, "count");
	private final IntegerProperty damage = new IntegerProperty(0, "damage");
	private final StringProperty name = new StringProperty(null, "name");
	private final StringProperty lore = new StringProperty(null, "lore");
	
	public GiveItemAction() {
		properties.addProperty(type);
		properties.addProperty(count);
		properties.addProperty(damage);
		properties.addProperty(name);
		properties.addProperty(lore);
	}

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
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		ItemStack item = new ItemStack(type.getValue(), count.getValue(), damage.getValue().shortValue());
		ItemMeta meta = item.getItemMeta();
		if(name.getValue() != null){
			meta.setDisplayName(name.getValue());
		}
		if(lore.getValue() != null){
			meta.setLore(MinigameUtils.stringToList(lore.getValue()));
		}
		item.setItemMeta(meta);
		
		Map<Integer, ItemStack> unadded = player.getPlayer().getInventory().addItem(
				item);
		
		if(!unadded.isEmpty()){
			for(ItemStack i : unadded.values()){
				player.getLocation().getWorld().dropItem(player.getLocation(), i);
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
	public boolean displayMenu(final MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Give Item");
		
		MenuItemString n = new MenuItemString("Name", Material.NAME_TAG, name);
		n.setAllowNull(true);
		m.addItem(n);
		MenuItemString l = new MenuItemString("Lore", "Separate with semi-colons;for new lines", Material.PAPER, lore);
		l.setAllowNull(true);
		m.addItem(l);
		
		m.addItem(new MenuItemEnum<Material>("Type", Material.STONE, type, Material.class));
		m.addItem(new MenuItemInteger("Count", Material.STEP, count, 1, 64));
		m.addItem(new MenuItemInteger("Damage", Material.COBBLESTONE, damage, 0, Integer.MAX_VALUE));
		
		m.displayMenu(player);
		return true;
	}

}
