package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.PlayerLoadout;

public class MenuItemDisplayPotions extends MenuItem{
	
	private PlayerLoadout loadout;
	
	public MenuItemDisplayPotions(String name, Material displayItem, PlayerLoadout loadout) {
		super(name, displayItem);
		this.loadout = loadout;
	}

	public MenuItemDisplayPotions(String name, String description, Material displayItem, PlayerLoadout loadout) {
		super(name, description, displayItem);
		this.loadout = loadout;
	}
	
	
	@Override
	public void onClick(MinigamePlayer player){
		Menu potionMenu = new Menu(5, getContainer().getName());
		
		potionMenu.setAllowModify(true);
		potionMenu.setControlItem(new MenuItemPotionAdd("Add Potion", Material.ITEM_FRAME, loadout), 4);
		
		for(PotionEffect eff : loadout.getAllPotionEffects()){
			potionMenu.addItem(new MenuItemPotion(eff.getType().getName().toLowerCase().replace("_", " "), "Shift + Right Click to Delete", Material.POTION, eff, loadout));
		}
		
		potionMenu.displayMenu(player);
	}
}
