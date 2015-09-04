package au.com.mineauz.minigames.properties;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class AbstractObservableValue<T> implements ObservableValue<T> {
	private final Set<ChangeListener<? super T>> listeners;
	
	private T value;
	
	public AbstractObservableValue(T value) {
		this.value = value;
		
		// Hack to get a weak set
		Map<ChangeListener<? super T>, Void> map = new WeakHashMap<ChangeListener<? super T>, Void>();
		listeners = Collections.synchronizedSet(map.keySet());
	}
	
	public AbstractObservableValue() {
		this(null);
	}
	
	@Override
	public T getValue() {
		return value;
	}
	
	@Override
	public void setValue(T value) {
		T oldValue = this.value;
		this.value = value;
		
		notifyListeners(oldValue);
	}
	
	
	@Override
	public final void addListener(ChangeListener<? super T> listener) {
		listeners.add(listener);
	}
	
	@Override
	public final void removeListener(ChangeListener<? super T> listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners(T oldValue) {
		synchronized(listeners) {
			for (ChangeListener<? super T> listener : listeners) {
				listener.onValueChange(this, oldValue, value);
			}
		}
	}
}
