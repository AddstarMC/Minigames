package au.com.mineauz.minigames.stats;

class BasicMinigameStat extends MinigameStat {
	private final boolean isSpecial;
	
	BasicMinigameStat(String name, StatFormat format, boolean isSpecial) {
		super(name, format);
		this.isSpecial = isSpecial;
	}
	
	BasicMinigameStat(String name, StatFormat format) {
		this(name, format, false);
	}
	
	@Override
	public boolean canOverrideFormat() {
		return !isSpecial;
	}
}
