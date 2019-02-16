package au.com.mineauz.minigames.script;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import com.google.common.collect.Iterables;

class ScriptCollectionImpl<E extends ScriptReference> extends ScriptCollection {
    private final Collection<E> collection;

    public ScriptCollectionImpl(Collection<E> collection) {
        this.collection = collection;
    }

    @Override
    public ScriptReference getValue(String key) throws IllegalArgumentException, NoSuchElementException {
        int index = Integer.parseInt(key);

        if (index < 0 || index >= collection.size()) {
            throw new NoSuchElementException();
        }

        return Iterables.get(collection, index);
    }

    @Override
    public Collection<String> getKeys() {
        return Collections.emptyList();
    }
}
