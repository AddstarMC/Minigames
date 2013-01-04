package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.events.TimerExpireEvent;

public class MinigameTimer extends Thread{
	private int time = 0;
	private Minigame minigame;
	private List<Integer> timeMsg = new ArrayList<Integer>();
	private static Minigames plugin = Minigames.plugin;
	private boolean running = true;
	
	public MinigameTimer(Minigame minigame, int time){
		this.time = time;
		this.minigame = minigame;
		timeMsg.addAll(plugin.getConfig().getIntegerList("multiplayer.timerMessageInterval"));
		start();
	}
	
	public void run(){
		while(time > 0 && running){
			time -= 1;
			if(timeMsg.contains(time)){
				for(Player pl : minigame.getPlayers()){
					pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.convertTime(time) + " left.");
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				time = 1;
				e.printStackTrace();
			}
		}
		if(running){
			Bukkit.getServer().getPluginManager().callEvent(new TimerExpireEvent(this, minigame));
		}
	}
	
	public void stopTimer(){
		running = false;
	}
}
