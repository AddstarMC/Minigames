package com.pauldavdesign.mineauz.minigames;

public enum MinigameToolMode {
	START("Start"),
	END("End"),
	QUIT("Quit"),
	LOBBY("Lobby"),
	REGEN_AREA("Regen Area"),
	DEGEN_AREA("Degen Area"),
	RESTORE_BLOCK("Restore Block");
	
	private String mode;
	
	private MinigameToolMode(String mode){
		this.mode = mode;
	}
	
	public String getMode(){
		return mode;
	}
	
	public static MinigameToolMode getByName(String name){
		if(name.equals(START.getMode()))
			return START;
		else if(name.equals(END.getMode()))
			return END;
		else if(name.equals(QUIT.getMode()))
			return QUIT;
		else if(name.equals(LOBBY.getMode()))
			return LOBBY;
		else if(name.equals(REGEN_AREA.getMode()))
			return REGEN_AREA;
		else if(name.equals(DEGEN_AREA.getMode()))
			return DEGEN_AREA;
		else if(name.equals(RESTORE_BLOCK.getMode()))
			return RESTORE_BLOCK;
		return null;
	}
}
