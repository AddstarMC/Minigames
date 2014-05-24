package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.modules.LobbySettingsModule;

public class MultiplayerTimer{
	private int playerWaitTime;
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
		playerWaitTime = plugin.getConfig().getInt("multiplayer.waitforplayers");
		if(playerWaitTime <= 0)
			playerWaitTime = 10;
		if(minigame.getStartWaitTime() == 0	){
			startWaitTime = plugin.getConfig().getInt("multiplayer.startcountdown");
			if(startWaitTime <= 0)
				startWaitTime = 5;
		}
		else
			startWaitTime = minigame.getStartWaitTime();
		oStartWaitTime = startWaitTime;
		timeMsg.addAll(plugin.getConfig().getIntegerList("multiplayer.timerMessageInterval"));
	}
	
	public void startTimer(){
		playerWaitTime += 1;
//		startWaitTime += 1;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if(!paused){
					if(playerWaitTime != 0)
						playerWaitTime -= 1;
					else
						startWaitTime -= 1;
				}
				
				if(playerWaitTime != 0 && !paused){
					if(playerWaitTime == plugin.getConfig().getInt("multiplayer.waitforplayers")){
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.getLang("time.startup.waitingForPlayers"));
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.formStr("time.startup.time", playerWaitTime));
					}
					else if(timeMsg.contains(playerWaitTime)){
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.formStr("time.startup.time", playerWaitTime));
					}
				}
				else if(playerWaitTime == 0 && startWaitTime != 0 && !paused){
					if(startWaitTime == oStartWaitTime){
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.getLang("time.startup.minigameStarts"));
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.formStr("time.startup.time", startWaitTime));
						freezePlayers(!LobbySettingsModule.getMinigameModule(minigame).canMoveStartWait());
						allowInteraction(LobbySettingsModule.getMinigameModule(minigame).canInteractStartWait());
						if(LobbySettingsModule.getMinigameModule(minigame).isTeleportOnPlayerWait()){
							reclearInventories(minigame);
							pdata.startMPMinigame(minigame, true);
						}
					}
					else if(timeMsg.contains(startWaitTime)){
						sendPlayersMessage(ChatColor.GRAY + MinigameUtils.formStr("time.startup.time", startWaitTime));
					}
				}
				else if(playerWaitTime == 0 && startWaitTime == 0){
					sendPlayersMessage(ChatColor.GREEN + MinigameUtils.getLang("time.startup.go"));
					reclearInventories(minigame);
					if(LobbySettingsModule.getMinigameModule(minigame).isTeleportOnStart())
						pdata.startMPMinigame(minigame, true);
					else
						pdata.startMPMinigame(minigame, false);
					freezePlayers(false);
					allowInteraction(true);
					
					if(minigame.getFloorDegen1() != null && minigame.getFloorDegen2() != null){
						minigame.addFloorDegenerator();
						minigame.getFloorDegenerator().startDegeneration();
					}
			
					if(minigame.hasRestoreBlocks()){
						for(RestoreBlock block : minigame.getRestoreBlocks().values()){
							minigame.getBlockRecorder().addBlock(block.getLocation().getBlock(), null);
						}
					}
					
					if(minigame.getTimer() > 0){
						minigame.setMinigameTimer(new MinigameTimer(minigame, minigame.getTimer()));
						plugin.mdata.sendMinigameMessage(minigame, 
								MinigameUtils.formStr("minigame.timeLeft", MinigameUtils.convertTime(minigame.getTimer())), null, null);
					}
					
					Bukkit.getScheduler().cancelTask(taskID);
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
