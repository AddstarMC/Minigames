package au.com.mineauz.minigames.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemString;

public class StringFlag extends Flag<String> {

    public StringFlag(String value, String name) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        config.set(path + "." + getName(), getFlag());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        if (config.contains(path + "." + getName())) {
            setFlag(config.getString(path + "." + getName()));
        } else {
            setFlag(getDefaultFlag());
        }
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem) {
        return new MenuItemString(name, displayItem, new Callback<String>() {

            @Override
            public String getValue() {
                return getFlag();
            }            @Override
            public void setValue(String value) {
                setFlag(value);
            }


        });
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem, List<String> description) {
        return new MenuItemString(name, description, displayItem, new Callback<String>() {

            @Override
            public String getValue() {
                return getFlag();
            }            @Override
            public void setValue(String value) {
                setFlag(value);
            }


        });
    }

}
