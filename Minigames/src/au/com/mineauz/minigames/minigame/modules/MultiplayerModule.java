package au.com.mineauz.minigames.minigame.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigames.properties.types.LocationProperty;

public class MultiplayerModule extends MinigameModule {

	private final ConfigPropertyContainer properties;
	private final IntegerProperty minPlayers = new IntegerProperty(2, "minplayers");
	private final IntegerProperty maxPlayers = new IntegerProperty(4, "maxplayers");
	private final LocationProperty lobbyPosisiton = new LocationProperty(null, "lobbypos");
	private final IntegerProperty timer = new IntegerProperty(0, "timer");
	private final BooleanProperty useXPBarTimer = new BooleanProperty(true, "useXPBarTimer");
	private final IntegerProperty startWaitTime = new IntegerProperty(0, "startWaitTime");
	private final BooleanProperty lateJoin = new BooleanProperty(false, "latejoin");
	
	public MultiplayerModule(Minigame mgm) {
		super(mgm);
		
		properties = new ConfigPropertyContainer();
		properties.addProperty(minPlayers);
		properties.addProperty(maxPlayers);
		properties.addProperty(lobbyPosisiton);
		properties.addProperty(timer);
		properties.addProperty(useXPBarTimer);
		properties.addProperty(startWaitTime);
		properties.addProperty(lateJoin);
	}

	@Override
	public String getName() {
		return "Multiplayer";
	}

	@Override
	public ConfigPropertyContainer getProperties() {
		return properties;
	}
	
	public int getMinPlayers(){
		return minPlayers.getValue();
	}

	public void setMinPlayers(int minPlayers){
		this.minPlayers.setValue(minPlayers);
	}
	
	public int getMaxPlayers(){
		return maxPlayers.getValue();
	}

	public void setMaxPlayers(int maxPlayers){
		this.maxPlayers.setValue(maxPlayers);
	}
	
	public Location getLobbyPosition(){
		return lobbyPosisiton.getValue();
	}

	public void setLobbyPosition(Location lobbyPosisiton){
		this.lobbyPosisiton.setValue(lobbyPosisiton);
	}
	
	public void setTimer(int time){
		timer.setValue(time);
	}
	
	public int getTimer(){
		return timer.getValue();
	}

	public boolean isUsingXPBarTimer() {
		return useXPBarTimer.getValue();
	}

	public void setUseXPBarTimer(boolean useXPBarTimer) {
		this.useXPBarTimer.setValue(useXPBarTimer);
	}

	public int getStartWaitTime() {
		return startWaitTime.getValue();
	}

	public void setStartWaitTime(int startWaitTime) {
		this.startWaitTime.setValue(startWaitTime);
	}
	
	public boolean canLateJoin() {
		return lateJoin.getValue();
	}

	public void setLateJoin(boolean lateJoin) {
		this.lateJoin.setValue(lateJoin);
	}

	@Override
	public boolean useSeparateConfig() {
		return false;
	}

	@Override
	public void save(FileConfiguration config) {
	}

	@Override
	public void load(FileConfiguration config) {
	}

	public void addGameTypeMenuItems(Menu menu) {
		menu.addItem(new MenuItemInteger("Min. Players", Material.STEP, minPlayers, 0, Integer.MAX_VALUE));
		menu.addItem(new MenuItemInteger("Max. Players", Material.STONE, maxPlayers, 0, Integer.MAX_VALUE));
		
		menu.addItem(new MenuItemSubMenu("Lobby Settings", Material.WOOD_DOOR, getMinigame().getModule(LobbySettingsModule.class).createSettingsMenu()));
		menu.addItem(new MenuItemNewLine());
		menu.addItem(new MenuItemTime("Time Length", Material.WATCH, timer, 0, Integer.MAX_VALUE));
		menu.addItem(new MenuItemBoolean("Use XP bar as Timer", Material.ENDER_PEARL, useXPBarTimer));
		menu.addItem(new MenuItemTime("Start Wait Time", Material.WATCH, startWaitTime, 3, Integer.MAX_VALUE));
		menu.addItem(new MenuItemBoolean("Allow Late Join", Material.DEAD_BUSH, lateJoin));
	}
}
