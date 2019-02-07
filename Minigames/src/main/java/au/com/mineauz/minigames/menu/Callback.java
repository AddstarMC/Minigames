package au.com.mineauz.minigames.menu;

public interface Callback<T> {
    void setValue(T value);
    
    T getValue();
}
