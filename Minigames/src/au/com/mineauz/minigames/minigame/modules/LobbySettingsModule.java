package au.com.mineauz.minigames.minigame.modules;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Minigame;

public class LobbySettingsModule extends MinigameModule {
	
	private BooleanFlag canMovePlayerWait = new BooleanFlag(true, "canMovePlayerWait");
	private BooleanFlag canMoveStartWait = new BooleanFlag(true, "canMoveStartWait");
	private BooleanFlag canInteractPlayerWait = new BooleanFlag(true, "canInteractPlayerWait");
	private BooleanFlag canInteractStartWait = new BooleanFlag(true, "canInteractStartWait");
	private BooleanFlag teleportOnPlayerWait = new BooleanFlag(false, "teleportOnPlayerWait");
	private BooleanFlag teleportOnStart = new BooleanFlag(true, "teleportOnStart");
	private IntegerFlag playerWaitTime = new IntegerFlag(0, "playerWaitTime");
	
	public LobbySettingsModule(Minigame mgm){
		super(mgm);
	}

	@Override
	public String getName() {
		return "LobbySettings";
	}
	
	@Override
	public Map<String, Flag<?>> getFlags(){
		Map<String, Flag<?>> map = new HashMap<String, Flag<?>>();
		addConfigFlag(canInteractPlayerWait, map);
		addConfigFlag(canInteractStartWait, map);
		addConfigFlag(canMovePlayerWait, map);
		addConfigFlag(canMoveStartWait, map);
		addConfigFlag(teleportOnPlayerWait, map);
		addConfigFlag(teleportOnStart, map);
		addConfigFlag(playerWaitTime, map);
		return map;
	}
	
	private void addConfigFlag(Flag<?> flag, Map<String, Flag<?>> flags){
		flags.put(flag.getName(), flag);
	}
	
	@Override
	public boolean useSeparateConfig(){
		return false;
	}

	@Override
	public void save(FileConfiguration config) {
	}

	@Override
	public void load(FileConfiguration config) {
	}
	
	public static LobbySettingsModule getMinigameModule(Minigame minigame) {
		return (LobbySettingsModule) minigame.getModule("LobbySettings");
	}
	
	public boolean canMovePlayerWait() {
		return canMovePlayerWait.getFlag();
	}

	public void setCanMovePlayerWait(boolean canMovePlayerWait) {
		this.canMovePlayerWait.setFlag(canMovePlayerWait);
	}
	
	public boolean canMoveStartWait() {
		return canMoveStartWait.getFlag();
	}

	public void setCanMoveStartWait(boolean canMoveStartWait) {
		this.canMoveStartWait.setFlag(canMoveStartWait);
	}
	
	public boolean canInteractPlayerWait() {
		return canInteractPlayerWait.getFlag();
	}

	public void setCanInteractPlayerWait(boolean canInteractPlayerWait) {
		this.canInteractPlayerWait.setFlag(canInteractPlayerWait);
	}
	
	public boolean canInteractStartWait() {
		return canInteractStartWait.getFlag();
	}

	public void setCanInteractStartWait(boolean canInteractStartWait) {
		this.canInteractStartWait.setFlag(canInteractStartWait);
	}
	
	public boolean isTeleportOnStart() {
		return teleportOnStart.getFlag();
	}

	public void setTeleportOnStart(boolean teleportOnStart) {
		this.teleportOnStart.setFlag(teleportOnStart);
	}
	
	public boolean isTeleportOnPlayerWait() {
		return teleportOnPlayerWait.getFlag();
	}

	public void setTeleportOnPlayerWait(boolean teleportOnPlayerWait) {
		this.teleportOnPlayerWait.setFlag(teleportOnPlayerWait);
	}
	
	public int getPlayerWaitTime() {
		return playerWaitTime.getFlag();
	}
	
	public void setPlayerWaitTime(int time) {
		playerWaitTime.setFlag(time);
	}
	
	@Override
	public Menu createSettingsMenu() {
		Menu menu = new Menu(5, "Lobby Settings");
		
		menu.addItem(canInteractPlayerWait.getMenuItem("Can Interact on Player Wait", Material.STONE_BUTTON));
		menu.addItem(canInteractStartWait.getMenuItem("Can Interact on Start Wait", Material.STONE_BUTTON));
		menu.addItem(canMovePlayerWait.getMenuItem("Can Move on Player Wait", Material.ICE));
		menu.addItem(canMoveStartWait.getMenuItem("Can Move on Start Wait", Material.ICE));
		menu.addItem(teleportOnPlayerWait.getMenuItem("Teleport After Player Wait", Material.ENDER_PEARL, MinigameUtils.stringToList("Should players be teleported;after player wait time?")));
		menu.addItem(teleportOnStart.getMenuItem("Teleport on Start", Material.ENDER_PEARL, MinigameUtils.stringToList("Should players teleport;to the start position;after lobby?")));
		menu.addItem(playerWaitTime.getMenuItem("Waiting for Players Time", Material.WATCH, MinigameUtils.stringToList("The time in seconds;the game will wait for;more players to join.;A value of 0 will use;the config setting")));
		
		return menu;
	}
}
