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
	Last,
	/**
	 * The latest value assigned to this stat is recorded
	 * and a summation of all values stored is kept.
	 * The stored keys will be {@code '<name>'} and {@code '<name>_total'}
	 */
	LastAndTotal,
	/**
	 * The minimum value of this stat is kept.
	 * The stored key will be {@code '<name>'}
	 */
	Min,
	/**
	 * The minimum value of this stat is kept
	 * and a summation of all values input is kept.
	 * The stored keys will be {@code '<name>'} and {@code '<name>_total'}
	 */
	MinAndTotal,
	/**
	 * The maximum value of this stat is kept.
	 * The stored key will be {@code '<name>'}
	 */
	Max,
	/**
	 * The maximum value of this stat is kept
	 * and a summation of all values input is kept.
	 * The stored keys will be {@code '<name>'} and {@code '<name>_total'}
	 */
	MaxAndTotal,
	/**
	 * The minimum and maximum values of this stat are kept.
	 * The stored keys will be {@code '<name>_min'} and {@code '<name>_max'}
	 */
	MinMax,
	/**
	 * The minimum and maximum values or this stat are kept
	 * and a summation of all values input is kept.
	 * The stored keys will be {@code '<name>_min'} and {@code '<name>_max'} and {@code '<name>_total'}
	 */
	MinMaxAndTotal,
	/**
	 * A summation of all values input is recorded.
	 * The stored key will be {@code '<name>'}
	 */
	Total
}
