package au.com.mineauz.minigames.minigame;

import java.util.List;
import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import au.com.mineauz.minigames.MinigameSave;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.sql.SQLStatLoaderTask;
import au.com.mineauz.minigames.stats.StoredStat;

public class ScoreboardData {
	private final Minigame minigame;
	private final Map<Block, ScoreboardDisplay> displays = Maps.newHashMap();
	
	public ScoreboardData(Minigame minigame) {
		this.minigame = minigame;
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
			loadAndApply(display);
		}
	}
	
	public void reload(Block block) {
		ScoreboardDisplay display = getDisplay(block);
		if (display != null) {
			loadAndApply(display);
		}
	}
	
	private void loadAndApply(final ScoreboardDisplay display) {
		SQLStatLoaderTask task = new SQLStatLoaderTask(
				minigame,
				display.getStat(),
				display.getField(),
				display.getOrder(),
				0,
				display.getWidth() * display.getHeight(),
				Minigames.plugin
				);
		
		ListenableFuture<List<StoredStat>> future = Minigames.plugin.getExecutorService().submit(task);
		Futures.addCallback(future, display.getUpdateCallback(), Minigames.plugin.getBukkitThreadExecutor());
	}
	
	/**
	 * Makes each scoreboard update its signs with their current data. This does not update the scoreboard data.
	 */
	public void refreshDisplays() {
		for (ScoreboardDisplay display : displays.values()) {
			display.updateSigns();
		}
	}
	
	public void saveDisplays(MinigameSave save, String name){
		FileConfiguration root = save.getConfig();
		ConfigurationSection section = root.createSection(name + ".scoreboards");
		
		int index = 0;
		for(ScoreboardDisplay display : displays.values()) {
			ConfigurationSection displaySection = section.createSection(String.valueOf(index++));
			display.save(displaySection);
		}
	}
	
	public void loadDisplays(MinigameSave save, Minigame mgm) {
		FileConfiguration con = save.getConfig();
		ConfigurationSection root = con.getConfigurationSection(mgm.getName(false) + ".scoreboards");
		
		if (root == null) {
			return;
		}
		
		for (String key : root.getKeys(false)) {
			ConfigurationSection displayConf = root.getConfigurationSection(key);
			
			ScoreboardDisplay display = ScoreboardDisplay.load(mgm, displayConf);
			if (display != null) {
				addDisplay(display);
			}
		}
	}
}
