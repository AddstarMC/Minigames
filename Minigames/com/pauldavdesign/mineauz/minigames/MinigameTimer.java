package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.pauldavdesign.mineauz.minigames.events.MinigameTimerTickEvent;
import com.pauldavdesign.mineauz.minigames.events.TimerExpireEvent;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class MinigameTimer{
	private int time = 0;
	private int otime = 0;
	private Minigame minigame;
	private List<Integer> timeMsg = new ArrayList<Integer>();
	private static Minigames plugin = Minigames.plugin;
	private int taskID = -1;
	
	public MinigameTimer(Minigame minigame, int time){
		this.time = time;
		otime = time;
		this.minigame = minigame;
		timeMsg.addAll(plugin.getConfig().getIntegerList("multiplayer.timerMessageInterval"));
		startTimer();
	}
	
	public void startTimer(){
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				time -= 1;
				if(minigame.isUsingXPBarTimer()){
					float timeper = ((Integer)time).floatValue() / ((Integer)otime).floatValue();
					int level = 0;
					if(time / 60 > 0)
						level = time / 60;
					else
						level = time;
					
					for(MinigamePlayer ply : minigame.getPlayers()){
						ply.getPlayer().setExp(timeper);
						ply.getPlayer().setLevel(level);
					}
				}
				if(timeMsg.contains(time)){
					plugin.mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("minigame.timeLeft", MinigameUtils.convertTime(time)), null, null);
				}
				else if(time == 0){
					Bukkit.getServer().getPluginManager().callEvent(new TimerExpireEvent(minigame));
					stopTimer();
				}
				Bukkit.getPluginManager().callEvent(new MinigameTimerTickEvent(minigame, minigame.getMinigameTimer()));
			}
		}, 0, 20);
	}
	
	public void stopTimer(){
		if(taskID != -1){
			Bukkit.getScheduler().cancelTask(taskID);
		}
	}
	
	public int getTimeLeft(){
		return time;
	}
	
	public void setTimeLeft(int time){
		this.time = time;
	}
}
