package com.pauldavdesign.mineauz.minigames;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lib.PatPeter.SQLibrary.Database;

import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.ScoreboardPlayer;

public class SQLDataLoader extends Thread{
	
	private List<Minigame> mgs;
	
	public SQLDataLoader(Collection<Minigame> minigames){
		mgs = new ArrayList<Minigame>(minigames);
	}
	
	public void run(){
		Database sql = Minigames.plugin.getSQL().getSql();
		if(!sql.isOpen()){
		    sql.open();
		}
		if(sql.isOpen()){
			for(Minigame mg : mgs){
				String minigame = mg.getName();
				if(sql.isTable("mgm_" + minigame + "_comp")){
					try {
						sql.query("SELECT Score FROM mgm_" + minigame + "_comp");
						try { //Remove before 1.6.0 release
							sql.query("SELECT Failures FROM mgm_" + minigame + "_comp");
						}
						catch (SQLException e){
							sql.query("ALTER TABLE mgm_" + minigame + "_comp ADD Failures int DEFAULT 0");
						}
					} catch (SQLException e) {
						try {
							sql.query("ALTER TABLE mgm_" + minigame + "_comp ADD Score int DEFAULT -1, " +
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
					
					ResultSet set = null;
					try {
						set = sql.query("SELECT * FROM mgm_" + minigame + "_comp");
					} catch (SQLException e1) {
						e1.printStackTrace();
						return;
					}
					
					try {
						while(set.next()){
							String name = set.getString(1);
							int completed = set.getInt(2);
							
							int kills = set.getInt(3);
							int deaths = set.getInt(4);
							int score = set.getInt(5);
							long time = set.getLong(6);
							int reverts = set.getInt(7);
							int tkills = set.getInt(8);
							int tdeaths = set.getInt(9);
							int tscore = set.getInt(10);
							int treverts = set.getInt(11);
							long ttime = set.getLong(12);
							int failures = set.getInt(13);
							
							mg.getScoreboardData().addPlayer(new 
									ScoreboardPlayer(name, completed, failures, kills, deaths, score, time, 
											reverts, tkills, tdeaths, tscore, treverts, ttime));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
