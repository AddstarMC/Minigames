package com.pauldavdesign.mineauz.minigames;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.MySQL;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.block.Chest;
//import org.bukkit.block.Dispenser;
//import org.bukkit.block.Furnace;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.pauldavdesign.mineauz.minigames.commands.CommandDispatcher;
import com.pauldavdesign.mineauz.minigames.gametypes.DMMinigame;
import com.pauldavdesign.mineauz.minigames.gametypes.LMSMinigame;
import com.pauldavdesign.mineauz.minigames.gametypes.RaceMinigame;
import com.pauldavdesign.mineauz.minigames.gametypes.SPMinigame;
import com.pauldavdesign.mineauz.minigames.gametypes.SpleefMinigame;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamDMMinigame;
import com.pauldavdesign.mineauz.minigames.scoring.ScoreTypes;

public class Minigames extends JavaPlugin{
	static Logger log = Logger.getLogger("Minecraft");
	public PlayerData pdata;
	public MinigameData mdata;
	public static Minigames plugin;
    private static Economy econ = null;
	private MySQL sql = null;
	private ScoreTypes scoretypes;

	public void onEnable(){
		plugin = this;
		PluginDescriptionFile desc = this.getDescription();
		log.info(desc.getName() + " successfully enabled.");
		
		mdata = new MinigameData();
		pdata = new PlayerData();
		
		mdata.addMinigameType(new SPMinigame());
		mdata.addMinigameType(new SpleefMinigame());
		mdata.addMinigameType(new RaceMinigame());
		mdata.addMinigameType(new LMSMinigame());
		try{
			Class.forName("net.minecraft.server.v1_4_R1.EntityPlayer");
			mdata.addMinigameType(new TeamDMMinigame());
		}catch(ClassNotFoundException e){
			getLogger().info("Note: Team Deathmatch cannot be run on this server version, please check for updates!");
		}
		mdata.addMinigameType(new DMMinigame());
		
		if(!pdata.invsave.getConfig().contains("inventories")){
			pdata.invsave.getConfig().createSection("inventories");
		}
		try{
			Set<String> set = pdata.invsave.getConfig().getConfigurationSection("inventories").getKeys(false);
			ItemStack[] items = getServer().createInventory(null, InventoryType.PLAYER).getContents();
			ItemStack[] armour = new ItemStack[4];
			int health;
			int food;
			float saturation;
			
			for(String player : set){
				health = pdata.invsave.getConfig().getInt("inventories." + player + ".health");
				food = pdata.invsave.getConfig().getInt("inventories." + player + ".food");
				saturation = Float.parseFloat(pdata.invsave.getConfig().getString("inventories." + player + ".saturation"));
				log.info("Restoring " + player + "'s Items");
				for(int i = 0; i < items.length; i++){
					if(pdata.invsave.getConfig().contains("inventories." + player + "." + i)){
						items[i] = pdata.invsave.getConfig().getItemStack("inventories." + player + "." + i);
					}
				}
				for(int i = 0; i < 4; i++){
					armour[i] = pdata.invsave.getConfig().getItemStack("inventories." + player + ".armour." + i);
				}
				
				pdata.storePlayerInventory(player, items, armour, health, food, saturation);
				items = getServer().createInventory(null, InventoryType.PLAYER).getContents();
			}
		}
		catch(Exception e){
			log.log(Level.SEVERE, "Failed to load saved inventories!");
			e.printStackTrace();
		}
		
		MinigameSave completion = new MinigameSave("completion");
		mdata.addConfigurationFile("completion", completion.getConfig());
		
		getServer().getPluginManager().registerEvents(new Events(), this);
		
		
		try{
			this.getConfig().load(this.getDataFolder() + "/config.yml");
			//Set<String> minigames = getConfig().getConfigurationSection("minigames").getKeys(false);
			List<String> mgs = new ArrayList<String>();
			if(getConfig().contains("minigames")){
				mgs = getConfig().getStringList("minigames");
			}
			final List<String> allMGS = new ArrayList<String>();
			allMGS.addAll(mgs);
			
			if(!mgs.isEmpty()){
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						for(String minigame : allMGS){
							Minigame game = new Minigame(minigame);
							game.loadMinigame();
							mdata.addMinigame(game);
						}
					}
				}, 1L);
			}
		}
		catch(FileNotFoundException ex){
			log.info("Failed to load config, creating one.");
			try{
				this.getConfig().save(this.getDataFolder() + "/config.yml");
			} 
			catch(IOException e){
				log.log(Level.SEVERE, "Could not save config.yml!");
				e.printStackTrace();
			}
		}
		catch(Exception e){
			log.log(Level.SEVERE, "Failed to load config!");
			e.printStackTrace();
		}
		
		if(!setupEconomy()){
	        getLogger().info("No Vault plugin found! You may only reward items.");
		 }
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		if(getConfig().getBoolean("use-sql")){
			sql = new MySQL(log, 
					"[Minigames] ", 
					getConfig().getString("sql-host"), 
					getConfig().getString("sql-port"), 
					getConfig().getString("sql-database"), 
					getConfig().getString("sql-username"), 
					getConfig().getString("sql-password"));
		}
		
		Calendar cal = Calendar.getInstance();
		if(cal.get(Calendar.DAY_OF_MONTH) == 21 && cal.get(Calendar.MONTH) == 8 ||
				cal.get(Calendar.DAY_OF_MONTH) == 25 && cal.get(Calendar.MONTH) == 11 ||
				cal.get(Calendar.DAY_OF_MONTH) == 1 && cal.get(Calendar.MONTH) == 0){
			getLogger().info(ChatColor.GREEN.name() + "Party Mode enabled!");
			pdata.setPartyMode(true);
		}
		
		pdata.loadDCPlayers();
		pdata.loadDeniedCommands();
		
		scoretypes = new ScoreTypes();
		
		MinigameSave save = new MinigameSave("storedCheckpoints");
		for(String player : save.getConfig().getKeys(false)){
			StoredPlayerCheckpoints spc = new StoredPlayerCheckpoints(player);
			spc.loadCheckpoints();
			pdata.addStoredPlayerCheckpoints(player, spc);
		}
		
		MinigameSave globalLoadouts = new MinigameSave("globalLoadouts");
		Set<String> keys = globalLoadouts.getConfig().getKeys(false);
		for(String loadout : keys){
			mdata.addLoadout(loadout);
			Set<String> items = globalLoadouts.getConfig().getConfigurationSection(loadout).getKeys(false);
			for(int i = 0; i < items.size(); i++){
				mdata.getLoadout(loadout).addItemToLoadout(globalLoadouts.getConfig().getItemStack(loadout + "." + i));
			}
		}
		
		getCommand("minigame").setExecutor(new CommandDispatcher());
	}

	public void onDisable(){
		PluginDescriptionFile desc = this.getDescription();
		log.info(desc.getName() + " successfully disabled.");
		
		for(Player p : getServer().getOnlinePlayers()){
			if(pdata.playerInMinigame(p)){
				pdata.quitMinigame(p, false);
			}
		}
		for(Minigame mg : mdata.getAllMinigames().values()){
			if(mg.getThTimer() != null){
				mg.getThTimer().setActive(false);
				mdata.removeTreasure(mg.getName());
			}
		}
		for(Minigame mg : mdata.getAllMinigames().values()){
			mg.saveMinigame();
		}
		if(sql != null){
			sql.close();
		}
		
		pdata.saveDCPlayers();
		pdata.saveDeniedCommands();
		
		MinigameSave globalLoadouts = new MinigameSave("globalLoadouts");
		if(mdata.hasLoadouts()){
			for(String loadout : mdata.getLoadouts()){
				for(int i = 0; i < mdata.getLoadout(loadout).getItems().size(); i++){
					globalLoadouts.getConfig().set(loadout + "." + i, mdata.getLoadout(loadout).getItems().get(i));
				}
			}
		}
		else{
			globalLoadouts.getConfig().set("globalloadouts", null);
		}
		globalLoadouts.saveConfig();
	}
	
	private boolean setupEconomy(){
        if(getServer().getPluginManager().getPlugin("Vault") == null){
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null){
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public boolean hasEconomy(){
		if(econ != null){
			return true;
		}
		return false;
	}
	
	public Economy getEconomy(){
		return econ;
	}
	
	public PlayerData getPlayerData(){
		return pdata;
	}
	
	public MinigameData getMinigameData(){
		return mdata;
	}
	
	public MySQL getSQL(){
		return sql;
	}
	
	public ScoreTypes getScoreTypes(){
		return scoretypes;
	}
}
