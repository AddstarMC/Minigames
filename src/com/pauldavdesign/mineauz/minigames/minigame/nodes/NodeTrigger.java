package com.pauldavdesign.mineauz.minigames.minigame.nodes;

public enum NodeTrigger {
	
	NONE("None"),
	INTERACT("Interact"),
	REMOTE("Remote");
	
	private String name;
	
	private NodeTrigger(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static NodeTrigger getByName(String name){
		for(NodeTrigger t : NodeTrigger.values()){
			if(name.equalsIgnoreCase(t.getName())){
				return t;
			}
		}
		return null;
	}
}
