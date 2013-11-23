package com.pauldavdesign.mineauz.minigames.sql;

import com.pauldavdesign.mineauz.minigames.Minigames;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;

public class SQLDatabase {
	private Database sql = null;
	private static Minigames plugin = Minigames.plugin;
	
	public void loadSQL(){
		if(plugin.getServer().getPluginManager().getPlugin("SQLibrary") != null){
			if(plugin.getConfig().getBoolean("use-sql")){
				setSql(new MySQL(plugin.getLogger(), 
						"[Minigames] ", 
						plugin.getConfig().getString("sql-host"), 
						plugin.getConfig().getInt("sql-port"), 
						plugin.getConfig().getString("sql-database"), 
						plugin.getConfig().getString("sql-username"), 
						plugin.getConfig().getString("sql-password")));
			}
		}
		else{
			plugin.getLogger().info("SQLibrary not found! You cannot use SQL to save data!");
		}
	}

	public Database getSql() {
		return sql;
	}

	public void setSql(Database sql) {
		this.sql = sql;
	}
}
