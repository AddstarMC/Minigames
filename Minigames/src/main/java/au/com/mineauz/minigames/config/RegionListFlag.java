package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.minigame.MgRegion;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RegionListFlag extends Flag<List<MgRegion>> {

    public RegionListFlag(List<MgRegion> value, String name) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        if (!getFlag().isEmpty()) {
            RegionFlag regionFlag;

            for (int i = 0; i < getFlag().size(); i++) {
                regionFlag = new RegionFlag(getFlag().get(i), getName() + "." + i);
                regionFlag.saveValue(path, config);
            }
        }
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        List<MgRegion> regions = new ArrayList<>();
        Set<String> ids = config.getConfigurationSection(path + "." + getName()).getKeys(false);
        RegionFlag regionFlag;

        for (int i = 0; i < ids.size(); i++) {
            regionFlag = new RegionFlag(null, getName() + "." + i);
            regionFlag.loadValue(path, config);
            regions.add(regionFlag.getFlag());
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
