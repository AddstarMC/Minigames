package au.com.mineauz.minigames.menu;

/**
 * A callback for setting and getting values
 * @param <T> The type of the value
 */
public interface Callback<T> {
	/**
	 * Changes the value
	 * @param value The new value
	 */
	public void setValue(T value);
	
	/**
	 * @return Returns the current value
	 */
	public T getValue();
}
