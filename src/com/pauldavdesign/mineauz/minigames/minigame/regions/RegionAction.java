package com.pauldavdesign.mineauz.minigames.minigame.regions;

public enum RegionAction {
	END("end"),
	QUIT("quit"),
	REVERT("revert"),
	KILL("kill"),
	TELEPORT("teleport");
	
	private String name;
	
	private RegionAction(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static RegionAction getByName(String name){
		for(RegionAction t : RegionAction.values()){
			if(name.equalsIgnoreCase(t.getName())){
				return t;
			}
		}
		return null;
	}
}
