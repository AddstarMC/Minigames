package au.com.mineauz.minigames.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;

public abstract class Flag<T> {
    
    private T value;
    private String name;
    private T defaultVal;
    
    public T getFlag(){
        return value;
    }
    
    public void setFlag(T value){
        this.value = value;
    }
    
    protected void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public T getDefaultFlag(){
        return defaultVal;
    }
    
    public Callback<T> getCallback(){
        return new Callback<T>() {

            @Override
            public void setValue(T value) {
                setFlag(value);
            }

            @Override
            public T getValue() {
                return getFlag();
            }
        };
    }
    
    protected void setDefaultFlag(T value){
        defaultVal = value;
    }
    
    public abstract void saveValue(String path, FileConfiguration config);
    public abstract void loadValue(String path, FileConfiguration config);
    public abstract MenuItem getMenuItem(String name, Material displayItem);
    public abstract MenuItem getMenuItem(String name, Material displayItem, List<String> description);
}
