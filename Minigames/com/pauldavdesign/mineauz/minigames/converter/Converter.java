package com.pauldavdesign.mineauz.minigames.converter;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigameSave;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.sql.SQLDatabase;

public class Converter{
	
	private Minigames mg = Minigames.plugin;
	
	private boolean sqlfailed = false;
	
	public Converter(){
		if(!mg.getConfig().getBoolean("convert.completion") || 
				!mg.getConfig().getBoolean("convert.inventories") || 
				!mg.getConfig().getBoolean("convert.checkpoints") || 
				!mg.getConfig().getBoolean("convert.sql"))
			convert();
	}
	
	@SuppressWarnings("deprecation")
	private boolean convert(){
		MinigameSave completion = new MinigameSave("completion");
		FileConfiguration compcfg = completion.getConfig();
		Set<String> compList = compcfg.getKeys(false);
		
		//Convert Completion.yml
		
		if(!mg.getConfig().getBoolean("convert.completion")){
			List<String> list = new ArrayList<String>();
			mg.getLogger().info("Converting: completion.yml");
			for(String g : compList){
				mg.getLogger().info(g);
				for(String p : compcfg.getStringList(g)){
					if(mg.getServer().getOfflinePlayer(p) != null){
						if(mg.getServer().getOfflinePlayer(p).getUniqueId() != null){
							String id = mg.getServer().getOfflinePlayer(p).getUniqueId().toString().replace("-", "_");
							list.add(id);
						}
					}
				}
				compcfg.set(g, new ArrayList<String>(list));
				list.clear();
			}
			completion.saveConfig();
			
			mg.getConfig().set("convert.completion", true);
			mg.saveConfig();
		}
		
		//Convert Inventories
		if(!mg.getConfig().getBoolean("convert.inventories")){
			mg.getLogger().info("Converting: inventories");
			String path = mg.getDataFolder().getPath() + "/playerdata/inventories";
			File invfol = new File(path);
			
			for(File f : invfol.listFiles()){
				String name = f.getName().replace(".yml", "");
				if(mg.getServer().getOfflinePlayer(name) != null){
					if(mg.getServer().getOfflinePlayer(name).getUniqueId() != null){
						String id = mg.getServer().getOfflinePlayer(name).getUniqueId().toString();
						File n = new File(path + "/" + id + ".yml"); 
						f.renameTo(n);
					}
					else{
						f.delete();
					}
				}
				else{
					f.delete();
				}
			}
			
			mg.getConfig().set("convert.inventories", true);
			mg.saveConfig();
		}

		//Convert Checkpoints
		if(!mg.getConfig().getBoolean("convert.checkpoints")){
			mg.getLogger().info("Converting: checkpoints");
			String path = mg.getDataFolder().getPath() + "/playerdata/checkpoints";
			File invfol = new File(path);
			
			for(File f : invfol.listFiles()){
				String name = f.getName().replace(".yml", "");
				if(mg.getServer().getOfflinePlayer(name) != null){
					if(mg.getServer().getOfflinePlayer(name).getUniqueId() != null){
						String id = mg.getServer().getOfflinePlayer(name).getUniqueId().toString();
						File n = new File(path + "/" + id + ".yml"); 
						f.renameTo(n);
					}
					else{
						f.delete();
					}
				}
				else{
					f.delete();
				}
			}
			
			mg.getConfig().set("convert.checkpoints", true);
			mg.saveConfig();
		}
		
		//Convert SQL
		if(!mg.getConfig().getBoolean("convert.sql")){
			if(mg.getConfig().getBoolean("use-sql")){
				SQLDatabase db = new SQLDatabase();
				if(db.loadSQL()){
					for(String game : mg.getConfig().getStringList("minigames")){
						mg.getLogger().info("Converting DB of " + game + ":");
						sqlConverter(game, db);
					}
					db.close();
				}
			}
			
			if(!sqlfailed)
				mg.getConfig().set("convert.sql", true);
			mg.saveConfig();
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	private void sqlConverter(String minigame, SQLDatabase database){
		String table = "mgm_" + minigame + "_comp";
		
		if(!database.isOpen())
		{
			if(!database.loadSQL())
			{
				mg.getLogger().warning("Database Connection was closed and could not be re-established!");
				sqlfailed = true;
				return;
			}
		}
		
		Connection sql = database.getSql();
		
		if(database.isTable(table)){
			try {
				if(database.columnExists("UUID", table)){
					mg.getLogger().info("Table already updated!");
					return;
				}
				sql.setAutoCommit(false);
				Statement alterTable;
				alterTable = sql.createStatement();
				alterTable.execute("ALTER TABLE `" + table + "` CHANGE Player UUID varchar(40)");
				alterTable.execute("ALTER TABLE `" + table + "` ADD Player varchar(32) AFTER UUID");
				
				ResultSet set = null;
				Statement getData = sql.createStatement();
				set = getData.executeQuery("SELECT UUID FROM `" + table + "`");
				int size = 0;
				set.last();
				size = set.getRow();
				mg.getLogger().info("Converting " + size + " entries...");
				for(int i = 1; i <= size; i++){
					set.absolute(i);
					String name = set.getString(1);
					Statement updatePlayer = sql.createStatement();
					if(mg.getServer().getOfflinePlayer(name) != null){
						if(mg.getServer().getOfflinePlayer(name).getUniqueId() != null){
							String id = mg.getServer().getOfflinePlayer(name).getUniqueId().toString();
							updatePlayer.executeUpdate("UPDATE `" + table + "` SET UUID='" + id + "', Player='" + name + "' WHERE UUID='" + name + "'");
						}
						else{
							updatePlayer.execute("DELETE FROM `" + table + "` WHERE UUID='" + name + "'");
						}
					}
					else{
						updatePlayer.execute("DELETE FROM `" + table + "` WHERE UUID='" + name + "'");
					}
					updatePlayer.close();
					if(i % 100 == 0){
						double p = (double)i / (double)size * 100;
						mg.getLogger().info((int)p + "%");
					}
				}
				mg.getLogger().info("100%");
				mg.getLogger().info("Converted!");
				sql.commit();
				getData.close();
				alterTable.close();
			} catch (SQLException e) {
				e.printStackTrace();
				sqlfailed = true;
			}
		}
		else{
			mg.getLogger().info("No table found!");
		}
	}
}
