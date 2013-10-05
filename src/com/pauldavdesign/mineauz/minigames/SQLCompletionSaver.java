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
							"Deaths int, " +
							"Score int, " +
							"Time long, " +
							"Reverts int, " +
							"TotalKills int, " +
							"TotalDeaths int, " +
							"TotalScore int, " +
							"TotalReverts int, " +
							"TotalTime long " +
							")");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else{
				try {
					sql.query("SELECT Score FROM mgm_" + minigame + "_comp");
				} catch (SQLException e) {
					try {
						sql.query("ALTER TABLE mgm_" + minigame + "_comp ADD Score int DEFAULT -1, " +
								"ADD Time long NOT NULL, " +
								"ADD Reverts int DEFAULT -1, " +
								"ADD TotalKills int DEFAULT 0, " +
								"ADD TotalDeaths int DEFAULT 0, " +
								"ADD TotalScore int DEFAULT 0, " +
								"ADD TotalReverts int DEFAULT 0, " +
								"ADD TotalTime long NOT NULL");
					} catch (SQLException e1) {
						e1.printStackTrace();
						return;
					}
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
				
				String name = null;
				int completed = 0;
				int kills = player.getKills();
				int deaths = player.getDeaths();
				int score = player.getScore();
				int reverts = player.getReverts();
				long time = player.getEndTime() - player.getStartTime() + player.getStoredTime();
				player.resetKills();
				player.resetDeaths();
				player.resetScore();
				player.resetReverts();
				player.resetTime();
				
				int okills = 0;
				int odeaths = -1;
				int oscore = 0;
				int oreverts = -1;
				long otime = 0;
				int otkills = 0;
				int otdeaths = 0;
				int otscore = 0;
				int otreverts = 0;
				long ottime = 0;
				try {
					set.absolute(1);
					name = set.getString(1);
					completed = set.getInt(2);
					
					okills = set.getInt(3);
					odeaths = set.getInt(4);
					oscore = set.getInt(5);
					otime = set.getLong(6);
					oreverts = set.getInt(7);
					otkills = set.getInt(8);
					otdeaths = set.getInt(9);
					otscore = set.getInt(10);
					otreverts = set.getInt(11);
					ottime = set.getLong(12);
				} catch (SQLException e) {
					//e.printStackTrace();
				}
	
				completed++;
				otkills += kills;
				otdeaths += deaths;
				otscore += score;
				otreverts += reverts;
				ottime += time;
				
				if(okills > kills){
					kills = okills;
				}
				
				if(odeaths < deaths && odeaths != -1){
					deaths = odeaths;
				}
				
				if(oscore > score){
					score = oscore;
				}
				
				if(oreverts < reverts && oreverts != -1){
					reverts = oreverts;
				}
				
				if(otime < time && otime != 0){
					time = otime;
				}
				
				if(name != null){
					hascompleted = true;
					try {
						sql.query("UPDATE mgm_" + minigame + "_comp SET Completion='" + completed + "', " +
								"Kills=" + kills + ", " +
								"Deaths=" + deaths + ", " +
								"Score=" + score + ", " +
								"Time=" + time + ", " +
								"Reverts=" + reverts + ", " +
								"TotalKills=" + otkills + ", " +
								"TotalDeaths=" + otdeaths + ", " +
								"TotalScore=" + otscore + ", " +
								"TotalReverts=" + otreverts + ", " +
								"TotalTime=" + ottime +
								" WHERE Player='" + name + "'");
					} catch (SQLException e) {
						e.printStackTrace();
						return;
					}
				}
				else{
					name = player.getName();
					try {
						sql.query("INSERT INTO mgm_" + minigame + "_comp VALUES " +
								"( '" + name + "', " + 
								completed + ", " + 
								kills + ", " + 
								deaths + ", " +
								score + ", " +
								time + ", " +
								reverts + ", " +
								otkills + ", " +
								otdeaths + ", " +
								otscore + ", " +
								otreverts + ", " +
								ottime + 
								" )");
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
					int score = player.getScore();
					int reverts = player.getReverts();
					long time = player.getEndTime() - player.getStartTime() + player.getStoredTime();
					player.resetKills();
					player.resetDeaths();
					player.resetScore();
					player.resetReverts();
					player.resetTime();
					
					int okills = 0;
					int odeaths = -1;
					int oscore = 0;
					int oreverts = -1;
					long otime = 0;
					int otkills = 0;
					int otdeaths = 0;
					int otscore = 0;
					int otreverts = 0;
					long ottime = 0;
					try {
						set.absolute(1);
						name = set.getString(1);
						completed = set.getInt(2);
						
						okills = set.getInt(3);
						odeaths = set.getInt(4);
						oscore = set.getInt(5);
						otime = set.getLong(6);
						oreverts = set.getInt(7);
						otkills = set.getInt(8);
						otdeaths = set.getInt(9);
						otscore = set.getInt(10);
						otreverts = set.getInt(11);
						ottime = set.getLong(12);
					} catch (SQLException e) {
						//e.printStackTrace();
					}
		
					completed++;
					
					otkills += kills;
					otdeaths += deaths;
					otscore += score;
					otreverts += reverts;
					ottime += time;
					
					if(okills > kills){
						kills = okills;
					}
					
					if(odeaths < deaths && odeaths != -1){
						deaths = odeaths;
					}
					
					if(oscore > score){
						score = oscore;
					}
					
					if(oreverts < reverts && oreverts != -1){
						reverts = oreverts;
					}
					
					if(otime < time && otime != 0){
						time = otime;
					}
					
					if(name != null){
						hascompleted = true;
						try {
							sql.query("UPDATE mgm_" + minigame + "_comp SET Completion='" + completed + "', " +
									"Kills=" + kills + ", " +
									"Deaths=" + deaths + ", " +
									"Score=" + score + ", " +
									"Time=" + time + ", " +
									"Reverts=" + reverts + ", " +
									"TotalKills=" + otkills + ", " +
									"TotalDeaths=" + otdeaths + ", " +
									"TotalScore=" + otscore + ", " +
									"TotalReverts=" + otreverts + ", " +
									"TotalTime=" + ottime +
									" WHERE Player='" + name + "'");
						} catch (SQLException e) {
							e.printStackTrace();
							return;
						}
					}
					else{
						name = player.getName();
						try {
							sql.query("INSERT INTO mgm_" + minigame + "_comp VALUES " +
									"( '" + name + "', " + 
									completed + ", " + 
									kills + ", " + 
									deaths + ", " +
									score + ", " +
									time + ", " +
									reverts + ", " +
									otkills + ", " +
									otdeaths + ", " +
									otscore + ", " +
									otreverts + ", " +
									ottime + 
									" )");
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
