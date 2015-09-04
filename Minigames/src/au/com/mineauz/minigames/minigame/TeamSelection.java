package au.com.mineauz.minigames.minigame;

public enum TeamSelection {
	NONE(null),
	RED(TeamColor.RED),
	BLUE(TeamColor.BLUE),
	GREEN(TeamColor.GREEN),
	YELLOW(TeamColor.YELLOW),
	PURPLE(TeamColor.PURPLE),
	WHITE(TeamColor.WHITE),
	BLACK(TeamColor.BLACK),
	DARK_RED(TeamColor.DARK_RED),
	DARK_PURPLE(TeamColor.DARK_PURPLE),
	DARK_GREEN(TeamColor.DARK_GREEN),
	DARK_BLUE(TeamColor.DARK_BLUE),
	GRAY(TeamColor.GRAY);
	
	private TeamColor team;
	
	private TeamSelection(TeamColor team) {
		this.team = team;
	}
	
	public TeamColor getTeam() {
		return team;
	}
	
	public static TeamSelection from(TeamColor color) {
		for (TeamSelection selection : values()) {
			if (selection.getTeam() == color) {
				return selection;
			}
		}
		
		return NONE;
	}
	
	public static TeamSelection from(String name) {
		TeamSelection team = valueOf(name.toUpperCase());
		if (team != null) {
			return team;
		}
		
		return NONE;
	}
}
