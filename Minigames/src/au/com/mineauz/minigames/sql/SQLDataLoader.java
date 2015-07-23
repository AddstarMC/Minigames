package au.com.mineauz.minigames.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardPlayer;

// TODO: This class has to be updated to new stat format
// also, it needs to be made more efficient grabbing only the data it needs
public class SQLDataLoader extends Thread{
	
	private List<Minigame> mgs;
	
	public SQLDataLoader(Collection<Minigame> minigames){
		mgs = new ArrayList<Minigame>(minigames);
	}
	
	public void run(){
		SQLDatabase database = Minigames.plugin.getSQL();
		
		if(!database.isOpen())
		{
			if(!database.loadSQL())
			{
				Minigames.plugin.getLogger().warning("Database Connection was closed and could not be re-established!");
				return;
			}
		}
		
		Connection sql = database.getSql();

		for(Minigame mg : mgs){
			String minigame = mg.getName(false);
			String table = "mgm_" + minigame + "_comp";
			if(database.isTable(table)){
				try{
					if(!database.columnExists("Score", table))
					{
						Statement alterTable = sql.createStatement();
						alterTable.execute("ALTER TABLE " + table + " ADD Score int DEFAULT -1, " +
							"ADD Time long NOT NULL, " +
							"ADD Reverts int DEFAULT -1, " +
							"ADD TotalKills int DEFAULT 0, " +
							"ADD TotalDeaths int DEFAULT 0, " +
							"ADD TotalScore int DEFAULT 0, " +
							"ADD TotalReverts int DEFAULT 0, " +
							"ADD TotalTime long NOT NULL, " + 
							"ADD Failures int DEFAULT 0");
					}
					
					Statement getAllStats = sql.createStatement();
					ResultSet set = getAllStats.executeQuery("SELECT * FROM " + table);
					
					while(set.next()){
						String uuid = set.getString(1);
						String name = set.getString(2);
						int completed = set.getInt(3);
						
						int kills = set.getInt(4);
						int deaths = set.getInt(5);
						int score = set.getInt(6);
						long time = set.getLong(7);
						int reverts = set.getInt(8);
						int tkills = set.getInt(9);
						int tdeaths = set.getInt(10);
						int tscore = set.getInt(11);
						int treverts = set.getInt(12);
						long ttime = set.getLong(13);
						int failures = set.getInt(14);
						
						mg.getScoreboardData().addPlayer(new 
								ScoreboardPlayer(name, uuid, completed, failures, kills, deaths, score, time, 
										reverts, tkills, tdeaths, tscore, treverts, ttime));
					}
					
					set.close();
					getAllStats.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
