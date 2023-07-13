package au.com.mineauz.minigames.script;

public record ScriptValue<T>(T value) implements ScriptReference {

    public static <T> ScriptValue<T> of(T value) {
        return new ScriptValue<>(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
