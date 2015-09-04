package au.com.mineauz.minigames.properties;

/**
 * Provides an interface for listening to {@link ObservableValue}'s 
 * 
 * @param <T> The value's type
 */
public interface ChangeListener<T> {
	/**
	 * Called upon the value of {@code observable} changing
	 * @param observable The {@link ObservableValue} whose value has changed
	 * @param oldValue The value before the change
	 * @param newValue The value after the change
	 */
	public void onValueChange(ObservableValue<? extends T> observable, T oldValue, T newValue);
}
