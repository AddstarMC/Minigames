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
        MenuItemDecimal dec = new MenuItemDecimal(name, displayItem, new Callback<Double>() {

            @Override
            public Double getValue() {
                return getFlag().doubleValue();
            }            @Override
            public void setValue(Double value) {
                setFlag(value.floatValue());
            }


        }, 1d, 1d, 0d, Double.POSITIVE_INFINITY);
        return dec;
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem,
                                List<String> description) {
        MenuItemDecimal dec = new MenuItemDecimal(name, description, displayItem, new Callback<Double>() {

            @Override
            public Double getValue() {
                return getFlag().doubleValue();
            }            @Override
            public void setValue(Double value) {
                setFlag(value.floatValue());
            }


        }, 1d, 1d, 0d, Double.POSITIVE_INFINITY);
        return dec;
    }

    public MenuItem getMenuItem(String name, Material displayItem, double lowerinc, double upperinc, Double min, Double max) {
        MenuItemDecimal dec = new MenuItemDecimal(name, displayItem, new Callback<Double>() {

            @Override
            public Double getValue() {
                return getFlag().doubleValue();
            }            @Override
            public void setValue(Double value) {
                setFlag(value.floatValue());
            }


        }, lowerinc, upperinc, min, max);
        return dec;
    }

    public MenuItem getMenuItem(String name, Material displayItem,
                                List<String> description, double lowerinc, double upperinc, Double min, Double max) {
        MenuItemDecimal dec = new MenuItemDecimal(name, description, displayItem, new Callback<Double>() {

            @Override
            public Double getValue() {
                return getFlag().doubleValue();
            }            @Override
            public void setValue(Double value) {
                setFlag(value.floatValue());
            }


        }, lowerinc, upperinc, min, max);
        return dec;
    }

}
