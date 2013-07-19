package com.pauldavdesign.mineauz.minigames;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lib.PatPeter.SQLibrary.Database;

import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;

public class SQLCompletionSaver extends Thread{
	private boolean hascompleted = false;
	private String minigame = null;
	private MinigamePlayer player = null;
	private List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
	public MinigameType mgtype = null;
	public PlayerData pdata = Minigames.plugin.pdata;
	
	public SQLCompletionSaver(String minigame, MinigamePlayer player, MinigameType mgtype){
		this.minigame = minigame;
		this.player = player;
		this.mgtype = mgtype;
		this.start();
	}
	
	public SQLCompletionSaver(String minigame, List<MinigamePlayer> players, MinigameType mgtype){
		this.minigame = minigame;
		this.players = players;
		this.mgtype = mgtype;
		this.start();
	}
	
	public void run(){
		Database sql = Minigames.plugin.getSQL().getSql();
		if(!sql.isOpen()){
		    sql.open();
		}
		if(sql.isOpen()){
			if(!sql.isTable("mgm_" + minigame + "_comp")){
				try {
					sql.query("CREATE TABLE mgm_" + minigame + "_comp " +
							"( " +
							"Player varchar(32) NOT NULL PRIMARY KEY, " +
							"Completion int, " +
							"Kills int, " +
							"Deaths int " +
							")");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(player != null){
				ResultSet set = null;
				try {
					set = sql.query("SELECT * FROM mgm_" + minigame + "_comp WHERE Player='" + player.getName() + "'");
				} catch (SQLException e1) {
					e1.printStackTrace();
					return;
				}
				
				try {
					set = sql.query("SELECT * FROM mgm_" + minigame + "_comp WHERE Player='" + player.getName() + "'");
				} catch (SQLException e1) {
					e1.printStackTrace();
					return;
				}
				
				String name = null;
				int completed = 0;
				int kills = player.getKills();
				int deaths = player.getDeaths();
				player.resetKills();
				player.resetDeaths();
				player.resetScore();
				
				int okills = 0;
				int odeaths = -1;
				try {
					set.absolute(1);
					name = set.getString(1);
					completed = set.getInt(2);
					okills = set.getInt(3);
					odeaths = set.getInt(4);
				} catch (SQLException e) {
					//e.printStackTrace();
				}
	
				completed++;
				if(okills > kills){
					kills = okills;
				}
				
				if(odeaths < deaths && odeaths != -1){
					deaths = odeaths;
				}
				
				if(name != null){
					hascompleted = true;
					try {
						sql.query("UPDATE mgm_" + minigame + "_comp SET Completion='" + completed + "', Kills=" + kills + ", Deaths=" + deaths + " WHERE Player='" + name + "'");
					} catch (SQLException e) {
						e.printStackTrace();
						return;
					}
				}
				else{
					name = player.getName();
					try {
						sql.query("INSERT INTO mgm_" + minigame + "_comp VALUES ( '" + name + "', " + completed + ", " + kills + ", " + deaths + " )");
					} catch (SQLException e) {
						e.printStackTrace();
						return;
					}
				}
				
				mgtype.issuePlayerRewards(player, Minigames.plugin.mdata.getMinigame(minigame), hascompleted);
			}
			else{
				for(MinigamePlayer player : players){
					ResultSet set = null;
					try {
						set = sql.query("SELECT * FROM mgm_" + minigame + "_comp WHERE Player='" + player.getName() + "'");
					} catch (SQLException e1) {
						e1.printStackTrace();
						return;
					}
					
					String name = null;
					int completed = 0;
					int kills = player.getKills();
					int deaths = player.getDeaths();
					player.resetKills();
					player.resetDeaths();
					player.resetScore();
					
					int okills = 0;
					int odeaths = -1;
					try {
						set.absolute(1);
						name = set.getString(1);
						completed = set.getInt(2);
						okills = set.getInt(3);
						odeaths = set.getInt(4);
					} catch (SQLException e) {
						//e.printStackTrace();
					}
		
					completed++;
					if(okills > kills){
						kills = okills;
					}
					
					if(odeaths < deaths && odeaths != -1){
						deaths = odeaths;
					}
					
					if(name != null){
						hascompleted = true;
						try {
							sql.query("UPDATE mgm_" + minigame + "_comp SET Completion='" + completed + "', Kills=" + kills + ", Deaths=" + deaths + " WHERE Player='" + name + "'");
						} catch (SQLException e) {
							e.printStackTrace();
							return;
						}
					}
					else{
						name = player.getName();
						try {
							sql.query("INSERT INTO mgm_" + minigame + "_comp VALUES ( '" + name + "', " + completed + ", " + kills + ", " + deaths + " )");
						} catch (SQLException e) {
							e.printStackTrace();
							return;
						}
					}
					
					mgtype.issuePlayerRewards(player, Minigames.plugin.mdata.getMinigame(minigame), hascompleted);
				}
			}
		}
		sql.close();
	}
}
