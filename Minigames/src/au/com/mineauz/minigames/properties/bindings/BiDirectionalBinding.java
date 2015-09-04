package au.com.mineauz.minigames.properties.bindings;

import au.com.mineauz.minigames.properties.ChangeListener;
import au.com.mineauz.minigames.properties.ObservableValue;
import au.com.mineauz.minigames.properties.Property;

public class BiDirectionalBinding<T> implements Binding, ChangeListener<T> {
	private final Property<T> prop1;
	private final Property<T> prop2;
	
	private boolean isUpdating;
	
	public BiDirectionalBinding(Property<T> prop1, Property<T> prop2) {
		this.prop1 = prop1;
		this.prop2 = prop2;
		
		isUpdating = false;
	}
	
	/**
	 * Registers the listeners bridging these two properties.
	 * Property 1's value will be updated to property 2's value at this time
	 */
	@Override
	public void link() {
		prop1.setValue(prop2.getValue());
		
		prop1.addListener(this);
		prop2.addListener(this);
	}
	
	/**
	 * Removes the listeners unlinking the two properties
	 */
	@Override
	public void unlink() {
		prop1.removeListener(this);
		prop2.removeListener(this);
	}
	
	@Override
	public void onValueChange(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		if (isUpdating) {
			return;
		}
		
		isUpdating = true;
		if (observable == prop1) {
			prop2.setValue(newValue);
		} else if (observable == prop2) {
			prop1.setValue(newValue);
		}
		
		isUpdating = false;
	}
}
