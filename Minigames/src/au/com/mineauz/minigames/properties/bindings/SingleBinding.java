package au.com.mineauz.minigames.properties.bindings;

import au.com.mineauz.minigames.properties.ChangeListener;
import au.com.mineauz.minigames.properties.ObservableValue;

public class SingleBinding<T> implements Binding, ChangeListener<T> {
	private final ObservableValue<? extends T> source;
	private final ObservableValue<T> target;
	
	public SingleBinding(ObservableValue<? extends T> source, ObservableValue<T> target) {
		this.source = source;
		this.target = target;
	}
	
	@Override
	public void link() {
		target.setValue(source.getValue());
		target.addListener(this);
	}

	@Override
	public void unlink() {
		target.removeListener(this);
	}

	@Override
	public void onValueChange(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		if (observable == source) {
			target.setValue(newValue);
		}
	}

}
