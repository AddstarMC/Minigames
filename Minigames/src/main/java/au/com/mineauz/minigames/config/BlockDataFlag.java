package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.MenuItem;
import javafx.beans.binding.ObjectBinding;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

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
        config.set(path+"."+getName(),getFlag().getAsString());
    }
    
    @Override
    public void loadValue(String path, FileConfiguration config) {
        String obj = config.getString(path+"."+getName());
        BlockData data = Bukkit.createBlockData(obj);
        setFlag(data);
    }
    
    @Override
    public MenuItem getMenuItem(String name, Material displayItem) {
        MenuItem item = new MenuItem()
        return null;
    }
    
    @Override
    public MenuItem getMenuItem(String name, Material displayItem, List<String> description) {
        return null;
    }
}
