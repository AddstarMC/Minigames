package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBlockData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BlockDataFlag extends Flag<BlockData> {

    public BlockDataFlag(BlockData value, String name) {
        setDefaultFlag(value);
        setFlag(value);
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        config.set(path + "." + getName(), getFlag().getAsString());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        String obj = config.getString(path + "." + getName());
        BlockData data = null;
        try {
            data = Bukkit.createBlockData(obj);
        } catch (NullPointerException | IllegalArgumentException e) {
            Minigames.getCmpnntLogger().warn("couldn't load Blockdata flag. Legacy data loading was removed.", e);
        }
        setFlag(Objects.requireNonNullElseGet(data, Material.STONE::createBlockData));
    }

    /**
     * @param description ignored and replaced with a description of the data
     */
    @Override
    public MenuItem getMenuItem(@Nullable Component name, @Nullable Material displayMat,
                                @Nullable List<@NotNull Component> description) {
        return new MenuItemBlockData(displayMat, name, new Callback<>() {
            @Override
            public BlockData getValue() {
                return getFlag();
            }

            @Override
            public void setValue(BlockData value) {
                setFlag(value);
            }
        });
    }
}
