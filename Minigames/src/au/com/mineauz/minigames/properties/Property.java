package au.com.mineauz.minigames.properties;

public interface Property<T> extends ObservableValue<T> {
	/**
	 * Creates a uni-directional binding to {@code observable}. Any change made to the
	 * target will update the value of this property.
	 * Only 1 observable can be bound to at one time, this includes {@link #link(Property)}.
	 * Attempting to bind another one will unbind the existing one
	 * 
	 * @param observable The observable to bind to
	 */
	public void bind(ObservableValue<? extends T> observable);
	
	/**
	 * Unbinds this property if bound to another property or observable
	 */
	public void unbind();
	
	/**
	 * Checks if this property is bound to something
	 * @return True if either {@link #bind(ObservableValue)} or {@link #link(Property)} is active
	 */
	public boolean isBound();
	
	/**
	 * Creates a bi-directional binding of this property and the specified one.
	 * This means any changes made to either property will be reflected in the other one
	 * @param property The propery to link
	 */
	public void link(Property<T> property);
}
