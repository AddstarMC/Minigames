package au.com.mineauz.minigames.objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An ordered map, where every key value pair is linked to an index
 * Keep in mind that it does not override any of the complex methods, they have to get coded if needed!
 */
public class IndexedMap<K, V> extends HashMap<K, V> implements Map<K, V> {
    private final @NotNull LinkedList<@Nullable K> keyList = new LinkedList<>();

    @Override
    public V put(K key, V value) {
        if (!keyList.contains(key)) {
            keyList.add(key);
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        keyList.remove(key);

        return super.remove(key);
    }

    @Override
    public void clear() {
        keyList.clear();
        super.clear();
    }

    public @NotNull List<@Nullable K> getKeys() {
        return keyList;
    }

    /**
     * Returns the index of the occurrence of the specified key in this map,
     * or -1 if this list does not contain the element.
     *
     * @param key – element to search for
     * @return the index of the occurrence of the specified key in this list, or -1 if this map does not contain the key
     */
    public int getKeyIndex(K key) {
        return keyList.indexOf(key);
    }

    public @Nullable K getKeyAt(int index) {
        if (keyList.size() > index && index >= 0) {
            return keyList.get(index);
        }
        return null;
    }

    public @Nullable V getValueAt(int index) {
        K key = getKeyAt(index);
        if (key != null) {
            return get(key);
        }
        return null;
    }

    public @Nullable V removeIndex(int index) {
        K key = getKeyAt(index);

        if (key != null) {
            return remove(key);
        } else {
            return null;
        }
    }
}
