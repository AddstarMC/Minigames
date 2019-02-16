package au.com.mineauz.minigames.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.menu.MenuItem;

public class EnumFlag<T extends Enum<T>> extends Flag<T> {

    private Class<T> enumClass;

    @SuppressWarnings("unchecked")
    public EnumFlag(T value, String name) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
        enumClass = (Class<T>) value.getClass();
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        config.set(path + "." + getName(), getFlag().name());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        setFlag(T.valueOf(enumClass, config.getString(path + "." + getName())));
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem) {
        return null;
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem,
                                List<String> description) {
        return null;
    }

}
