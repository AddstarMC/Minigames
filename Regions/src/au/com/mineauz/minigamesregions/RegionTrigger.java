package au.com.mineauz.minigamesregions;

public enum RegionTrigger {
	
	ENTER,
	LEAVE,
	TICK,
	REMOTE,
	GAME_START,
	GAME_QUIT,
	GAME_END,
	GAME_JOIN,
	TIMER;
	
	public static RegionTrigger getByName(String name){
		for(RegionTrigger t : RegionTrigger.values()){
			if(name.equalsIgnoreCase(t.toString())){
				return t;
			}
		}
		return null;
	}
}
