package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.PlayerLoadout;

public class MenuItemDisplayPotions extends MenuItem{
	
	private PlayerLoadout loadout;
	
	public MenuItemDisplayPotions(String name, Material displayItem, PlayerLoadout loadout) {
		super(name, displayItem);
		this.loadout = loadout;
	}

	public MenuItemDisplayPotions(String name, List<String> description, Material displayItem, PlayerLoadout loadout) {
		super(name, description, displayItem);
		this.loadout = loadout;
	}
	
	
	@Override
	public ItemStack onClick(MinigamePlayer player){
		Menu potionMenu = new Menu(5, getContainer().getName());
		
		potionMenu.setAllowModify(true);
		potionMenu.setControlItem(new MenuItemPotionAdd("Add Potion", Material.ITEM_FRAME, loadout), 4);
		
		List<String> des = new ArrayList<String>();
		des.add("Shift + Right Click to Delete");
		
		for(PotionEffect eff : loadout.getAllPotionEffects()){
			potionMenu.addItem(new MenuItemPotion(eff.getType().getName().toLowerCase().replace("_", " "), des, Material.POTION, eff, loadout));
		}
		
		potionMenu.displayMenu(player);
		
		return null;
	}
}
