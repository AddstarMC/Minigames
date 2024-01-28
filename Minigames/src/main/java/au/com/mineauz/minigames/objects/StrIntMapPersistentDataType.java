package au.com.mineauz.minigames.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public class StrIntMapPersistentDataType implements PersistentDataType<String, IndexedMap<String, Integer>> {
    private final Type MAP_TYPE = new TypeToken<IndexedMap<String, Integer>>() {
    }.getType();
    private final Gson GSON = new Gson();

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public @NotNull Class<IndexedMap<String, Integer>> getComplexType() {
        return (Class<IndexedMap<String, Integer>>) ((Class) LinkedHashMap.class);
    }

    @Override
    public @NotNull String toPrimitive(@NotNull IndexedMap<String, Integer> complex, @NotNull PersistentDataAdapterContext context) {
        return GSON.toJson(complex, MAP_TYPE);
    }

    @Override
    public @NotNull IndexedMap<String, Integer> fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return GSON.fromJson(primitive, MAP_TYPE);
    }
}
