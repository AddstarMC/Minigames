package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.minigame.MgRegion;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegionMapFlag extends Flag<Map<String, MgRegion>> {

    public RegionMapFlag(Map<String, MgRegion> value, String name) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        if (!getFlag().isEmpty()) {
            RegionFlag regionFlag;

            for (MgRegion region : getFlag().values()) {
                regionFlag = new RegionFlag(region, getName() + "." + region.getName());
                regionFlag.saveValue(path, config);
            }
        }
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        Map<String, MgRegion> regions = new HashMap<>();
        Set<String> regionNames = config.getConfigurationSection(path + "." + getName()).getKeys(false);
        RegionFlag regionFlag;

        for (String regionName : regionNames) {
            regionFlag = new RegionFlag(null, getName() + "." + regionName);
            regionFlag.loadValue(path, config);
            regions.put(regionFlag.getFlag().getName(), regionFlag.getFlag());
        }
        setFlag(regions);
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem) {
        return null;
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem, List<String> description) {
        return null;
    }
}
