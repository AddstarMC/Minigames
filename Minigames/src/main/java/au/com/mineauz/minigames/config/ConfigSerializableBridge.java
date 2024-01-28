package au.com.mineauz.minigames.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigSerializableBridge<T> {
    private final @NotNull T object;

    public ConfigSerializableBridge(@NotNull T serializable) throws IllegalArgumentException {
        if (!(serializable instanceof ConfigurationSerializable) && !(serializable instanceof Serializable)) {
            throw new IllegalArgumentException("Object was nighter a Serializable nor a ConfigurationSerializable");
        }

        this.object = serializable;
    }

    public Object serialize() {
        if (object instanceof ConfigurationSerializable configurationSerializable) {
            Map<String, Object> map = new LinkedHashMap<>();
            // add key for deserialization at top
            map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(configurationSerializable.getClass()));
            map.putAll(((ConfigurationSerializable) object).serialize());
            return map;
        } else {
            return object;
        }
    }

    public static @Nullable ConfigSerializableBridge<?> deserialize(Object object) {
        if (object instanceof ConfigurationSerializable serializable) { // bukkit already did the work for us
            return new ConfigSerializableBridge<>(serializable);
        } else if (object instanceof ConfigurationSection configSection) { // configs are weird.
            Map<String, Object> stringMap = configSection.getValues(false);
            try {
                ConfigurationSerializable configurationSerializable = ConfigurationSerialization.deserializeObject(stringMap);
                if (configurationSerializable != null) {
                    return new ConfigSerializableBridge<>(configurationSerializable);
                }
            } catch (IllegalArgumentException ignored) {
            }

        } else if (object instanceof Map<?, ?> objMap) {
            Map<String, Object> stringMap = new HashMap<>();

            for (Map.Entry<?, ?> entry : objMap.entrySet()) {
                stringMap.put(entry.getKey().toString(), entry.getValue());
            }

            try {
                ConfigurationSerializable configurationSerializable = ConfigurationSerialization.deserializeObject(stringMap);
                if (configurationSerializable != null) {
                    return new ConfigSerializableBridge<>(configurationSerializable);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (object instanceof Serializable serializable) {
            return new ConfigSerializableBridge<>(serializable);
        } else {
            return null;
            //throw new IllegalArgumentException("Object was nighter a Serializable nor a ConfigurationSerializable");
        }
    }

    public @NotNull T getObject() {
        return object;
    }
}
