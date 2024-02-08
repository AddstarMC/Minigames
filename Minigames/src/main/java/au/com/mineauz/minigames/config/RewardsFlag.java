package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.MenuItemDisplayRewards;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RewardsFlag extends Flag<Rewards> {

    public RewardsFlag(Rewards value, String name) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        if (!getFlag().isEmpty()) {
            getFlag().save(config.createSection(path + "." + getName()));
        }
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        getFlag().load(config.getConfigurationSection(path + "." + getName()));
    }

    @Override
    public MenuItemDisplayRewards getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                @Nullable List<@NotNull Component> description) {
        return new MenuItemDisplayRewards(displayMat, name, description, getFlag());
    }
}
