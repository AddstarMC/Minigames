package au.com.mineauz.minigames.minigame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scoreboard.NameTagVisibility;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.properties.AbstractProperty;
import au.com.mineauz.minigames.properties.ChangeListener;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.ObservableValue;
import au.com.mineauz.minigames.properties.Property;
import au.com.mineauz.minigames.properties.types.EnumProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigames.properties.types.StringProperty;

public class Team {
	private String displayName = null;
	private TeamColor color;
	
	private final ConfigPropertyContainer properties = new ConfigPropertyContainer();
	private final IntegerProperty maxPlayers = new IntegerProperty(0, "maxPlayers");
	private final List<Location> startLocations = new ArrayList<Location>();
	private final StringProperty assignMsg = new StringProperty(MinigameUtils.getLang("player.team.assign.joinTeam"), "assignMsg");
	private final StringProperty gameAssignMsg = new StringProperty(MinigameUtils.getLang("player.team.assign.joinAnnounce"), "gameAssignMsg");
	private final StringProperty autobalanceMsg = new StringProperty(MinigameUtils.getLang("player.team.autobalance.plyMsg"), "autobalanceMsg");
	private final StringProperty gameAutobalanceMsg = new StringProperty(MinigameUtils.getLang("player.team.autobalance.minigameMsg"), "gameAutobalanceMsg");
	private final EnumProperty<NameTagVisibility> nametagVisibility = new EnumProperty<NameTagVisibility>(NameTagVisibility.ALWAYS, "nametagVisibility");
	
	private List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
	private int score = 0;
	private Minigame mgm;
	
	/**
	 * Creates a team for the use in a specific Minigame
	 * @param color - The unique team color to identify the team by.
	 * @param minigame - The Minigame this team is assigned to.
	 */
	public Team(TeamColor color, Minigame minigame){
		this.color = color;
		displayName = MinigameUtils.capitalize(color.toString()) + " Team";
		mgm = minigame;
		
		properties.addProperty(maxPlayers);
		properties.addProperty(assignMsg);
		properties.addProperty(gameAssignMsg);
		properties.addProperty(autobalanceMsg);
		properties.addProperty(gameAutobalanceMsg);
		properties.addProperty(nametagVisibility);
		
		nametagVisibility.addListener(new ChangeListener<NameTagVisibility>() {
			@Override
			public void onValueChange(ObservableValue<? extends NameTagVisibility> observable, NameTagVisibility oldValue, NameTagVisibility newValue) {
				mgm.getScoreboardManager().getTeam(Team.this.color.toString().toLowerCase()).setNameTagVisibility(newValue);
			}
		});
	}
	/**
	 * Gets the teams Minigame
	 * @return The Minigame this team is assigned to.
	 */
	public Minigame getMinigame(){
		return mgm;
	}
	
