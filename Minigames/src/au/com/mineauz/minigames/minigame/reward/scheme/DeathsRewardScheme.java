package au.com.mineauz.minigames.minigame.reward.scheme;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.sql.SQLPlayer;

public class DeathsRewardScheme extends HierarchyRewardScheme<Integer> {
	@Override
	protected Integer decrement(Integer value) {
		return value - 1;
	}
	
	@Override
	protected Integer increment(Integer value) {
		return value + 1;
	}
	
	@Override
	protected Integer loadValue(String key) {
		return Integer.valueOf(key);
	}
	
	@Override
	protected String getMenuItemDescName(Integer value) {
		return "Deaths: " + value;
	}
	
	@Override
	protected Integer getValue(MinigamePlayer player, SQLPlayer data, Minigame minigame) {
		return data.getDeaths();
	}
	
	@Override
	protected String getMenuItemName(Integer value) {
		return value.toString();
	}
}
