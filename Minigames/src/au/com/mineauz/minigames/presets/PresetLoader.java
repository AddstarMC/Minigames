package au.com.mineauz.minigames.presets;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigameSave;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.properties.ConfigProperty;

public class PresetLoader {
	
	public static String loadPreset(String preset, Minigame minigame){
		preset = preset.toLowerCase();
		File file = new File(Minigames.plugin.getDataFolder() + "/presets/" + preset + ".yml");
		if(file.exists()){
			MinigameSave save = new MinigameSave("presets/" + preset);
			FileConfiguration config = save.getConfig();
			
			//TODO: This wont load options in modules, perhaps it should
			for(String opt : config.getConfigurationSection(preset).getKeys(false)){
				ConfigProperty<?> property = minigame.getProperties().getProperty(opt);
				if (property != null) {
					property.load(config.getConfigurationSection(preset));
				}
			}
			
			return ChatColor.GRAY + "Loaded the " + 
					MinigameUtils.capitalize(preset) + " preset to " + minigame.getName(false);
		}
		else{
			return ChatColor.RED + "Failed to load preset: " + 
					ChatColor.GRAY + preset + ".yml was not found in the presets folder!";
		}
	}
	
	public static String getPresetInfo(String preset){
		preset = preset.toLowerCase();
		File file = new File(Minigames.plugin.getDataFolder() + "/presets/" + preset + ".yml");
		if(file.exists()){
			MinigameSave save = new MinigameSave("presets/" + preset);
			FileConfiguration config = save.getConfig();
			
			if(config.contains(preset + ".info"))
				return config.getString(preset + ".info");
			else
				return "No information given on this preset.";
		}
		return ChatColor.RED + "Failed to load preset: " + 
			ChatColor.GRAY + preset + ".yml was not found in the presets folder!";
	}

}
