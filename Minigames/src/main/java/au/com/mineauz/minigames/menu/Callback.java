package au.com.mineauz.minigames.menu;

public interface Callback<T> {
    T getValue();

    void setValue(T value);
}
