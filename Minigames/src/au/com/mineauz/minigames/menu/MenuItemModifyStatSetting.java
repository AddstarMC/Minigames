package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StatFormat;

public class MenuItemModifyStatSetting extends MenuItem {
	private final Minigame minigame;
	private final MinigameStat stat;
	
	public MenuItemModifyStatSetting(Minigame minigame, MinigameStat stat, Material material) {
		super(stat.getDisplayName(), material);
		
		this.minigame = minigame;
		this.stat = stat;
	}
	
	@Override
	public void onClick(MinigamePlayer player) {
		Menu subMenu = new Menu(6, "Edit " + stat.getDisplayName());
		
		subMenu.addItem(new MenuItemString("Display Name", Material.NAME_TAG, minigame.getSettings(stat).displayName()));
		if (stat != MinigameStats.Losses) {
			subMenu.addItem(new MenuItemEnum<StatFormat>("Storage Format", Material.ENDER_CHEST, minigame.getSettings(stat).format(), StatFormat.class));
		}
		
		subMenu.displayMenu(player);
	}
}
