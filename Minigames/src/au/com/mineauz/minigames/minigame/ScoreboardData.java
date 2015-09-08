package au.com.mineauz.minigames.minigame;

import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Maps;
import au.com.mineauz.minigames.Minigames;

public class ScoreboardData {
	private final Map<Block, ScoreboardDisplay> displays = Maps.newHashMap();
	
	public ScoreboardData() {
	}
	
	public ScoreboardDisplay getDisplay(Block block) {
		return displays.get(block);
	}
	
	public void addDisplay(ScoreboardDisplay display) {
		displays.put(display.getRoot().getBlock(), display);
	}
	
	public void removeDisplay(Block block) {
		ScoreboardDisplay display = displays.remove(block);
		if (display != null) {
			display.deleteSigns();
			
			block.removeMetadata("MGScoreboardSign", Minigames.plugin);
			block.removeMetadata("Minigame", Minigames.plugin);
		}
	}
	
	/**
	 * Makes async queries to the database loading the data for each scoreboard display
	 */
	public void reload() {
		for (ScoreboardDisplay display : displays.values()) {
			display.reload();
		}
	}
	
	public void reload(Block block) {
		ScoreboardDisplay display = getDisplay(block);
		if (display != null) {
			display.reload();
		}
	}
	
	/**
	 * Makes each scoreboard update its signs with their current data. This does not update the scoreboard data.
	 */
	public void refreshDisplays() {
		for (ScoreboardDisplay display : displays.values()) {
			display.updateSigns();
		}
	}
	
	public void saveDisplays(ConfigurationSection root) {
		ConfigurationSection section = root.createSection("scoreboards");
		
		int index = 0;
		for (ScoreboardDisplay display : displays.values()) {
			ConfigurationSection displaySection = section.createSection(String.valueOf(index++));
			display.save(displaySection);
		}
	}
	
	public void loadDisplays(ConfigurationSection root, Minigame mgm) {
		ConfigurationSection section = root.getConfigurationSection("scoreboards");
		
		if (section == null) {
			return;
		}
		
		for (String key : section.getKeys(false)) {
			ConfigurationSection displayConf = section.getConfigurationSection(key);
			
			ScoreboardDisplay display = ScoreboardDisplay.load(mgm, displayConf);
			if (display != null) {
				addDisplay(display);
			}
		}
	}
}
