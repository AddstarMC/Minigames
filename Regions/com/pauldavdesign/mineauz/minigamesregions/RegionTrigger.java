package com.pauldavdesign.mineauz.minigamesregions;

public enum RegionTrigger {
	
	ENTER("Enter"),
	LEAVE("Leave"),
	TICK("Tick");
	
	private String name;
	
	private RegionTrigger(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static RegionTrigger getByName(String name){
		for(RegionTrigger t : RegionTrigger.values()){
			if(name.equalsIgnoreCase(t.getName())){
				return t;
			}
		}
		return null;
	}
}
