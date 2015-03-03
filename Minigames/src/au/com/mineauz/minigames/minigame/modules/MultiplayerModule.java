package au.com.mineauz.minigames.minigame.modules;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.LocationFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.minigame.Minigame;

public class MultiplayerModule extends MinigameModule {

	private IntegerFlag minPlayers = new IntegerFlag(2, "minplayers");
	private IntegerFlag maxPlayers = new IntegerFlag(4, "maxplayers");
	private LocationFlag lobbyPosisiton = new LocationFlag(null, "lobbypos");
	private IntegerFlag timer = new IntegerFlag(0, "timer");
	private BooleanFlag useXPBarTimer = new BooleanFlag(true, "useXPBarTimer");
	private IntegerFlag startWaitTime = new IntegerFlag(0, "startWaitTime");
	private BooleanFlag lateJoin = new BooleanFlag(false, "latejoin");
	
	public MultiplayerModule(Minigame mgm) {
		super(mgm);
	}

	@Override
	public String getName() {
		return "Multiplayer";
	}

	@Override
	public Map<String, Flag<?>> getFlags() {
		Map<String, Flag<?>> flags = new HashMap<String, Flag<?>>();
		flags.put(minPlayers.getName(), minPlayers);
		flags.put(maxPlayers.getName(), maxPlayers);
		flags.put(lobbyPosisiton.getName(), lobbyPosisiton);
		flags.put(timer.getName(), timer);
		flags.put(useXPBarTimer.getName(), useXPBarTimer);
		flags.put(startWaitTime.getName(), startWaitTime);
		flags.put(lateJoin.getName(), lateJoin);
		return flags;
	}
	
	public int getMinPlayers(){
		return minPlayers.getFlag();
	}

	public void setMinPlayers(int minPlayers){
		this.minPlayers.setFlag(minPlayers);
	}
	
	public int getMaxPlayers(){
		return maxPlayers.getFlag();
	}

	public void setMaxPlayers(int maxPlayers){
		this.maxPlayers.setFlag(maxPlayers);
	}
	
	public Location getLobbyPosition(){
		return lobbyPosisiton.getFlag();
	}

	public void setLobbyPosition(Location lobbyPosisiton){
		this.lobbyPosisiton.setFlag(lobbyPosisiton);
	}
	
	public void setTimer(int time){
		timer.setFlag(time);
	}
	
	public int getTimer(){
		return timer.getFlag();
	}

	public boolean isUsingXPBarTimer() {
		return useXPBarTimer.getFlag();
	}

	public void setUseXPBarTimer(boolean useXPBarTimer) {
		this.useXPBarTimer.setFlag(useXPBarTimer);
	}

	public int getStartWaitTime() {
		return startWaitTime.getFlag();
	}

	public void setStartWaitTime(int startWaitTime) {
		this.startWaitTime.setFlag(startWaitTime);
	}
	
	public boolean canLateJoin() {
		return lateJoin.getFlag();
	}

	public void setLateJoin(boolean lateJoin) {
		this.lateJoin.setFlag(lateJoin);
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
		menu.addItem(minPlayers.getMenuItem("Min. Players", Material.STEP));
		menu.addItem(maxPlayers.getMenuItem("Max. Players", Material.STONE));
		
		menu.addItem(new MenuItemSubMenu("Lobby Settings", Material.WOOD_DOOR, getMinigame().getModule(LobbySettingsModule.class).createSettingsMenu()));
		menu.addItem(new MenuItemNewLine());
		menu.addItem(new MenuItemTime("Time Length", Material.WATCH, timer.getCallback(), 0, Integer.MAX_VALUE));
		menu.addItem(useXPBarTimer.getMenuItem("Use XP bar as Timer", Material.ENDER_PEARL));
		menu.addItem(new MenuItemTime("Start Wait Time", Material.WATCH, startWaitTime.getCallback(), 3, Integer.MAX_VALUE));
		menu.addItem(lateJoin.getMenuItem("Allow Late Join", Material.DEAD_BUSH));
	}
}
