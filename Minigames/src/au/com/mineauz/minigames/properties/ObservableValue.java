package au.com.mineauz.minigames.properties;

/**
 * Represents a value that can be watched for changes
 *
 * @param <T> The value's type
 */
public interface ObservableValue<T> {
	/**
	 * Adds a change listener to be notified upon a value change.
	 * Listeners will be weakly referenced.
	 * @param listener The listener to register
	 */
	public void addListener(ChangeListener<? super T> listener);
	/**
	 * Removes a previously added listener
	 * @param listener The listener to unregister
	 */
	public void removeListener(ChangeListener<? super T> listener);
	
	/**
	 * Gets the current value
	 * @return The value
	 */
	public T getValue();
	
	/**
	 * Sets the current value
	 * @param value The value
	 */
	public void setValue(T value);
}
