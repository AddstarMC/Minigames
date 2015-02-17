package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class MenuItemSaveMinigame extends MenuItem{
	private Minigame mgm = null;
	
	public MenuItemSaveMinigame(String name, Material displayItem, Minigame minigame) {
		super(name, displayItem);
		mgm = minigame;
	}
	
	public MenuItemSaveMinigame(String name, List<String> description, Material displayItem, Minigame minigame) {
		super(name, description, displayItem);
		mgm = minigame;
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player){
		mgm.saveMinigame();
		player.sendMessage("Saved the '" + mgm.getName(false) + "' Minigame.", null);
		return getItem();
	}

}
