package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemDecimal;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class FloatFlag extends Flag<Float> {

    public FloatFlag(Float value, String name) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        config.set(path + "." + getName(), getFlag().doubleValue());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        setFlag(((Double) config.getDouble(path + "." + getName())).floatValue());
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem) {
        return new MenuItemDecimal(name, displayItem, new Callback<>() {

            @Override
            public Double getValue() {
                return getFlag().doubleValue();
            }

            @Override
            public void setValue(Double value) {
                setFlag(value.floatValue());
            }


        }, 1d, 1d, 0d, Double.POSITIVE_INFINITY);
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem,
                                List<String> description) {
        return new MenuItemDecimal(name, description, displayItem, new Callback<>() {

            @Override
            public Double getValue() {
                return getFlag().doubleValue();
            }

            @Override
            public void setValue(Double value) {
                setFlag(value.floatValue());
            }


        }, 1d, 1d, 0d, Double.POSITIVE_INFINITY);
    }

    public MenuItem getMenuItem(String name, Material displayItem, double lowerinc, double upperinc, Double min, Double max) {
        return new MenuItemDecimal(name, displayItem, new Callback<>() {

            @Override
            public Double getValue() {
                return getFlag().doubleValue();
            }

            @Override
            public void setValue(Double value) {
                setFlag(value.floatValue());
            }


        }, lowerinc, upperinc, min, max);
    }

    public MenuItem getMenuItem(String name, Material displayItem,
                                List<String> description, double lowerinc, double upperinc, Double min, Double max) {
        return new MenuItemDecimal(name, description, displayItem, new Callback<>() {

            @Override
            public Double getValue() {
                return getFlag().doubleValue();
            }

            @Override
            public void setValue(Double value) {
                setFlag(value.floatValue());
            }


        }, lowerinc, upperinc, min, max);
    }

}
