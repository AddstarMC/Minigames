package au.com.mineauz.minigames;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.sounds.MGSounds;
import au.com.mineauz.minigames.sounds.PlayMGSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class MultiplayerTimer{
	private int playerWaitTime;
	private int oPlayerWaitTime;
	private int startWaitTime;
	private int oStartWaitTime;
	private Minigame minigame;
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private boolean paused = false;
	private int taskID = -1;
	private List<Integer> timeMsg = new ArrayList<Integer>();
	
	public MultiplayerTimer(Minigame mg){
		minigame = mg;
		
		playerWaitTime = LobbySettingsModule.getMinigameModule(mg).getPlayerWaitTime();
		
		if (playerWaitTime == 0) {
			playerWaitTime = plugin.getConfig().getInt("multiplayer.waitforplayers");
			if(playerWaitTime <= 0)
				playerWaitTime = 10;
		}
		oPlayerWaitTime = playerWaitTime;
		startWaitTime = minigame.getStartWaitTime();  //minigames setting should be priority over general plugin config.
		if(startWaitTime == 0	){
			startWaitTime = plugin.getConfig().getInt("multiplayer.startcountdown");
			if(startWaitTime <= 0)
				startWaitTime = 5;
		}
		oStartWaitTime = startWaitTime;
		timeMsg.addAll(plugin.getConfig().getIntegerList("multiplayer.timerMessageInterval"));
	}
	
	public void startTimer(){
		if(taskID != -1)
			removeTimer();
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				
				if(playerWaitTime != 0 && !paused){
					if(playerWaitTime == oPlayerWaitTime){
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.getLang("time.startup.waitingForPlayers"));
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.formStr("time.startup.time", playerWaitTime));
                        allowInteraction(LobbySettingsModule.getMinigameModule(minigame).canInteractPlayerWait());
                        freezePlayers(!LobbySettingsModule.getMinigameModule(minigame).canMovePlayerWait());
                        minigame.setState(MinigameState.WAITING);
					}
					else if(timeMsg.contains(playerWaitTime)){
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.formStr("time.startup.time", playerWaitTime));
						PlayMGSound.playSound(minigame, MGSounds.getSound("timerTick"));
					}
				}
				else if(playerWaitTime == 0 && startWaitTime != 0 && !paused){
					if(startWaitTime == oStartWaitTime){
						minigame.setState(MinigameState.STARTING);
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.getLang("time.startup.minigameStarts"));
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.formStr("time.startup.time", startWaitTime));
						freezePlayers(!LobbySettingsModule.getMinigameModule(minigame).canMoveStartWait());
						allowInteraction(LobbySettingsModule.getMinigameModule(minigame).canInteractStartWait());
						if(LobbySettingsModule.getMinigameModule(minigame).isTeleportOnPlayerWait()){
							reclearInventories(minigame);
							pdata.balanceGame(minigame);
							pdata.getStartLocations(minigame.getPlayers(),minigame);
                            pdata.teleportToStart(minigame);
                        }
					}
					else if(timeMsg.contains(startWaitTime)){
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.formStr("time.startup.time", startWaitTime));
						PlayMGSound.playSound(minigame, MGSounds.getSound("timerTick"));
					}
				}
				else if(playerWaitTime == 0 && startWaitTime == 0){
					sendPlayersMessage(ChatColor.GREEN + MinigameUtils.getLang("time.startup.go"));
					reclearInventories(minigame);
					if(LobbySettingsModule.getMinigameModule(minigame).isTeleportOnStart()) {
                        pdata.startMPMinigame(minigame,true);
                        pdata.teleportToStart(minigame);
                    }else{
					    pdata.startMPMinigame(minigame);
                    }
                    freezePlayers(false);
					allowInteraction(true);
					
					if(minigame.getFloorDegen1() != null && minigame.getFloorDegen2() != null){
						minigame.addFloorDegenerator();
						minigame.getFloorDegenerator().startDegeneration();
					}
					
					if(minigame.getTimer() > 0){
						minigame.setMinigameTimer(new MinigameTimer(minigame, minigame.getTimer()));
						plugin.mdata.sendMinigameMessage(minigame, 
								MinigameUtils.formStr("minigame.timeLeft", MinigameUtils.convertTime(minigame.getTimer())), null, null);
					}
					
					Bukkit.getScheduler().cancelTask(taskID);
				}
				
				if(!paused){
					if(playerWaitTime != 0)
						playerWaitTime -= 1;
					else
						startWaitTime -= 1;
				}
			}
		}, 0, 20);
	}
	
	private void sendPlayersMessage(String message){
		for(MinigamePlayer ply : minigame.getPlayers()){
			ply.sendMessage(message);
		}
	}
	
	private void reclearInventories(Minigame minigame){
		for(MinigamePlayer ply : minigame.getPlayers()){
			ply.getPlayer().getInventory().clear();
		}
	}
	
	private void freezePlayers(boolean freeze){
		for(MinigamePlayer ply : minigame.getPlayers()){
			ply.setFrozen(freeze);
		}
	}
	
	private void allowInteraction(boolean allow){
		for(MinigamePlayer ply : minigame.getPlayers()){
			ply.setCanInteract(allow);
		}
	}
	
	public int getPlayerWaitTimeLeft(){
		return playerWaitTime;
	}
	
	public int getStartWaitTimeLeft(){
		return startWaitTime;
	}
	
	public void setPlayerWaitTime(int time){
		playerWaitTime = time;
	}
	
	public void setStartWaitTime(int time){
		startWaitTime = time;
	}
	
	public void pauseTimer(){
		paused = true;
		for(MinigamePlayer ply : minigame.getPlayers()){
			ply.sendMessage(MinigameUtils.getLang("time.startup.timerPaused"), null);
		}
	}
	
	public void pauseTimer(String reason){
		paused = true;
		for(MinigamePlayer ply : minigame.getPlayers()){
			ply.sendMessage(MinigameUtils.formStr("time.startup.timerPaused", reason), null);
		}
	}
	
	public void removeTimer(){
		if(taskID != -1){
			Bukkit.getScheduler().cancelTask(taskID);
		}
	}
	
	public void resumeTimer(){
		paused = false;
		for(MinigamePlayer ply : minigame.getPlayers()){
			ply.sendMessage(MinigameUtils.getLang("time.startup.timerResumed"), null);
		}
	}
	
	public boolean isPaused(){
		return paused;
	}
}
