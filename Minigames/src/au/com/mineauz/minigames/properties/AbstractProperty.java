package au.com.mineauz.minigames.properties;

import au.com.mineauz.minigames.properties.bindings.BiDirectionalBinding;
import au.com.mineauz.minigames.properties.bindings.Binding;
import au.com.mineauz.minigames.properties.bindings.SingleBinding;

import com.google.common.base.Preconditions;

public class AbstractProperty<T> extends AbstractObservableValue<T> implements Property<T> {
	private Binding binding;
	
	public AbstractProperty() {
		super();
	}
	
	public AbstractProperty(T value) {
		super(value);
	}

	@Override
	public final void bind(ObservableValue<? extends T> observable) {
		Preconditions.checkNotNull(observable);
		unbind();
		
		binding = new SingleBinding<T>(observable, this);
		binding.link();
	}

	@Override
	public final void unbind() {
		if (binding != null) {
			binding.unlink();
			binding = null;
		}
	}

	@Override
	public final boolean isBound() {
		return (binding != null);
	}

	@Override
	public final void link(Property<T> property) {
		Preconditions.checkNotNull(property);
		unbind();
		
		binding = new BiDirectionalBinding<T>(this, property);
		binding.link();
	}
}
