package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Flag<T> {
    private T value;
    private String name;
    private T defaultVal;

    public T getFlag() {
        return value;
    }

    public void setFlag(T value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public T getDefaultFlag() {
        return defaultVal;
    }

    protected void setDefaultFlag(T value) {
        defaultVal = value;
    }

    public T getFlagOrDefault() {
        if (value == null) {
            return getDefaultFlag();
        } else {
            return getFlag();
        }
    }

    public Callback<T> getCallback() {
        return new Callback<>() {

            @Override
            public T getValue() {
                return getFlag();
            }

            @Override
            public void setValue(T value) {
                setFlag(value);
            }
        };
    }

    public abstract void saveValue(String path, FileConfiguration config);

    public abstract void loadValue(String path, FileConfiguration config);

    public MenuItem getMenuItem(@Nullable Material displayMaterial, @NotNull LangKey langKey) {
        return getMenuItem(displayMaterial, MinigameMessageManager.getMgMessage(langKey));
    }

    public MenuItem getMenuItem(@Nullable Material displayMaterial, @Nullable Component name) {
        return getMenuItem(displayMaterial, name, null);
    }

    public abstract MenuItem getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                         @Nullable List<@NotNull Component> description);
}
