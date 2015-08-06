package au.com.mineauz.minigames.menu;

import org.bukkit.Material;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;

public class MenuItemStatisticsSettings extends MenuItem {
	private final Minigame minigame;
	
	public MenuItemStatisticsSettings(Minigame minigame, String name, Material displayItem) {
		super(name, displayItem);
		this.minigame = minigame;
	}

	@Override
	public void onClick(MinigamePlayer player) {
		Menu subMenu = new Menu(6, "Statistics Settings");
		
		for (MinigameStat stat : MinigameStats.getAllStats().values()) {
			subMenu.addItem(new MenuItemModifyStatSetting(minigame, stat, Material.BOOK_AND_QUILL));
		}
		
		subMenu.displayMenu(player);
	}
}
