package au.com.mineauz.minigames.minigame.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;

public class LobbySettingsModule extends MinigameModule {
	private final ConfigPropertyContainer properties;
	
	private final BooleanProperty canMovePlayerWait = new BooleanProperty(true, "canMovePlayerWait");
	private final BooleanProperty canMoveStartWait = new BooleanProperty(true, "canMoveStartWait");
	private final BooleanProperty canInteractPlayerWait = new BooleanProperty(true, "canInteractPlayerWait");
	private final BooleanProperty canInteractStartWait = new BooleanProperty(true, "canInteractStartWait");
	private final BooleanProperty teleportOnPlayerWait = new BooleanProperty(false, "teleportOnPlayerWait");
	private final BooleanProperty teleportOnStart = new BooleanProperty(true, "teleportOnStart");
	private final IntegerProperty playerWaitTime = new IntegerProperty(0, "playerWaitTime");
	
	public LobbySettingsModule(Minigame mgm){
		super(mgm);
		
		properties = new ConfigPropertyContainer();
		properties.addProperty(canMovePlayerWait);
		properties.addProperty(canMoveStartWait);
		properties.addProperty(canInteractPlayerWait);
		properties.addProperty(canInteractStartWait);
		properties.addProperty(teleportOnPlayerWait);
		properties.addProperty(teleportOnStart);
		properties.addProperty(playerWaitTime);
	}

	@Override
	public String getName() {
		return "LobbySettings";
	}
	
	@Override
	public ConfigPropertyContainer getProperties() {
		return properties;
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
	
	@Deprecated
	public static LobbySettingsModule getMinigameModule(Minigame minigame) {
		return (LobbySettingsModule) minigame.getModule(LobbySettingsModule.class);
	}
	
	public boolean canMovePlayerWait() {
		return canMovePlayerWait.getValue();
	}

	public void setCanMovePlayerWait(boolean canMovePlayerWait) {
		this.canMovePlayerWait.setValue(canMovePlayerWait);
	}
	
	public boolean canMoveStartWait() {
		return canMoveStartWait.getValue();
	}

	public void setCanMoveStartWait(boolean canMoveStartWait) {
		this.canMoveStartWait.setValue(canMoveStartWait);
	}
	
	public boolean canInteractPlayerWait() {
		return canInteractPlayerWait.getValue();
	}

	public void setCanInteractPlayerWait(boolean canInteractPlayerWait) {
		this.canInteractPlayerWait.setValue(canInteractPlayerWait);
	}
	
	public boolean canInteractStartWait() {
		return canInteractStartWait.getValue();
	}

	public void setCanInteractStartWait(boolean canInteractStartWait) {
		this.canInteractStartWait.setValue(canInteractStartWait);
	}
	
	public boolean isTeleportOnStart() {
		return teleportOnStart.getValue();
	}

	public void setTeleportOnStart(boolean teleportOnStart) {
		this.teleportOnStart.setValue(teleportOnStart);
	}
	
	public boolean isTeleportOnPlayerWait() {
		return teleportOnPlayerWait.getValue();
	}

	public void setTeleportOnPlayerWait(boolean teleportOnPlayerWait) {
		this.teleportOnPlayerWait.setValue(teleportOnPlayerWait);
	}
	
	public int getPlayerWaitTime() {
		return playerWaitTime.getValue();
	}
	
	public void setPlayerWaitTime(int time) {
		playerWaitTime.setValue(time);
	}
	
	@Override
	public Menu createSettingsMenu() {
		Menu menu = new Menu(5, "Lobby Settings");
		
		menu.addItem(new MenuItemBoolean("Can Interact on Player Wait", Material.STONE_BUTTON, canInteractPlayerWait));
		menu.addItem(new MenuItemBoolean("Can Interact on Start Wait", Material.STONE_BUTTON, canInteractStartWait));
		menu.addItem(new MenuItemBoolean("Can Move on Player Wait", Material.ICE, canMovePlayerWait));
		menu.addItem(new MenuItemBoolean("Can Move on Start Wait", Material.ICE, canMoveStartWait));
		menu.addItem(new MenuItemBoolean("Teleport After Player Wait", "Should players be teleported;after player wait time?", Material.ENDER_PEARL, teleportOnPlayerWait));
		menu.addItem(new MenuItemBoolean("Teleport on Start", "Should players teleport;to the start position;after lobby?", Material.ENDER_PEARL, teleportOnStart));
		menu.addItem(new MenuItemTime("Waiting for Players Time", "The time in seconds;the game will wait for;more players to join.;A value of 0 will use;the config setting", Material.WATCH, playerWaitTime, 0, Integer.MAX_VALUE));
		
		return menu;
	}
}
