package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.menu.MenuItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    @Deprecated
    @Override
    public @Nullable MenuItem getMenuItem(@Nullable Component name, @Nullable Material displayMat) {
        return null; //TODO: Menu item easy access for loadouts.
    }

    @Deprecated
    @Override
    public @Nullable MenuItem getMenuItem(@Nullable Component name, @Nullable Material displayMat,
                                          @Nullable List<@NotNull Component> description) {
        return null;
    }
}