	/**
	 * Changes the color of the team for the Minigame its assigned to.
	 * @param color - The color to change this team to.
	 * @return true if the Minigame doesn't have the team color already available, fails if it already has that team.
	 */
	public boolean setColor(TeamColor color){
		TeamsModule module = mgm.getModule(TeamsModule.class);
		if(!module.hasTeam(color)){
			module.removeTeam(this.color);
			if(displayName.equals(this.color.toString().toLowerCase() + " team"))
				displayName = MinigameUtils.capitalize(color.toString()) + " Team";
			this.color = color;
			module.addTeam(color, this);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the teams color.
	 * @return The teams color.
	 */
	public TeamColor getColor(){
		return color;
	}
	
	/**
	 * Gets the teams ChatColor alternative.
	 * @return The ChatColor
	 */
	public ChatColor getChatColor(){
		return color.getColor();
	}
	
	/**
	 * Sets the display name for this team. If the name is longer than 32 characters, 
	 * it'll be trimmed to that length (Minecraft limitation).
	 * @param name - The name to change the team to.
	 */
	public void setDisplayName(String name){
		if(name.length() > 32)
			name = name.substring(0, 31);
		displayName = name;
	}
	
	/**
	 * Gets the teams display name. If none is set, it will return the color folowed by "Team".
	 * @return The display name or the color followed by "Team"
	 */
	public String getDisplayName(){
		return displayName;
	}
	
	public Property<String> displayName() {
		return new AbstractProperty<String>() {
			@Override
			public void setValue(String value) {
				setDisplayName(value);
			}
			
			@Override
			public String getValue() {
				return getDisplayName();
			}
		};
	}
	
	public ConfigPropertyContainer getProperties() {
		return properties;
	}
	
	public int getMaxPlayers() {
		return maxPlayers.getValue();
	}
	
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers.setValue(maxPlayers);
	}
	
	public Property<Integer> maxPlayers() {
		return maxPlayers;
	}
	
	public boolean isFull(){
		if(maxPlayers.getValue() != 0 && players.size() >= maxPlayers.getValue())
			return true;
		return false;
	}
	
	
	/**
	 * Gets the teams current score
	 * @return The score.
	 */
	public int getScore(){
		return score;
	}
	
	/**
	 * Adds 1 point to the team.
	 */
	public int addScore(){
		return addScore(1);
	}
	
	/**
	 * Adds a specific amount to the teams score.
	 * @param amount - The amount of points to add to the team
	 * @return The new score for the team.
	 */
	public int addScore(int amount){
		score += amount;
		mgm.getScoreboardManager().getObjective(mgm.getName(false)).getScore(getChatColor() + getDisplayName()).setScore(score);
		return score;
	}
	
	/**
	 * Sets the teams score to a specific value.
	 * @param amount The score amount to set for the team.
	 */
	public void setScore(int amount){
		score = amount;
		mgm.getScoreboardManager().getObjective(mgm.getName(false)).getScore(getChatColor() + getDisplayName()).setScore(score);
	}
	
	/**
	 * Sets the teams score back to 0.
	 */
	public void resetScore(){
		score = 0;
		mgm.getScoreboardManager().resetScores(getChatColor() + getDisplayName());
	}
	
	/**
	 * Gets a list of all the players assigned to this team.
	 * @return A list of all players assigned to the team.
	 */
	public List<MinigamePlayer> getPlayers(){
		return players;
	}
	
	/**
	 * Adds a player to the team.
	 * @param player - The player to add.
	 */
	public void addPlayer(MinigamePlayer player){
		players.add(player);
		player.setTeam(this);
		player.getPlayer().setScoreboard(mgm.getScoreboardManager());
		mgm.getScoreboardManager().getTeam(getColor().toString().toLowerCase()).addPlayer(player.getPlayer());
	}
	
	/**
	 * Removes a player from the team.
	 * @param player - The player to remove.
	 */
	public void removePlayer(MinigamePlayer player){
		players.remove(player);
		mgm.getScoreboardManager().getTeam(getColor().toString().toLowerCase()).removePlayer(player.getPlayer());
		player.getPlayer().setScoreboard(Minigames.plugin.getServer().getScoreboardManager().getMainScoreboard());
	}
	
	/**
	 * Adds a starting location for the team to spawn at.
	 * @param loc - The location to add to the team.
	 */
	public void addStartLocation(Location loc){
		startLocations.add(loc);
	}
	
	/**
	 * Replaces a starting location already assigned on the team.
	 * @param loc - The new location
	 * @param number - The number id of the original starting location (Ranging from 1 to the amount of start points [Not 0])
	 */
	public void addStartLocation(Location loc, int number){
		if(startLocations.size() >= number){
			startLocations.set(number - 1, loc);
		}
		else{
			startLocations.add(loc);
		}
	}
	
	/**
	 * Gets all the starting locations for this team.
	 * @return The teams starting locations.
	 */
	public List<Location> getStartLocations(){
		return startLocations;
	}
	
	/**
	 * Gets whether the team has start locations
	 * @return true if the team has start locations
	 */
	public boolean hasStartLocations(){
		return !startLocations.isEmpty();
	}
	
	/**
	 * Removes a specific start location from this team.
	 * @param locNumber - The id of the starting location.
	 * @return true if removal was successful.
	 */
	public boolean removeStartLocation(int locNumber){
		if(startLocations.size() < locNumber){
			startLocations.remove(locNumber);
			return true;
		}
		return false;
	}
	
	public String getAssignMessage(){
		return assignMsg.getValue();
	}
	
	public void setAssignMessage(String msg){
		assignMsg.setValue(msg);
	}
	
	public Property<String> assignMessage() {
		return assignMsg;
	}
	
	public String getGameAssignMessage(){
		return gameAssignMsg.getValue();
	}
	
	public void setGameAssignMessage(String msg){
		gameAssignMsg.setValue(msg);
	}
	
	public Property<String> gameAssignMessage() {
		return gameAssignMsg;
	}
	
	public String getAutobalanceMessage(){
		return autobalanceMsg.getValue();
	}
	
	public void setAutobalanceMessage(String msg){
		autobalanceMsg.setValue(msg);
	}
	
	public Property<String> autobalanceMessage() {
		return autobalanceMsg;
	}
	
	public String getGameAutobalanceMessage(){
		return gameAutobalanceMsg.getValue();
	}
	
	public void setGameAutobalanceMessage(String msg){
		gameAutobalanceMsg.setValue(msg);
	}
	
	public Property<String> gameAutobalanceMessage() {
		return gameAutobalanceMsg;
	}
	
	public NameTagVisibility getNameTagVisibility(){
		return nametagVisibility.getValue();
	}
	
	public void setNameTagVisibility(NameTagVisibility vis){
		nametagVisibility.setValue(vis);
	}
	
	public Property<NameTagVisibility> nameTagVisibility() {
		return nametagVisibility;
	}
}
