package com.pauldavdesign.mineauz.minigames;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import lib.PatPeter.SQLibrary.MySQL;

public class SQLCompletionSaver extends Thread{
	private boolean hascompleted = false;
	private String minigame = null;
	private Player player = null;
	private List<Player> players = new ArrayList<Player>();
	public MinigameType mgtype = null;
	public PlayerData pdata = Minigames.plugin.pdata;
	
	public SQLCompletionSaver(String minigame, Player player, MinigameType mgtype){
		this.minigame = minigame;
		this.player = player;
		this.mgtype = mgtype;
		this.start();
	}
	
	public SQLCompletionSaver(String minigame, List<Player> players, MinigameType mgtype){
		this.minigame = minigame;
		this.players = players;
		this.mgtype = mgtype;
		this.start();
	}
	
	public void run(){
		MySQL sql = Minigames.plugin.getSQL();
		sql.open();
		if(sql.checkConnection()){
			if(!sql.checkTable("mgm_" + minigame + "_comp")){
				sql.createTable("CREATE TABLE mgm_" + minigame + "_comp " +
						"( " +
						"Player varchar(32) NOT NULL PRIMARY KEY, " +
						"Completion int, " +
						"Kills int, " +
						"Deaths int " +
						")");
			}
			
			if(player != null){
				ResultSet set = sql.query("SELECT * FROM mgm_" + minigame + "_comp WHERE Player='" + player.getName() + "'");
	
				//TODO remove these checks later
				try{
					set.absolute(1);
					set.findColumn("Kills");
				}catch (SQLException e){
					sql.query("ALTER TABLE mgm_" + minigame + "_comp ADD Kills int DEFAULT 0");
					sql.query("ALTER TABLE mgm_" + minigame + "_comp ADD Deaths int DEFAULT -1");
				}
				
				set = sql.query("SELECT * FROM mgm_" + minigame + "_comp WHERE Player='" + player.getName() + "'");
				
				String name = null;
				int completed = 0;
				int kills = pdata.getPlayerKills(player);
				int deaths = pdata.getPlayerDeath(player);
				pdata.removePlayerDeath(player);
				pdata.removePlayerKills(player);
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
					sql.query("UPDATE mgm_" + minigame + "_comp SET Completion='" + completed + "', Kills=" + kills + ", Deaths=" + deaths + " WHERE Player='" + name + "'");
				}
				else{
					name = player.getName();
					sql.query("INSERT INTO mgm_" + minigame + "_comp VALUES ( '" + name + "', " + completed + ", " + kills + ", " + deaths + " )");
				}
				
				mgtype.issuePlayerRewards(player, Minigames.plugin.mdata.getMinigame(minigame), hascompleted);
			}
			else{
				for(Player player : players){
					ResultSet set = sql.query("SELECT * FROM mgm_" + minigame + "_comp WHERE Player='" + player.getName() + "'");
					
					String name = null;
					int completed = 0;
					int kills = pdata.getPlayerKills(player);
					int deaths = pdata.getPlayerDeath(player);
					pdata.removePlayerDeath(player);
					pdata.removePlayerKills(player);
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
						sql.query("UPDATE mgm_" + minigame + "_comp SET Completion='" + completed + "', Kills=" + kills + ", Deaths=" + deaths + " WHERE Player='" + name + "'");
					}
					else{
						name = player.getName();
						sql.query("INSERT INTO mgm_" + minigame + "_comp VALUES ( '" + name + "', " + completed + ", " + kills + ", " + deaths + " )");
					}
					
					mgtype.issuePlayerRewards(player, Minigames.plugin.mdata.getMinigame(minigame), hascompleted);
				}
			}
		}
		sql.close();
	}
}
