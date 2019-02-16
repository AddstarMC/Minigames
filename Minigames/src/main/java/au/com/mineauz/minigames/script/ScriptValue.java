package au.com.mineauz.minigames.script;

public class ScriptValue<T> implements ScriptReference {
    private final T value;

    public ScriptValue(T value) {
        this.value = value;
    }

    public static <T> ScriptValue<T> of(T value) {
        return new ScriptValue<>(value);
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
