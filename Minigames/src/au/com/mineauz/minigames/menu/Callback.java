package au.com.mineauz.minigames.menu;

public interface Callback<T> {
	public void setValue(T value);
	
	public T getValue();
}
