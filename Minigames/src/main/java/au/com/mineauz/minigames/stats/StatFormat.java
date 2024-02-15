package au.com.mineauz.minigames.stats;

/**
 * The value of StatFormat determines how a stat is stored.
 *
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
    Last(StatisticValueField.Last),
    /**
     * The latest value assigned to this stat is recorded
     * and a summation of all values stored is kept.
     * The stored keys will be {@code '<name>'} and {@code '<name>_total'}
     */
    LastAndTotal(StatisticValueField.Last, StatisticValueField.Total),
    /**
     * The minimum value of this stat is kept.
     * The stored key will be {@code '<name>'}
     */
    Min(StatisticValueField.Min),
    /**
     * The minimum value of this stat is kept
     * and a summation of all values input is kept.
     * The stored keys will be {@code '<name>'} and {@code '<name>_total'}
     */
    MinAndTotal(StatisticValueField.Min, StatisticValueField.Total),
    /**
     * The maximum value of this stat is kept.
     * The stored key will be {@code '<name>'}
     */
    Max(StatisticValueField.Max),
    /**
     * The maximum value of this stat is kept
     * and a summation of all values input is kept.
     * The stored keys will be {@code '<name>'} and {@code '<name>_total'}
     */
    MaxAndTotal(StatisticValueField.Max, StatisticValueField.Total),
    /**
     * The minimum and maximum values of this stat are kept.
     * The stored keys will be {@code '<name>_min'} and {@code '<name>_max'}
     */
    MinMax(StatisticValueField.Min, StatisticValueField.Max),
    /**
     * The minimum and maximum values or this stat are kept
     * and a summation of all values input is kept.
     * The stored keys will be {@code '<name>_min'} and {@code '<name>_max'} and {@code '<name>_total'}
     */
    MinMaxAndTotal(StatisticValueField.Min, StatisticValueField.Max, StatisticValueField.Total),
    /**
     * A summation of all values input is recorded.
     * The stored key will be {@code '<name>'}
     */
    Total(StatisticValueField.Total);

    private final StatisticValueField[] fields;

    StatFormat(StatisticValueField... fields) {
        this.fields = fields;
    }

    /**
     * @return Returns all fields in use by this format
     */
    public StatisticValueField[] getFields() {
        return fields;
    }
}
