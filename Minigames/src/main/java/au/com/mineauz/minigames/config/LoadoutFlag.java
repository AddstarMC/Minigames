package au.com.mineauz.minigames.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.menu.MenuItem;

public class LoadoutFlag extends Flag<PlayerLoadout> {

    public LoadoutFlag(PlayerLoadout value, String name) {
        setFlag(value);
        setDefaultFlag(null);
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        getFlag().save(config.createSection(path + "." + getName()));
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        getFlag().load(config.getConfigurationSection(path + "." + getName()));
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem) {
        return null; //TODO: Menu item easy access for loadouts.
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem,
                                List<String> description) {
        return null;
    }

}
