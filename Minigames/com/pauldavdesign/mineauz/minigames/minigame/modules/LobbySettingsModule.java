package com.pauldavdesign.mineauz.minigames.minigame.modules;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.config.BooleanFlag;
import com.pauldavdesign.mineauz.minigames.config.Flag;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;

public class LobbySettingsModule extends MinigameModule {
	
	private BooleanFlag canMovePlayerWait = new BooleanFlag(true, "canMovePlayerWait");
	private BooleanFlag canMoveStartWait = new BooleanFlag(true, "canMoveStartWait");
	private BooleanFlag canInteractPlayerWait = new BooleanFlag(true, "canInteractPlayerWait");
	private BooleanFlag canInteractStartWait = new BooleanFlag(true, "canInteractStartWait");
	private BooleanFlag teleportOnPlayerWait = new BooleanFlag(false, "teleportOnPlayerWait");
	private BooleanFlag teleportOnStart = new BooleanFlag(true, "teleportOnStart");
	
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
	
	public Callback<Boolean> getCanMovePlayerWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canMovePlayerWait.setFlag(value);
			}
			@Override
			public Boolean getValue(){
				return canMovePlayerWait.getFlag();
			}
		};
	}

	public boolean canMoveStartWait() {
		return canMoveStartWait.getFlag();
	}

	public void setCanMoveStartWait(boolean canMoveStartWait) {
		this.canMoveStartWait.setFlag(canMoveStartWait);
	}
	
	public Callback<Boolean> getCanMoveStartWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canMoveStartWait.setFlag(value);
			}
			@Override
			public Boolean getValue(){
				return canMoveStartWait.getFlag();
			}
		};
	}

	public boolean canInteractPlayerWait() {
		return canInteractPlayerWait.getFlag();
	}

	public void setCanInteractPlayerWait(boolean canInteractPlayerWait) {
		this.canInteractPlayerWait.setFlag(canInteractPlayerWait);
	}
	
	public Callback<Boolean> getCanInteractPlayerWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canInteractPlayerWait.setFlag(value);
			}
			@Override
			public Boolean getValue(){
				return canInteractPlayerWait.getFlag();
			}
		};
	}

	public boolean canInteractStartWait() {
		return canInteractStartWait.getFlag();
	}

	public void setCanInteractStartWait(boolean canInteractStartWait) {
		this.canInteractStartWait.setFlag(canInteractStartWait);
	}
	
	public Callback<Boolean> getCanInteractStartWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canInteractStartWait.setFlag(value);
			}
			@Override
			public Boolean getValue(){
				return canInteractStartWait.getFlag();
			}
		};
	}

	public boolean isTeleportOnStart() {
		return teleportOnStart.getFlag();
	}

	public void setTeleportOnStart(boolean teleportOnStart) {
		this.teleportOnStart.setFlag(teleportOnStart);
	}
	
	public Callback<Boolean> getTeleportOnStartCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				teleportOnStart.setFlag(value);
			}
			@Override
			public Boolean getValue(){
				return teleportOnStart.getFlag();
			}
		};
	}

	public boolean isTeleportOnPlayerWait() {
		return teleportOnPlayerWait.getFlag();
	}

	public void setTeleportOnPlayerWait(boolean teleportOnPlayerWait) {
		this.teleportOnPlayerWait.setFlag(teleportOnPlayerWait);
	}
	
	public Callback<Boolean> getTeleportOnPlayerWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				teleportOnPlayerWait.setFlag(value);
			}
			@Override
			public Boolean getValue(){
				return teleportOnPlayerWait.getFlag();
			}
		};
	}

	@Override
	public void addMenuOptions(Menu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getMenuOptions(Menu previous) {
		return false;
	}

}
