package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class MenuItemSaveMinigame extends MenuItem{
	private Minigame mgm = null;
	
	public MenuItemSaveMinigame(String name, Material displayItem, Minigame minigame) {
		super(name, displayItem);
		mgm = minigame;
	}
	
	public MenuItemSaveMinigame(String name, String description, Material displayItem, Minigame minigame) {
		super(name, description, displayItem);
		mgm = minigame;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		mgm.saveMinigame();
		mgm.clearCachedModules();
		player.sendMessage("Saved the '" + mgm.getName(false) + "' Minigame.", null);
	}

}
