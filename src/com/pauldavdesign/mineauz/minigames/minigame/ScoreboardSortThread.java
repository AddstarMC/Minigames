package com.pauldavdesign.mineauz.minigames.minigame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;

public class ScoreboardSortThread extends Thread{
	
	private List<ScoreboardPlayer> players;
	private String minigame;
	private List<ScoreboardPlayer> result = new ArrayList<ScoreboardPlayer>();
	private ScoreboardType type;
	private ScoreboardOrder order;
	private CommandSender requested = null;
	private ScoreboardDisplay display = null;
	private int limit = 8;
	private String specificPlayer = null;
	
	public ScoreboardSortThread(List<ScoreboardPlayer> players, ScoreboardType type, 
			ScoreboardOrder order, String minigame, CommandSender requested){
		this.players = players;
		this.type = type;
		this.order = order;
		this.requested = requested;
		this.minigame = minigame;
	}
	
	public ScoreboardSortThread(List<ScoreboardPlayer> players, ScoreboardType type, 
			ScoreboardOrder order, String minigame, CommandSender requested, int resultLimit){
		this.players = players;
		this.type = type;
		this.order = order;
		this.requested = requested;
		this.minigame = minigame;
		limit = resultLimit;
	}
	
	public ScoreboardSortThread(List<ScoreboardPlayer> players, ScoreboardType type, 
			ScoreboardOrder order, ScoreboardDisplay display){
		this.players = players;
		this.type = type;
		this.order = order;
		this.display = display;
	}
	
	public void setSpecificPlayer(String player){
		specificPlayer = player;
	}
	
	public void run(){
		for(ScoreboardPlayer ply : players){
			if(result.isEmpty())
				result.add(ply);
			else{
				List<ScoreboardPlayer> resultCopy = new ArrayList<ScoreboardPlayer>(result);
				boolean added = false;
				for(ScoreboardPlayer ply2 : resultCopy){
					if(type == ScoreboardType.LEAST_TIME || type == ScoreboardType.TOTAL_TIME){
						long plyTime = (Long) ply.getByType(type);
						long ply2Time = (Long) ply2.getByType(type);
						if(type == ScoreboardType.LEAST_TIME && (plyTime <= 0)){added = true; break;}
						if(order == ScoreboardOrder.DESCENDING){
							if(plyTime > ply2Time){
								result.add(resultCopy.indexOf(ply2), ply);
								added = true;
								break;
							}
						}
						else{
							if(plyTime < ply2Time){
								result.add(resultCopy.indexOf(ply2), ply);
								added = true;
								break;
							}
						}
					}
					else{
						int val = (Integer) ply.getByType(type);
						int val2 = (Integer) ply2.getByType(type);
						if(((type == ScoreboardType.LEAST_DEATHS || type == ScoreboardType.LEAST_REVERTS) && val <= -1) || 
								(type != ScoreboardType.LEAST_DEATHS && type != ScoreboardType.LEAST_REVERTS && type != ScoreboardType.FAILURES && val == 0)){added = true; break;}
						if(order == ScoreboardOrder.DESCENDING){
							if(val > val2){
								result.add(resultCopy.indexOf(ply2), ply);
								added = true;
								break;
							}
						}
						else{
							if(val < val2){
								result.add(resultCopy.indexOf(ply2), ply);
								added = true;
								break;
							}
						}
					}
				}
				if(!added)
					result.add(ply);
			}
		}
		
		if(requested != null && ((requested instanceof Player && ((Player)requested).isOnline()) || !(requested instanceof Player))){
			if(specificPlayer == null){
				requested.sendMessage(ChatColor.GREEN + minigame + " Scoreboard: " + type.toString().toLowerCase().replace("_", " ") + " " + order.toString().toLowerCase());
				for(int i = 0; i < limit; i++){
					if(i >= result.size()) break;
					String msg = ChatColor.AQUA + result.get(i).getPlayerName() + ": " + ChatColor.WHITE;
					if(type == ScoreboardType.LEAST_TIME || type == ScoreboardType.TOTAL_TIME){
						int time = (int)((Long)result.get(i).getByType(type) / 1000);
						msg += MinigameUtils.convertTime(time, true);
					}
					else{ 
						msg += (Integer)result.get(i).getByType(type);
					}
					requested.sendMessage(msg);
				}
			}
			else{
				requested.sendMessage(ChatColor.GREEN + minigame + " Scoreboard for " + specificPlayer + ": " + type.toString().toLowerCase().replace("_", " ") + " " + order.toString().toLowerCase());
				int c = -1;
				for(ScoreboardPlayer pl : result){
					if(pl.getPlayerName().equals(specificPlayer)){
						c = result.indexOf(pl);
						break;
					}
				}
				if(c != -1){
					ScoreboardPlayer pl = result.get(c);
					if(type == ScoreboardType.LEAST_TIME || type == ScoreboardType.TOTAL_TIME){
						int time = (int)((Long)pl.getByType(type) / 1000);
						requested.sendMessage(ChatColor.AQUA + "Time: " + ChatColor.WHITE + MinigameUtils.convertTime(time, true));
						requested.sendMessage(ChatColor.AQUA + "Place: " + ChatColor.WHITE + (c + 1));
					}
					else{ 
						requested.sendMessage(ChatColor.AQUA + type.getTypeName() + ": " + ChatColor.WHITE + ((Integer)pl.getByType(type)).toString());
						requested.sendMessage(ChatColor.AQUA + "Place: " + ChatColor.WHITE + (c + 1));
					}
				}
				else{
					requested.sendMessage(ChatColor.RED + specificPlayer + " is currently not in the leaderboards for " + type.getTypeName());
				}
			}
		}
		else if(display != null){
			display.displayStats(result);
		}
	}
}
