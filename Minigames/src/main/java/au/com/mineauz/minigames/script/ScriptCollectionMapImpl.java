package au.com.mineauz.minigames.script;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

class ScriptCollectionMapImpl<E extends ScriptReference> extends ScriptCollection {
    private final Map<String, E> map;

    public ScriptCollectionMapImpl(Map<String, E> map) {
        this.map = map;
    }

    @Override
    public ScriptReference getValue(String key) throws IllegalArgumentException, NoSuchElementException {
        ScriptReference ref = map.get(key);
        if (ref == null) {
            throw new NoSuchElementException();
        }

        return ref;
    }

    @Override
    public Collection<String> getKeys() {
        return map.keySet();
    }
}
