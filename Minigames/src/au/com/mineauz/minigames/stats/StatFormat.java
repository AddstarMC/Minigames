package au.com.mineauz.minigames.stats;

/**
 * The value of StatFormat determines how a stat is stored.
 * @see #Last
 * @see #LastAndTotal
 * @see #Min
 * @see #MinAndTotal
 * @see #Max
 * @see #MaxAndTotal
 * @see #MinMax
 * @see #MinMaxAndTotal
 * @see #Total 
 */
public enum StatFormat {
	/**
	 * The latest value assigned to this stat is recorded.
	 * The stored key will be {@code '<name>'}
	 */
	Last(StatValueField.Last),
	/**
	 * The latest value assigned to this stat is recorded
	 * and a summation of all values stored is kept.
	 * The stored keys will be {@code '<name>'} and {@code '<name>_total'}
	 */
	LastAndTotal(StatValueField.Last, StatValueField.Total),
	/**
	 * The minimum value of this stat is kept.
	 * The stored key will be {@code '<name>'}
	 */
	Min(StatValueField.Min),
	/**
	 * The minimum value of this stat is kept
	 * and a summation of all values input is kept.
	 * The stored keys will be {@code '<name>'} and {@code '<name>_total'}
	 */
	MinAndTotal(StatValueField.Min, StatValueField.Total),
	/**
	 * The maximum value of this stat is kept.
	 * The stored key will be {@code '<name>'}
	 */
	Max(StatValueField.Max),
	/**
	 * The maximum value of this stat is kept
	 * and a summation of all values input is kept.
	 * The stored keys will be {@code '<name>'} and {@code '<name>_total'}
	 */
	MaxAndTotal(StatValueField.Max, StatValueField.Total),
	/**
	 * The minimum and maximum values of this stat are kept.
	 * The stored keys will be {@code '<name>_min'} and {@code '<name>_max'}
	 */
	MinMax(StatValueField.Min, StatValueField.Max),
	/**
	 * The minimum and maximum values or this stat are kept
	 * and a summation of all values input is kept.
	 * The stored keys will be {@code '<name>_min'} and {@code '<name>_max'} and {@code '<name>_total'}
	 */
	MinMaxAndTotal(StatValueField.Min, StatValueField.Max, StatValueField.Total),
	/**
	 * A summation of all values input is recorded.
	 * The stored key will be {@code '<name>'}
	 */
	Total(StatValueField.Total);
	
	private StatValueField[] fields;
	
	private StatFormat(StatValueField... fields) {
		this.fields = fields;
	}
	
	/**
	 * @return Returns all fields in use by this format
	 */
	public StatValueField[] getFields() {
		return fields;
	}
	
	/**
	 * Gets the suffix used to save this field in the context of this format
	 * @param field The field being saved
	 * @return The suffix to apply
	 */
	public String getFieldSuffix(StatValueField field) {
		switch (this) {
		case LastAndTotal:
		case MaxAndTotal:
		case MinAndTotal:
			if (field == StatValueField.Total) {
				return "_total";
			}
			break;
		case MinMaxAndTotal:
			if (field == StatValueField.Total) {
				return "_total";
			}
		case MinMax:
			if (field == StatValueField.Min) {
				return "_min";
			} else if (field == StatValueField.Max) {
				return "_max";
			}
			break;
		default:
			break;
		}
		
		return "";
	}
	
	/**
	 * Gets the field from the specified suffix
	 * @param suffix The suffix to use
	 * @return The field type
	 */
	public StatValueField getFieldBySuffix(String suffix) {
		if (suffix.equals("_total")) {
			return StatValueField.Total;
		} else if (suffix.equals("_min")) {
			return StatValueField.Min;
		} else if (suffix.equals("_max")) {
			return StatValueField.Max;
		} else {
			switch (this) {
			case Last:
			case LastAndTotal:
				return StatValueField.Last;
			case Max:
			case MaxAndTotal:
				return StatValueField.Max;
			case Min:
			case MinAndTotal:
				return StatValueField.Min;
			case Total:
				return StatValueField.Total;
			default:
				// All other cases should have been covered by the initial checks
				throw new AssertionError();
			}
		}
	}
}
