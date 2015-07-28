package au.com.mineauz.minigames.stats;

/**
 * These are the components of {@link StatFormat} indicating what
 * fields are available
 */
public enum StatValueField {
	Last,
	Min,
	Max,
	Total;
	
	/**
	 * Can be used to apply this fields function
	 * @param currentValue The current value of this stat before update
	 * @param newValue The value being applied to this stat
	 * @return The resulting value that should be used for this field
	 */
	public long apply(long currentValue, long newValue) {
		switch (this) {
		case Last:
			return newValue;
		case Min:
			return Math.min(currentValue, newValue);
		case Max:
			return Math.max(currentValue, newValue);
		case Total:
			return currentValue + newValue;
		default:
			// Should be implemented
			throw new AssertionError();
		}
	}
}
