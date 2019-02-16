package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.logging.Level;

/**
 * Created for the AddstarMC Project. Created by Narimm on 6/08/2018.
 */
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
        } catch (IllegalArgumentException e) {
            data = parseOldMaterialData(path, config);
        }
        if (data != null)
            setFlag(data);
        else {
            setFlag(Material.STONE.createBlockData());
        }
    }

    /**
     * Remove in 1.14 as no configs should have materialdata stored.
     */
    @Deprecated
    private BlockData parseOldMaterialData(String path, FileConfiguration config) {
        try {
            String obj = config.getString(path + "." + getName());
            Material mat = Material.matchMaterial(obj);
            Integer olddata = config.getInt(path + ".matchdatavalue");
            if (olddata == 0) olddata = config.getInt(path + ".todatavalue");
            if (olddata == 0) olddata = config.getInt(path + ".dur");
            return Bukkit.getUnsafe().fromLegacy(mat, olddata.byteValue());
        } catch (Exception ignored) {
            Minigames.getPlugin().getLogger().log(Level.CONFIG, "Error loading Value for" + path);
        }
        return Material.STONE.createBlockData();
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem) {
        return new MenuItemBlockData(name, displayItem);
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem, List<String> description) {
        return new MenuItemBlockData(name, displayItem);
    }
}
