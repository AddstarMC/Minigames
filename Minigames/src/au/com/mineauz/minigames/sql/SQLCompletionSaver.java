package au.com.mineauz.minigames.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.PlayerData;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardPlayer;
import au.com.mineauz.minigames.minigame.reward.RewardsModule;

public class SQLCompletionSaver extends Thread{
	private Minigames plugin = Minigames.plugin;
	private PlayerData pdata = Minigames.plugin.pdata;
	
	public SQLCompletionSaver(){
		start();
	}
	
	public void run(){
		SQLDatabase sql = plugin.getSQL();
		
		while(plugin.hasSQLToStore()){
			List<SQLPlayer> players = plugin.getSQLToStore();
			plugin.clearSQLToStore();
			for(SQLPlayer player : players){
				addSQLData(player, sql);
			}
		}

		plugin.removeSQLCompletionSaver();
	}
	
	private void addSQLData(SQLPlayer player, SQLDatabase database){
		
		String table = "mgm_" + player.getMinigame() + "_comp";
		
		if(!database.isOpen())
		{
			if(!database.loadSQL())
			{
				plugin.getLogger().warning("Database Connection was closed and could not be re-established!");
				return;
			}
		}
		
		Connection sql = database.getSql();
		
		if(!database.isTable(table)){
			try {
				Statement createTable = sql.createStatement();
				createTable.execute("CREATE TABLE " + table + 
						"( " +
						"UUID varchar(40) NOT NULL PRIMARY KEY, " +
						"Player varchar(32), " +
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
				createTable.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try
		{
			ResultSet set = null;
			Statement getStats = sql.createStatement();
			set = getStats.executeQuery(String.format("SELECT * FROM %s WHERE UUID='%s'", table, player.getUUID()));
			
			String name = player.getPlayerName();
			String uuid = player.getUUID();
			int kills = player.getKills();
			int deaths = player.getDeaths();
			int score = player.getScore();
			int reverts = player.getReverts();
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
			boolean fetchedResults = false;
			try {
				set.absolute(1);
				ocompleted = set.getInt(3);
				
				okills = set.getInt(4);
				odeaths = set.getInt(5);
				oscore = set.getInt(6);
				otime = set.getLong(7);
				oreverts = set.getInt(8);
				otkills = set.getInt(9);
				otdeaths = set.getInt(10);
				otscore = set.getInt(11);
				otreverts = set.getInt(12);
				ottime = set.getLong(13);
				ofailures = set.getInt(14);
				fetchedResults = true;
			} catch (SQLException e) {
				//e.printStackTrace();
			}
			
			set.close();
			getStats.close();
			
			otkills += kills;
			otdeaths += deaths;
			otscore += score;
			otreverts += reverts;
			ottime += time;
			
			if(completed){
				ocompleted += completedNum;
				
				if(odeaths > deaths || odeaths == -1){
					odeaths = deaths;
				}
				
				if(oreverts > reverts || oreverts == -1){
					oreverts = reverts;
				}
				
				if(otime > time || otime == 0){
					otime = time;
				}
			}
			else{
				ofailures += failureNum;
			}
			
			if(okills < kills){
				okills = kills;
			}
			
			if(oscore < score){
				oscore = score;
			}
			
			boolean hasAlreadyCompleted = false;
			
			if(fetchedResults){
				if(ocompleted - 1 >= 1)
					hasAlreadyCompleted = true;
				Statement updateStats = sql.createStatement();
				updateStats.executeUpdate("UPDATE " + table + " SET Completion='" + ocompleted + "', " +
						"Player='" + name + "', " + 
						"Kills=" + okills + ", " +
						"Deaths=" + odeaths + ", " +
						"Score=" + oscore + ", " +
						"Time=" + otime + ", " +
						"Reverts=" + oreverts + ", " +
						"TotalKills=" + otkills + ", " +
						"TotalDeaths=" + otdeaths + ", " +
						"TotalScore=" + otscore + ", " +
						"TotalReverts=" + otreverts + ", " +
						"TotalTime=" + ottime + ", " +
						"Failures=" + ofailures +
						" WHERE UUID='" + uuid + "'");
				updateStats.close();
			}
			else{
				name = player.getPlayerName();
				Statement insertStats = sql.createStatement();
				insertStats.executeUpdate("INSERT INTO " + table + " VALUES " +
						"( '" + uuid + "', '" + 
						name + "', " +
						ocompleted + ", " + 
						okills + ", " + 
						odeaths + ", " +
						oscore + ", " +
						otime + ", " +
						oreverts + ", " +
						otkills + ", " +
						otdeaths + ", " +
						otscore + ", " +
						otreverts + ", " +
						ottime + ", " +
						ofailures +
						" )");
				insertStats.close();
			}
		
			Minigame mgm = Minigames.plugin.mdata.getMinigame(player.getMinigame());
			if(mgm.getScoreboardData().hasPlayer(uuid)){
				ScoreboardPlayer ply = mgm.getScoreboardData().getPlayer(uuid);
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
				String pname = new String(player.getPlayerName());
				mgm.getScoreboardData().addPlayer(new 
						ScoreboardPlayer(pname, uuid, ocompleted, ofailures, okills, odeaths, 
								oscore, otime, oreverts, otkills, otdeaths, otscore, otreverts, ottime));
			}
			mgm.getScoreboardData().updateDisplays();
			
			if(completed){
				MinigameUtils.debugMessage("SQL Saver giving rewards to " + player.getPlayerName());
				RewardsModule.getModule(mgm).awardPlayer(pdata.getMinigamePlayer(player.getPlayerName()), mgm, !hasAlreadyCompleted);
			}
			
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
}
