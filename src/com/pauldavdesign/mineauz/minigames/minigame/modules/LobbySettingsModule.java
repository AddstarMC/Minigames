package com.pauldavdesign.mineauz.minigames.minigame.modules;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;

public class LobbySettingsModule implements MinigameModule {
	
	private boolean canMovePlayerWait = true;
	private boolean canMoveStartWait = true;
	private boolean canInteractPlayerWait = true;
	private boolean canInteractStartWait = true;
	private boolean teleportOnPlayerWait = false;
	private boolean teleportOnStart = true;

	@Override
	public String getName() {
		return "LobbySettings";
	}

	@Override
	public void save(String minigame, FileConfiguration config) {
		if(!canInteractPlayerWait)
			config.set(minigame + ".canInteractPlayerWait", canInteractPlayerWait);
		if(!canInteractStartWait)
			config.set(minigame + ".canInteractStartWait", canInteractStartWait);
		if(!canMovePlayerWait)
			config.set(minigame + ".canMovePlayerWait", canMovePlayerWait);
		if(!canMoveStartWait)
			config.set(minigame + ".canMoveStartWait", canMoveStartWait);
		if(!teleportOnStart)
			config.set(minigame + ".teleportOnStart", teleportOnStart);
		if(teleportOnPlayerWait)
			config.set(minigame + ".teleportOnPlayerWait", teleportOnPlayerWait);
	}

	@Override
	public void load(String minigame, FileConfiguration config) {
		if(config.contains(minigame + ".canInteractPlayerWait"))
			canInteractPlayerWait = config.getBoolean(minigame + ".canInteractPlayerWait");
		if(config.contains(minigame + ".canInteractStartWait"))
			canInteractStartWait = config.getBoolean(minigame + ".canInteractStartWait");
		if(config.contains(minigame + ".canMovePlayerWait"))
			canMovePlayerWait = config.getBoolean(minigame + ".canMovePlayerWait");
		if(config.contains(minigame + ".canMoveStartWait"))
			canMoveStartWait = config.getBoolean(minigame + ".canMoveStartWait");
		if(config.contains(minigame + ".teleportOnStart"))
			teleportOnStart = config.getBoolean(minigame + ".teleportOnStart");
		if(config.contains(minigame + ".teleportOnPlayerWait"))
			teleportOnPlayerWait = config.getBoolean(minigame + ".teleportOnPlayerWait");
	}
	
	public static LobbySettingsModule getMinigameModule(Minigame minigame) {
		return (LobbySettingsModule) minigame.getModule("LobbySettings");
	}
	
	public boolean canMovePlayerWait() {
		return canMovePlayerWait;
	}

	public void setCanMovePlayerWait(boolean canMovePlayerWait) {
		this.canMovePlayerWait = canMovePlayerWait;
	}
	
	public Callback<Boolean> getCanMovePlayerWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canMovePlayerWait = value;
			}
			@Override
			public Boolean getValue(){
				return canMovePlayerWait;
			}
		};
	}

	public boolean canMoveStartWait() {
		return canMoveStartWait;
	}

	public void setCanMoveStartWait(boolean canMoveStartWait) {
		this.canMoveStartWait = canMoveStartWait;
	}
	
	public Callback<Boolean> getCanMoveStartWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canMoveStartWait = value;
			}
			@Override
			public Boolean getValue(){
				return canMoveStartWait;
			}
		};
	}

	public boolean canInteractPlayerWait() {
		return canInteractPlayerWait;
	}

	public void setCanInteractPlayerWait(boolean canInteractPlayerWait) {
		this.canInteractPlayerWait = canInteractPlayerWait;
	}
	
	public Callback<Boolean> getCanInteractPlayerWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canInteractPlayerWait = value;
			}
			@Override
			public Boolean getValue(){
				return canInteractPlayerWait;
			}
		};
	}

	public boolean canInteractStartWait() {
		return canInteractStartWait;
	}

	public void setCanInteractStartWait(boolean canInteractStartWait) {
		this.canInteractStartWait = canInteractStartWait;
	}
	
	public Callback<Boolean> getCanInteractStartWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canInteractStartWait = value;
			}
			@Override
			public Boolean getValue(){
				return canInteractStartWait;
			}
		};
	}

	public boolean isTeleportOnStart() {
		return teleportOnStart;
	}

	public void setTeleportOnStart(boolean teleportOnStart) {
		this.teleportOnStart = teleportOnStart;
	}
	
	public Callback<Boolean> getTeleportOnStartCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				teleportOnStart = value;
			}
			@Override
			public Boolean getValue(){
				return teleportOnStart;
			}
		};
	}

	public boolean isTeleportOnPlayerWait() {
		return teleportOnPlayerWait;
	}

	public void setTeleportOnPlayerWait(boolean teleportOnPlayerWait) {
		this.teleportOnPlayerWait = teleportOnPlayerWait;
	}
	
	public Callback<Boolean> getTeleportOnPlayerWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				teleportOnPlayerWait = value;
			}
			@Override
			public Boolean getValue(){
				return teleportOnPlayerWait;
			}
		};
	}

}
