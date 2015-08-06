package au.com.mineauz.minigames.menu;

import java.util.Arrays;

import org.bukkit.Material;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

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
		
		subMenu.addItem(new MenuItemString("Display Name", Material.NAME_TAG, new Callback<String>() {
			@Override
			public void setValue(String value) {
				minigame.getSettings(stat).setDisplayName(value);
			}
			
			@Override
			public String getValue() {
				return minigame.getSettings(stat).getDisplayName();
			}
		}));
		if (stat != MinigameStats.Losses) {
			subMenu.addItem(new MenuItemList("Storage Format", Material.ENDER_CHEST, new Callback<String>() {
				@Override
				public void setValue(String value) {
					StatFormat format = StatFormat.valueOf(value);
					minigame.getSettings(stat).setFormat(format);
				}
				
				@Override
				public String getValue() {
					return minigame.getSettings(stat).getFormat().toString();
				}
			}, Lists.transform(Arrays.asList(StatFormat.values()), Functions.toStringFunction())));
		}
		
		subMenu.displayMenu(player);
	}
}
