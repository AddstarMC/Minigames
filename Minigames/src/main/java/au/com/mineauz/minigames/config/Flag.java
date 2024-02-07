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

    public MenuItem getMenuItem(@NotNull LangKey langKey, @Nullable Material displayMaterial) {
        return getMenuItem(MinigameMessageManager.getMgMessage(langKey), displayMaterial);
    }

    public MenuItem getMenuItem(@Nullable Component name, @Nullable Material displayMaterial) {
        return getMenuItem(name, displayMaterial, null);
    }

    public abstract MenuItem getMenuItem(@Nullable Component name, @Nullable Material displayMat,
                                         @Nullable List<@NotNull Component> description);
}
