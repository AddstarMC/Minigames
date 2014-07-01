package au.com.mineauz.minigamesregions;

public enum NodeTrigger {
	
	NONE,
	INTERACT,
	REMOTE,
	GAME_START,
	GAME_QUIT,
	GAME_END,
	GAME_JOIN,
	TIMER,
	BLOCK_BROKEN,
	BLOCK_PLACED;
	
	public static NodeTrigger getByName(String name){
		for(NodeTrigger t : NodeTrigger.values()){
			if(name.equalsIgnoreCase(t.toString())){
				return t;
			}
		}
		return null;
	}
}
