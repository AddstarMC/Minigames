package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class IntegerFlag extends Flag<Integer> {

    public IntegerFlag(Integer value, String name) {
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
        setFlag(config.getInt(path + "." + getName()));
    }

    @Override
    public MenuItemInteger getMenuItem(String name, Material displayItem) {
        return new MenuItemInteger(name, displayItem, new Callback<>() {

            @Override
            public Integer getValue() {
                return getFlag();
            }

            @Override
            public void setValue(Integer value) {
                setFlag(value);
            }


        }, 0, null);
    }

    public MenuItemInteger getMenuItem(String name, Material displayItem, Integer min, Integer max) {
        return new MenuItemInteger(name, displayItem, new Callback<>() {

            @Override
            public Integer getValue() {
                return getFlag();
            }

            @Override
            public void setValue(Integer value) {
                setFlag(value);
            }


        }, min, max);
    }

    @Override
    public MenuItemInteger getMenuItem(String name, Material displayItem, List<String> description) {
        return new MenuItemInteger(name, description, displayItem, new Callback<>() {

            @Override
            public Integer getValue() {
                return getFlag();
            }

            @Override
            public void setValue(Integer value) {
                setFlag(value);
            }


        }, 0, null);
    }

    public MenuItemInteger getMenuItem(String name, Material displayItem, List<String> description, Integer min, Integer max) {
        return new MenuItemInteger(name, description, displayItem, new Callback<>() {

            @Override
            public Integer getValue() {
                return getFlag();
            }

            @Override
            public void setValue(Integer value) {
                setFlag(value);
            }


        }, min, max);
    }

}
