package com.pauldavdesign.mineauz.minigames.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lib.PatPeter.SQLibrary.Database;

import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameTypeBase;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.ScoreboardPlayer;

public class SQLCompletionSaver extends Thread{
	private Minigames plugin = Minigames.plugin;
	private PlayerData pdata = Minigames.plugin.pdata;
	
	public SQLCompletionSaver(){
		start();
	}
	
	public void run(){
		Database sql = plugin.getSQL().getSql();
		if(!sql.isOpen()){
		    sql.open();
		}
		if(sql.isOpen()){
			while(plugin.hasSQLToStore()){
				List<SQLPlayer> players = plugin.getSQLToStore();
				plugin.clearSQLToStore();
				for(SQLPlayer player : players){
					addSQLData(player, sql);
				}
			}
		}
		plugin.removeSQLCompletionSaver();
		sql.close();
	}
	
	private void addSQLData(SQLPlayer player, Database sql){
		if(!sql.isTable("mgm_" + player.getMinigame() + "_comp")){
			try {
				sql.query("CREATE TABLE mgm_" + player.getMinigame() + "_comp " +
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
						"TotalTime long, " +
						"Failures int " +
						")");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else{ //TODO: Remove after 1.6.0 release
			try {
				sql.query("SELECT Score FROM mgm_" + player.getMinigame() + "_comp");
				try { //Remove before 1.6.0 release
					sql.query("SELECT Failures FROM mgm_" + player.getMinigame() + "_comp");
				}
				catch (SQLException e){
					sql.query("ALTER TABLE mgm_" + player.getMinigame() + "_comp ADD Failures int DEFAULT 0");
				}
			} catch (SQLException e) {
				try {
					sql.query("ALTER TABLE mgm_" + player.getMinigame() + "_comp ADD Score int DEFAULT -1, " +
							"ADD Time long NOT NULL, " +
							"ADD Reverts int DEFAULT -1, " +
							"ADD TotalKills int DEFAULT 0, " +
							"ADD TotalDeaths int DEFAULT 0, " +
							"ADD TotalScore int DEFAULT 0, " +
							"ADD TotalReverts int DEFAULT 0, " +
							"ADD TotalTime long NOT NULL, " + 
							"ADD Failures int DEFAULT 0");
				} catch (SQLException e1) {
					e1.printStackTrace();
					return;
				}
			}
		}
		
		ResultSet set = null;
		try {
			set = sql.query("SELECT * FROM mgm_" + player.getMinigame() + "_comp WHERE Player='" + player.getPlayerName() + "'");
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
		
		String name = null;
		int kills = player.getKills();
		int deaths = player.getDeaths();
		int score = player.getScore();
		int reverts = player.getReverts();
		//long time = player.getEndTime() - player.getStartTime() + player.getStoredTime();
		long time = player.getTime();
		int completedNum = player.getCompletionChange();
		int failureNum = player.getFailureChange();
		boolean completed = false;
		if(player.getCompletionChange() >= 1){
			completed = true;
		}

		int ocompleted = 0;
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
		int ofailures = 0;
		try {
			set.absolute(1);
			name = set.getString(1);
			ocompleted = set.getInt(2);
			
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
			ofailures = set.getInt(13);
		} catch (SQLException e) {
			//e.printStackTrace();
		}
		
		otkills += kills;
		otdeaths += deaths;
		otscore += score;
		otreverts += reverts;
		ottime += time;
		
		if(completed){
			ocompleted += completedNum;
			
			if(odeaths < deaths && odeaths != -1){
				deaths = odeaths;
			}
			
			if(oreverts < reverts && oreverts != -1){
				reverts = oreverts;
			}
			
			if(otime < time && otime != 0){
				time = otime;
			}
		}
		else{
			ofailures += failureNum;
			if(odeaths != -1)
				deaths = odeaths;
			else
				deaths = -1;
			if(oreverts != -1)
				reverts = oreverts;
			else
				reverts = -1;
			if(otime != 0)
				time = otime;
			else
				time = 0;
		}
		
		if(okills > kills){
			kills = okills;
		}
		
		if(oscore > score){
			score = oscore;
		}
		
		boolean hasAlreadyCompleted = false;
		
		if(name != null){
			hasAlreadyCompleted = true;
			try {
				sql.query("UPDATE mgm_" + player.getMinigame() + "_comp SET Completion='" + ocompleted + "', " +
						"Kills=" + kills + ", " +
						"Deaths=" + deaths + ", " +
						"Score=" + score + ", " +
						"Time=" + time + ", " +
						"Reverts=" + reverts + ", " +
						"TotalKills=" + otkills + ", " +
						"TotalDeaths=" + otdeaths + ", " +
						"TotalScore=" + otscore + ", " +
						"TotalReverts=" + otreverts + ", " +
						"TotalTime=" + ottime + ", " +
						"Failures=" + ofailures +
						" WHERE Player='" + name + "'");
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
		}
		else{
			name = player.getPlayerName();
			try {
				sql.query("INSERT INTO mgm_" + player.getMinigame() + "_comp VALUES " +
						"( '" + name + "', " + 
						ocompleted + ", " + 
						kills + ", " + 
						deaths + ", " +
						score + ", " +
						time + ", " +
						reverts + ", " +
						otkills + ", " +
						otdeaths + ", " +
						otscore + ", " +
						otreverts + ", " +
						ottime + ", " +
						ofailures +
						" )");
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
		}
		
		Minigame mgm = Minigames.plugin.mdata.getMinigame(player.getMinigame());
		if(mgm.getScoreboardData().hasPlayer(player.getPlayerName())){
			ScoreboardPlayer ply = mgm.getScoreboardData().getPlayer(player.getPlayerName());
			ply.setCompletions(ocompleted);
			ply.setBestKills(okills);
			ply.setLeastDeaths(odeaths);
			ply.setBestScore(oscore);
			ply.setBestTime(otime);
			ply.setLeastReverts(oreverts);
			ply.setTotalKills(otkills);
			ply.setTotalDeaths(otdeaths);
			ply.setTotalScore(otscore);
			ply.setTotalReverts(otreverts);
			ply.setTotalTime(ottime);
			ply.setFailures(ofailures);
		}
		else{
			mgm.getScoreboardData().addPlayer(new 
					ScoreboardPlayer(player.getPlayerName(), ocompleted, ofailures, otkills, odeaths, 
							oscore, otime, oreverts, otkills, otdeaths, otscore, otreverts, ottime));
		}
		mgm.getScoreboardData().updateDisplays();
		
		if(completed)
			MinigameTypeBase.issuePlayerRewards(pdata.getMinigamePlayer(player.getPlayerName()), mgm, hasAlreadyCompleted);
//			mgtype.issuePlayerRewards(pdata.getMinigamePlayer(player.getPlayerName()), Minigames.plugin.mdata.getMinigame(player.getMinigame()), hasAlreadyCompleted);
	}
}
