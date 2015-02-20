package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class MenuItemAddFlag extends MenuItem{
	
	private Minigame mgm;

	public MenuItemAddFlag(String name, Material displayItem, Minigame mgm) {
		super(name, displayItem);
		this.mgm = mgm;
	}
	
	@Override
	public void onClick(MinigamePlayer player) {
		beginManualEntry(player, "Enter a flag name into chat for " + getName() + ", the menu will automatically reopen in 20s if nothing is entered.", 20);
	}
	
	@Override
	public void checkValidEntry(MinigamePlayer player, String entry){
		mgm.addFlag(entry);
		getContainer().addItem(new MenuItemFlag(Material.SIGN, entry, mgm.getFlags()));
	}
}
