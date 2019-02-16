package au.com.mineauz.minigames.minigame;

import au.com.mineauz.minigames.config.MinigameSave;
import au.com.mineauz.minigames.Minigames;
import com.google.common.collect.Maps;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

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

            block.removeMetadata("MGScoreboardSign", Minigames.getPlugin());
            block.removeMetadata("Minigame", Minigames.getPlugin());
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

    public void saveDisplays(MinigameSave save, String name) {
        FileConfiguration root = save.getConfig();
        ConfigurationSection section = root.createSection(name + ".scoreboards");

        int index = 0;
        for (ScoreboardDisplay display : displays.values()) {
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
