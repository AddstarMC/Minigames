package au.com.mineauz.minigames.stats;

class BasicMinigameStat extends MinigameStat {
	private final boolean isSpecial;
	
	BasicMinigameStat(String name, String displayName, StatFormat format, boolean isSpecial) {
		super(name, format);
		setDisplayName(displayName);
		this.isSpecial = isSpecial;
	}
	
	BasicMinigameStat(String name, String displayName, StatFormat format) {
		this(name, displayName, format, false);
	}
	
	@Override
	public boolean canOverrideFormat() {
		return !isSpecial;
	}
	
	@Override
	public String displayValue(long value) {
		if (this == MinigameStats.CompletionTime) {
			return (value / 1000) + " seconds";
		} else {
			return String.valueOf(value) + " " + getDisplayName();
		}
	}
	
	@Override
	public String displayValueSign(long value) {
		if (this == MinigameStats.CompletionTime) {
			return (value / 1000) + "sec";
		} else {
			return String.valueOf(value) + " " + getDisplayName();
		}
	}
}
