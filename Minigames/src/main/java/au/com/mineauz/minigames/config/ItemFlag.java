package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItemItemNbt;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemFlag extends Flag<ItemStack> {

    public ItemFlag(@NotNull ItemStack itemStack, @NotNull String name) {
        setFlag(itemStack);
        setDefaultFlag(itemStack);
        setName(name);
    }

    @Override
    public void saveValue(@NotNull String path, @NotNull FileConfiguration config) {
        config.set(path + "." + getName(), getFlag());
    }

    @Override
    public void loadValue(@NotNull String path, @NotNull FileConfiguration config) {
        if (config.contains(path + "." + getName())) {
            Object object = config.get(path + "." + getName());

            if (object instanceof ItemStack itemStack) { // bukkit already did the work for us
                setFlag(itemStack);
            } else if (object instanceof ConfigurationSection configSection) { // configs are weird.
                Map<String, Object> stringMap = configSection.getValues(false);
                setFlag(ItemStack.deserialize(stringMap));
            } else if (object instanceof Map<?, ?> objMap) {
                Map<String, Object> stringMap = new HashMap<>();

                for (Map.Entry<?, ?> entry : objMap.entrySet()) {
                    stringMap.put(entry.getKey().toString(), entry.getValue());
                }

                setFlag(ItemStack.deserialize(stringMap));
            }
        } else {
            setFlag(getDefaultFlag());
        }
    }


    public @NotNull MenuItemItemNbt getMenuItem(@NotNull String name) {
        return new MenuItemItemNbt(name, getFlagOrDefault(), new Callback<>() {
            @Override
            public ItemStack getValue() {
                return getFlag();
            }

            @Override
            public void setValue(ItemStack value) {
                setFlag(value);
            }
        });
    }

    @Override
    public @NotNull MenuItemItemNbt getMenuItem(@NotNull String name, @Nullable Material displayMaterial) {
        return getMenuItem(name, displayMaterial, null);
    }

    @Override
    public @NotNull MenuItemItemNbt getMenuItem(@NotNull String name, @Nullable Material displayMaterial, @Nullable List<@NotNull String> description) {
        return new MenuItemItemNbt(name, description, displayMaterial, new Callback<>() {
            @Override
            public ItemStack getValue() {
                return getFlag();
            }

            @Override
            public void setValue(ItemStack value) {
                setFlag(value);
            }
        });
    }
}
