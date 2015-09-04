package au.com.mineauz.minigames.properties;

import com.google.common.base.Converter;

public final class Properties {
	private Properties() {}
	
	/**
	 * Creates a new basic property with the specified value
	 * @param value The value for it to hold
	 * @return The created property
	 */
	public static <T> Property<T> create(T value) {
		return new SimpleProperty<T>(value);
	}
	
	/**
	 * Creates a new basic property with an empty (null) value.
	 * @return The created property
	 */
	public static <T> Property<T> create() {
		return new SimpleProperty<T>();
	}
	
	/**
	 * Creates a property that transforms a property to another type
	 * @param source The source property
	 * @param converter A converter that can transform between the two types
	 * @return The Property in the desired type
	 */
	public static <T,S> Property<T> transform(Property<S> source, Converter<S, T> converter) {
		return new TransformingProperty<T, S>(source, converter);
	}
	
	/**
	 * Creates an ObservableValue that transforms the input value to another type
	 * @param source The source value
	 * @param converter The converter than can transform between the two types
	 * @return The value in the desired type
	 */
	public static <T,S> ObservableValue<T> transform(ObservableValue<S> source, Converter<S, T> converter) {
		return new TransformingValue<T, S>(source, converter);
	}
	
	public static ObservableValue<Double> toDouble(ObservableValue<Float> floatValue) {
		return transform(floatValue, FloatToDoubleConverter);
	}
	
	private static class CastConverterDouble extends Converter<Float, Double> {
		@Override
		protected Double doForward(Float a) {
			return a.doubleValue();
		}

		@Override
		protected Float doBackward(Double b) {
			return b.floatValue();
		}
	}
	
	public static final Converter<Float, Double> FloatToDoubleConverter = new CastConverterDouble();
}
