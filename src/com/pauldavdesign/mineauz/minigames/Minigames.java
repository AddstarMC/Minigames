package com.pauldavdesign.mineauz.minigames;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.MySQL;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Minigames extends JavaPlugin{
	static Logger log = Logger.getLogger("Minecraft");
	public PlayerData pdata;
	public MinigameData mdata;
	public static Minigames plugin;
    private static Economy econ = null;
	private MySQL sql = null;
	
	public void onEnable(){
		plugin = this;
		PluginDescriptionFile desc = this.getDescription();
		log.info(desc.getName() + " successfully enabled.");
		
		mdata = new MinigameData();
		pdata = new PlayerData();
		
		mdata.addMinigameType("sp", new SPMinigame());
		mdata.addMinigameType("spleef", new MPMinigame());
		mdata.addMinigameType("race", new MPMinigame());
		mdata.addMinigameType("lms", new MPMinigame());
		mdata.addMinigameType("teamdm", new TeamDMMinigame());
		
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
			else{
				mgs = new ArrayList<String>();
			}
			if(!mgs.isEmpty()){
				for(String minigame : mgs){
					Minigame game = new Minigame(minigame);
					game.loadMinigame();
					mdata.addMinigame(game);
				}
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
			ex.printStackTrace();
		}
		catch(Exception e){
			log.log(Level.SEVERE, "Failed to load config!");
			e.printStackTrace();
		}
		
		if(!setupEconomy()){
	        getLogger().info("No Vault plugin found! You may only reward items.");
	        return;
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
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = null;
		
		if(sender instanceof Player){
			player = (Player) sender;
		}
		
		String arglong = "";
		for(int i = 0; i < args.length; i++){
			arglong += " " + args[i];
		}
		log.info("[" + sender.getName() + "] /" + cmd.getName() + arglong);
		
		if(cmd.getName().equalsIgnoreCase("minigame") && player != null){
			if(args.length == 0){
				player.sendMessage(ChatColor.GREEN + "Minigames");
				player.sendMessage(ChatColor.GRAY + "By: " + this.getDescription().getAuthors().get(0));
				player.sendMessage(ChatColor.GRAY + "Version: " +  this.getDescription().getVersion());
				player.sendMessage(ChatColor.GRAY + "Type /minigame help for help");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("create") && args.length > 1){
				if(player.hasPermission("minigame.create")){
					if(mdata.getMinigame(args[1]) == null) {
						String minigame = args[1];
						Minigame mgm = new Minigame(minigame, "sp", player.getLocation());
						
						player.sendMessage(ChatColor.GRAY + "The minigame " + args[1] + " has been created.");
						
						List<String> mgs = null;
						if(getConfig().contains("minigames")){
							mgs = getConfig().getStringList("minigames");
						}
						else{
							mgs = new ArrayList<String>();
						}
						mgs.add(minigame);
						getConfig().set("minigames", mgs);
						saveConfig();
						
						mgm.saveMinigame();
						mdata.addMinigame(mgm);
						
					}
					else {
						player.sendMessage(ChatColor.RED + "Error: A minigame already exists by that name!");
					}
				}
				else{
					player.sendMessage(ChatColor.RED + "You do not have permission to create Minigames!");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("set")){

//				String minigame = "";
//				Set<String> mgmset = mdata.getAllMinigames().keySet();
//				for(int i = 0; i < mgmset.size(); i++){
//					if(getConfig().getStringList("minigames").get(i).equalsIgnoreCase(args[1])){
//						minigame = getConfig().getStringList("minigames").get(i);
//					}
//				}
				Set<String> mgtypes = mdata.getMinigameTypes();
				
				Minigame mgm = mdata.getMinigame(args[1]);
				
				if(mgm != null){
					String minigame = mgm.getName();
					if(args[2].equalsIgnoreCase("start") && args.length > 1 && player.hasPermission("minigame.set.start")){
						if(args.length == 3){
							mgm.setStartLocation(player.getLocation());
							sender.sendMessage(ChatColor.GRAY + "Starting position has been set for " + minigame);
						}
						else if(args.length == 4 && args[3].matches("[0-9]+")){
							int position = Integer.parseInt(args[3]);
							
							if(position >= 1){
								mgm.addStartLocation(player.getLocation(), position);
								sender.sendMessage(ChatColor.GRAY + "Starting position has been set for player " + args[3]);
							}
							else{
								sender.sendMessage(ChatColor.RED + "Error: Invalid starting position: " + args[3]);
							}
						}
						else if(args.length == 5 && (args[3].matches("b|blue") || args[3].matches("r|red")) && args[4].matches("[0-9]+")){
							int position = Integer.parseInt(args[4]);
							int team = 0;
							
							if(args[3].matches("b|blue")){
								team = 1;
							}
							
							if(position >= 1){
								if(team == 0){
									mgm.addStartLocationRed(player.getLocation(), position);
									sender.sendMessage(ChatColor.GRAY + "Starting position for " + ChatColor.RED + "red team" + ChatColor.GRAY + " has been set for player " + position);
								}
								else{
									mgm.addStartLocationBlue(player.getLocation(), position);
									sender.sendMessage(ChatColor.GRAY + "Starting position for " + ChatColor.BLUE + "blue team" + ChatColor.GRAY + " has been set for player " + position);
								}
							}
							else{
								sender.sendMessage(ChatColor.RED + "Error: Invalid starting position: " + args[3]);
							}
						}
					}
					else if(args[2].equalsIgnoreCase("end") && args.length > 1 && player.hasPermission("minigame.set.end")){
						mgm.setEndPosition(player.getLocation());
						sender.sendMessage(ChatColor.GRAY + "Ending position has been set for " + minigame);
					}
					else if(args[2].equalsIgnoreCase("quit") && args.length > 1 && player.hasPermission("minigame.set.quit")){
						mgm.setQuitPosition(player.getLocation());
						sender.sendMessage(ChatColor.GRAY + "Quit position has been set for " + minigame);
					}
					else if(args[2].equalsIgnoreCase("lobby") && args.length > 1 && player.hasPermission("minigame.set.lobby")){
						mgm.setLobbyPosition(player.getLocation());
						sender.sendMessage(ChatColor.GRAY + "Lobby position has been set for " + minigame);
					}
					else if(args[2].equalsIgnoreCase("reward") || args[2].equalsIgnoreCase("reward2") && args.length > 3 && player.hasPermission("minigame.set.reward")){
						String itemname = "";
						double price = 0;
						String[] split = null;
						if(args[3].contains(":")){
							split = args[3].split(":");
							itemname = split[0].toUpperCase();
						}
						else{
							itemname = args[3].toUpperCase();
						}
						
						int itemint = 0;
						try{
							itemint = Integer.parseInt(itemname);
						}catch(Exception e){}
						
						if(args[3].contains("$")){
							price = Double.parseDouble(args[3].replace("$", ""));
						}
						
						ItemStack it;
						
						if(Material.getMaterial(itemname) != null){
							it = new ItemStack(Material.getMaterial(itemname));
							if(split != null && split.length == 2){
								it.setDurability(Short.valueOf(split[1]));
							}
							if(args.length == 5){
								int amount = Integer.parseInt(args[4]);
								it.setAmount(amount);
								if(args[2].equalsIgnoreCase("reward")){
									mgm.setRewardItem(it);
									sender.sendMessage(ChatColor.GRAY + "The reward item for " + minigame + " was set to " + itemname + " with an amount of " + args[4]);
								}
								else{
									mgm.setSecondaryRewardItem(it);
									sender.sendMessage(ChatColor.GRAY + "The secondary reward item for " + minigame + " was set to " + itemname + " with an amount of " + args[4]);
								}
							}
							else{
								it.setAmount(1);
								if(args[2].equalsIgnoreCase("reward")){
									mgm.setRewardItem(it);
									sender.sendMessage(ChatColor.GRAY + "The reward item for " + minigame + " was set to " + itemname);
								}
								else{
									mgm.setSecondaryRewardItem(it);
									sender.sendMessage(ChatColor.GRAY + "The secondary reward item for " + minigame + " was set to " + itemname);
								}
							}
						}
						else if(itemint != 0){
							it = new ItemStack(itemint);
							if(split != null && split.length == 2){
								it.setDurability(Short.valueOf(split[1]));
							}
							if(args.length == 5){
								int amount = Integer.parseInt(args[4]);
								it.setAmount(amount);
								if(args[2].equalsIgnoreCase("reward")){
									mgm.setRewardItem(it);
									sender.sendMessage(ChatColor.GRAY + "The reward item for " + minigame + " was set to " + args[3] + " with an amount of " + args[4]);
								}
								else{
									mgm.setSecondaryRewardItem(it);
									sender.sendMessage(ChatColor.GRAY + "The secondary item reward for " + minigame + " was set to " + args[3] + " with an amount of " + args[4]);
								}
							}
							else{
								it.setAmount(1);
								if(args[2].equalsIgnoreCase("reward")){
									mgm.setRewardItem(it);
									sender.sendMessage(ChatColor.GRAY + "The reward item for " + minigame + " was set to " + args[3]);
								}
								else{
									mgm.setSecondaryRewardItem(it);
									sender.sendMessage(ChatColor.GRAY + "The secondary reward item for " + minigame + " was set to " + args[3]);
								}
							}
						}
						else if(hasEconomy() && price != 0){
							if(args[2].equalsIgnoreCase("reward")){
								mgm.setRewardPrice(price);
								sender.sendMessage(ChatColor.GRAY + "The reward for " + minigame + " was set to $" + price);
							}
							else{
								mgm.setSecondaryRewardPrice(price);
								sender.sendMessage(ChatColor.GRAY + "The secondary reward for " + minigame + " was set to $" + price);
							}
						}
						else if(!hasEconomy() && price != 0){
							player.sendMessage(ChatColor.RED + "Vault is required to use dollar prizes.");
							player.sendMessage(ChatColor.RED + "Click here to download: http://dev.bukkit.org/server-mods/vault/");
						}
						else{
							if(args[2].equalsIgnoreCase("reward")){
								sender.sendMessage(ChatColor.RED + "The reward " + args[3] + " is not a valid item name!");
							}
							else{
								sender.sendMessage(ChatColor.RED + "The secondary reward " + args[3] + " is not a valid item name!");
							}
						}
					}
					else if(args[2].equalsIgnoreCase("type") && player.hasPermission("minigame.set.type")){
						if(mgtypes.contains(args[3]) || args[3].equalsIgnoreCase("th")){
							mgm.setType(args[3]);
							sender.sendMessage(ChatColor.GRAY + "Minigame type has been set to " + args[3]);
						}
						else{
							sender.sendMessage(ChatColor.RED + "Error: Invalid minigame type!");
						}
					}
					else if(args[2].equalsIgnoreCase("sfloor") && args.length > 3 && player.hasPermission("minigame.set.sfloor")){
						if(args[3].equals("1")){
							mgm.setSpleefFloor1(player.getLocation());
							sender.sendMessage(ChatColor.GRAY + "Spleef floor corner 1 has been set for " + minigame);
						}
						else if(args[3].equals("2")){
							mgm.setSpleefFloor2(player.getLocation());
							sender.sendMessage(ChatColor.GRAY + "Spleef floor corner 2 has been set for " + minigame);
						}
						else{
							sender.sendMessage(ChatColor.RED + "Error: Invalid spleef floor position, use 1 or 2");
						}
					}
					else if(args[2].equalsIgnoreCase("sfloormat") && args.length > 3 && player.hasPermission("minigame.set.sfloormat")){
						String itemname = "";
						String[] split = null;
						if(args[3].contains(":")){
							split = args[3].split(":");
							itemname = split[0].toUpperCase();
						}
						else{
							itemname = args[3].toUpperCase();
						}
						
						int itemint = 0;
						try{
							itemint = Integer.parseInt(itemname);
						}catch(Exception e){}
						
						ItemStack item = null;
						try{
							itemint = Integer.parseInt(args[3]);
						}catch(Exception e){}
						if(Material.getMaterial(itemname) != null){
							item = new ItemStack(Material.getMaterial(itemname), 1);
							if(split != null){
								short durint = Short.parseShort(split[1]);
								item.setDurability(durint);
								try{
									itemint = Integer.parseInt(itemname);
								}catch(Exception e){}
							}
							mgm.setSpleefFloorMaterial(item.getType());
							player.sendMessage(ChatColor.GRAY + "Set the spleef floor material to " + itemname.toLowerCase().replace("_", " "));
						}
						else if(itemint != 0){
							item = new ItemStack(itemint, 1);
							if(split != null){
								short durint = Short.parseShort(split[1]);
								item.setDurability(durint);
								try{
									itemint = Integer.parseInt(itemname);
								}catch(Exception e){}
							}
							mgm.setSpleefFloorMaterial(item.getType());
							player.sendMessage(ChatColor.GRAY + "Set the spleef floor material to " + args[4]);
						}
						else{
							sender.sendMessage(ChatColor.RED + "The material " + args[4] + " is not a valid material name!");
						}
					}
					else if(args[2].equalsIgnoreCase("maxplayers") && args.length > 3 && player.hasPermission("minigame.set.maxplayers")){
						int max = Integer.parseInt(args[3]);
						mgm.setMaxPlayers(max);
						sender.sendMessage(ChatColor.GRAY + "Maximum players has been set to " + args[3] + " for " + minigame);
					}
					else if(args[2].equalsIgnoreCase("minplayers") && args.length > 3 && player.hasPermission("minigame.set.minplayers")){
						int min = Integer.parseInt(args[3]);
						mgm.setMinPlayers(min);
						sender.sendMessage(ChatColor.GRAY + "Minimum players has been set to " + args[3] + " for " + minigame);
					}
					else if(args[2].equalsIgnoreCase("loadout") && args.length > 4){
						if(args[3].equalsIgnoreCase("add") && player.hasPermission("minigame.set.loadout")){
							String itemname = "";
							String[] split = null;
							if(args[4].contains(":")){
								split = args[4].split(":");
								itemname = split[0].toUpperCase();
							}
							else{
								itemname = args[4].toUpperCase();
							}
							
							int itemint = 0;
							try{
								itemint = Integer.parseInt(itemname);
							}catch(Exception e){}
							
							ItemStack item = null;
							try{
								itemint = Integer.parseInt(args[4]);
							}catch(Exception e){}
							if(Material.getMaterial(itemname) != null){
								if(args.length == 6){
									int amount = Integer.parseInt(args[5]);
									item = new ItemStack(Material.getMaterial(itemname), amount);
									if(split != null){
										short durint = Short.parseShort(split[1]);
										item.setDurability(durint);
										try{
											itemint = Integer.parseInt(itemname);
										}catch(Exception e){}
									}
									mgm.addLoadoutItem(item);
									player.sendMessage(ChatColor.GRAY + "Added " + args[5] + " of item " + itemname.toLowerCase().replace("_", " ") + " to the loadout.");
								}
								else{
									item = new ItemStack(Material.getMaterial(itemname), 1);
									if(split != null){
										short durint = Short.parseShort(split[1]);
										item.setDurability(durint);
										try{
											itemint = Integer.parseInt(itemname);
										}catch(Exception e){}
									}
									mgm.addLoadoutItem(item);
									player.sendMessage(ChatColor.GRAY + "Added item " + itemname.toLowerCase().replace("_", " ") + " to the loadout.");
								}
							}
							else if(itemint != 0){
								if(args.length == 6){
									item = new ItemStack(itemint, Integer.parseInt(args[5]));
									if(split != null){
										short durint = Short.parseShort(split[1]);
										item.setDurability(durint);
										try{
											itemint = Integer.parseInt(itemname);
										}catch(Exception e){}
									}
									mgm.addLoadoutItem(item);
									player.sendMessage(ChatColor.GRAY + "Added " + args[5] + " of item " + args[4] + " to the loadout.");
								}
								else{
									item = new ItemStack(itemint, 1);
									if(split != null){
										short durint = Short.parseShort(split[1]);
										item.setDurability(durint);
										try{
											itemint = Integer.parseInt(itemname);
										}catch(Exception e){}
									}
									mgm.addLoadoutItem(item);
									player.sendMessage(ChatColor.GRAY + "Added item " + args[4] + " to the loadout.");
								}
							}
							else{
								sender.sendMessage(ChatColor.RED + "The loadout item " + args[4] + " is not a valid item name!");
							}
						}
						else if(args[3].equalsIgnoreCase("remove") && player.hasPermission("minigame.set.loadout")){
							
							ItemStack item = null;
							int itemint = 0;
							try{
								itemint = Integer.parseInt(args[4]);
							}catch(Exception e){}
							if(Material.getMaterial(args[4].toUpperCase()) != null){
								item = new ItemStack(Material.getMaterial(args[4].toUpperCase()));
								for(ItemStack i : mgm.getLoadout()){
									if(item.getTypeId() == i.getTypeId()){
										item = i;
										item.setAmount(i.getAmount());
										break;
									}
								}
								mgm.removeLoadoutItem(item);
								player.sendMessage(ChatColor.GRAY + "Removed item " + args[4] + " from the loadout.");
							}
							else if(itemint != 0){
								item = new ItemStack(Integer.parseInt(args[4]));
								for(ItemStack i : mgm.getLoadout()){
									if(item.getTypeId() == i.getTypeId()){
										item = i;
										item.setAmount(i.getAmount());
										break;
									}
								}
								mgm.removeLoadoutItem(item);
								player.sendMessage(ChatColor.GRAY + "Removed item " + args[4] + " from the loadout.");
							}
							else{
								sender.sendMessage(ChatColor.RED + "The loadout item " + args[3] + " is not a valid item name!");
							}
						}
						else if(args[3].equalsIgnoreCase("clear") && player.hasPermission("minigame.set.loadout")){
							for(ItemStack item : mgm.getLoadout()){
								mgm.removeLoadoutItem(item);
								player.sendMessage(ChatColor.GRAY + "Removed all items from the loadout.");
							}
						}
					}
					else if(args[2].equalsIgnoreCase("enabled") && args.length > 3 && player.hasPermission("minigame.set.enabled")){
						boolean enabled = Boolean.parseBoolean(args[3]);
						mgm.setEnabled(enabled);
						if(enabled){
							sender.sendMessage(ChatColor.GRAY + minigame + " is now enabled.");
						}
						else{
							sender.sendMessage(ChatColor.GRAY + minigame + " is now disabled.");
						}
						mgm.saveMinigame();
					}
					else if(args[2].equalsIgnoreCase("maxradius") && player.hasPermission("minigame.set.maxradius")){
						int max = Integer.parseInt(args[3]);
						mgm.setMaxRadius(max);
						sender.sendMessage(ChatColor.GRAY + "Maximum treasure spawn radius has been set to " + args[3] + " for " + minigame);
					}else if(args[2].equalsIgnoreCase("flag")){
						if(args[3].equalsIgnoreCase("add") && player.hasPermission("minigame.set.flag.add") && args.length > 4){
							mgm.addFlag(args[4]);
							sender.sendMessage(ChatColor.GRAY + args[4] + " flag added to " + minigame);
						}
						else if(args[3].equalsIgnoreCase("remove") && player.hasPermission("minigame.set.flag.remove") && args.length > 4){
							if(mgm.removeFlag(args[4])){
								player.sendMessage(ChatColor.GRAY + "Removed the " + args[4] + " flag.");
							}
							else{
								player.sendMessage(ChatColor.RED + "There is no flag by the name " + args[4] + ".");
							}
						}
						else if(args[3].equalsIgnoreCase("list") && player.hasPermission("minigame.set.flag.list") && args.length > 3){
							if(!mgm.getFlags().isEmpty()){
								List<String> flag = mgm.getFlags();
								String flags = "";
								for(int i = 0; i < flag.size(); i++){
									flags += flag.get(i);
									if(i != flag.size() - 1){
										flags += ", ";
									}
								}
								player.sendMessage(ChatColor.BLUE + "All " + minigame + " flags:");
								player.sendMessage(ChatColor.GRAY + flags);
							}
							else{
								player.sendMessage(ChatColor.RED + "There are no flags in " + minigame + "!");
							}
						}
					}
					else if(args[2].equalsIgnoreCase("bets") && args.length > 3 && player.hasPermission("minigame.set.bets")){
						if(args[3].equalsIgnoreCase("true")){
							mgm.setBetting(true);
							sender.sendMessage(ChatColor.GRAY + "Betting has been enabled for " + minigame);
						}
						else{
							mgm.setBetting(false);
							sender.sendMessage(ChatColor.GRAY + "Betting has been disabled for " + minigame);
						}
					}
					else if(args[2].equalsIgnoreCase("location") && args.length > 3 && player.hasPermission("minigame.set.location")){
						String location = "";
						for(int i = 3; i < args.length; i++){
							location += args[i];
							if(i != args.length - 1){
								location += " ";
							}
						}
						mgm.setLocation(location);
						sender.sendMessage(ChatColor.GRAY + "The location name for " + minigame + " has been set to " + location);
					}
					else if(args[2].equalsIgnoreCase("restoreblock") && args.length == 4 && player.hasPermission("minigame.set.restoreblock")){
						Location loc = player.getLocation().getBlock().getLocation().clone();
						loc.setY(loc.getY() - 1);
						ItemStack[] items = null;
						if(loc.getBlock().getState() instanceof Chest){
							Chest chest = (Chest) loc.getBlock().getState();
							items = chest.getInventory().getContents().clone();
						}
						else if(loc.getBlock().getState() instanceof Furnace){
							Furnace furnace = (Furnace) loc.getBlock().getState();
							items = furnace.getInventory().getContents().clone();
						}
						else if(loc.getBlock().getState() instanceof Dispenser){
							Dispenser dispenser = (Dispenser) loc.getBlock().getState();
							items = dispenser.getInventory().getContents().clone();
						}
						RestoreBlock rb = new RestoreBlock(args[3], loc.getBlock().getType(), loc);
						rb.setItems(items);
						mdata.getMinigame(minigame).addRestoreBlock(rb);
						player.sendMessage(ChatColor.GRAY + "Saved block \"" + loc.getBlock().getType().toString() + "\" for " + minigame + " under the name " + args[3]);
						
					}
					else if(args[2].equalsIgnoreCase("restoreblock") && args.length == 5 && args[4].equalsIgnoreCase("remove") && player.hasPermission("minigame.set.restoreblock")){
						if(mgm.getRestoreBlocks().containsKey(args[3])){
							mgm.removeRestoreBlock(args[3]);
							player.sendMessage(ChatColor.GRAY + "Removed " + args[3]);
						}
						else{
							player.sendMessage(ChatColor.RED + "There is no restore block by the name \"" + args[3] + "\"");
						}
					}
					else if(args[2].equalsIgnoreCase("usepermissions") && args.length == 4 && player.hasPermission("minigame.set.usepermission")){
						boolean bool = Boolean.parseBoolean(args[3]);
						mgm.setUsePermissions(bool);
						player.sendMessage(ChatColor.GRAY + "Use permissions has been set to " + bool + " for " + mgm.getName());
					}
					else if(args[2].equalsIgnoreCase("maxscore") && args.length == 4 && player.hasPermission("minigame.set.maxscore")){
						if(args[3].matches("[0-9]+")){
							int maxscore = Integer.parseInt(args[3]);
							mgm.setMaxScore(maxscore);
							player.sendMessage(ChatColor.GRAY + "Maximum score has been set to " + maxscore + " for " + mgm.getName());
						}
						else{
							sender.sendMessage(ChatColor.RED + "Error: " + args[3] + " is not a number!");
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "Error: Invalid command!");
					}
				}
				else{
					sender.sendMessage(ChatColor.RED + args[1] + " does not exist!");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("join") && args.length > 1) {
				if(player != null && player.hasPermission("minigame.join")){
					String minigame = args[1];
					Minigame mgm = mdata.getMinigame(minigame);
					if(mgm != null && (!mgm.getUsePermissions() || player.hasPermission("minigame.join." + mgm.getName().toLowerCase()))){
						if(!pdata.playerInMinigame(player)){
							sender.sendMessage(ChatColor.GREEN + "Starting " + minigame);
							pdata.joinMinigame(player, minigame);
						}
						else {
							player.sendMessage(ChatColor.RED + "Error: You are already playing a minigame! Quit this one before joining another.");
						}
					}
					else if(mgm != null && mgm.getUsePermissions()){
						player.sendMessage(ChatColor.RED + "You do not have permission minigame.join." + mgm.getName().toLowerCase());
					}
					else{
						player.sendMessage(ChatColor.RED + "Error: That minigame doesn't exist!");
					}
				}
				else{
					player.sendMessage(ChatColor.RED + "Error: You do not have permission minigame.join");
				}
				return true;
			}
			else if(args[0].equals("start") && args.length > 1){
				Minigame mgm = mdata.getMinigame(args[1]);
				//String minigame = mgm.getName();
				
				if(player.hasPermission("minigame.start")){
					if( mgm != null && mgm.getThTimer() == null && mgm.getType().equals("th")){
						mdata.startGlobalMinigame(mgm.getName());
					}
					else if(mgm == null || !mgm.getType().equals("th")){
						player.sendMessage(ChatColor.RED + "There is no TreasureHunt Minigame by the name \"" + args[1] + "\"");
					}
					else if(mgm.getThTimer() != null){
						player.sendMessage(ChatColor.RED + mgm.getName() + " is already running!");
					}
				}
				else{
					player.sendMessage(ChatColor.RED + "Error: You do not have permission minigame.start");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("stop") && args.length > 1){
				Minigame mgm = mdata.getMinigame(args[1]);
//				String minigame = mgm.getName();
				
				if(player.hasPermission("minigame.stop")){
					if(mgm != null && mgm.getThTimer() != null && mgm.getType().equals("th")){
						getServer().broadcast(ChatColor.LIGHT_PURPLE + "The " + mgm.getName() + " treasure has been removed from the world", "minigame.treasure.announce");
						mgm.getThTimer().setActive(false);
						mdata.removeTreasure(mgm.getName());
						mgm.setThTimer(null);
					}
					else if(mgm == null || !mgm.getType().equals("th")){
						player.sendMessage(ChatColor.RED + "There is no TreasureHunt Minigame by the name \"" + args[1] + "\"");
					}
					else{
						player.sendMessage(ChatColor.RED + mgm.getName() + " is not running!");
					}
				}
				else{
					player.sendMessage(ChatColor.RED + "Error: You do not have permission minigame.stop");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("quit")){
				if(player.hasPermission("minigame.quit") && args.length == 1){
					if(pdata.playerInMinigame(player)){
						pdata.quitMinigame(player, false);
					}
					else {
						player.sendMessage(ChatColor.RED + "Error: You are not in a minigame!");
					}
				}
				else if(args.length > 1){
					if(player.hasPermission("minigame.quit.other")){
						List<Player> plist = pdata.playersInMinigame();
						Player ply = null;
						for(Player p : plist){
							if(p.getName().toLowerCase().contains(args[1].toLowerCase())){
								ply = p;
							}
						}
						if(ply != null){
							pdata.quitMinigame(ply, false);
							player.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " to quit the minigame.");
						}
						else{
							player.sendMessage(ChatColor.RED + "Error: There is no player by that name!");
						}
					}
					else
						player.sendMessage(ChatColor.RED + "You don't have permission minigame.quit.other");
				}
				else if(!player.hasPermission("minigame.quit")){
					player.sendMessage(ChatColor.RED + "You don't have permission minigame.quit");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("revert") || args[0].equalsIgnoreCase("r")){
				if(player.hasPermission("minigame.revert")){
					if(pdata.playerInMinigame(player)){
						pdata.revertToCheckpoint(player);
					}
					else {
						player.sendMessage(ChatColor.RED + "Error: You are not in a minigame!");
					}
				}
				else{
					player.sendMessage(ChatColor.RED + "Error: You don't have permission minigame.revert");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("hint")){
				if(args.length > 1){
					Minigame mgm = mdata.getMinigame(args[1]);
					
					if(mgm != null && mgm.getThTimer() != null && mgm.getType().equals("th") && player.hasPermission("minigame.treasure.hint")){
						TreasureHuntTimer treasure = mgm.getThTimer();
						if(treasure.getActive() && treasure.getTreasureFound() == false){
							treasure.hints(player);
						}
						else{
							player.sendMessage(ChatColor.GRAY + mgm.getName() + " is currently not running.");
						}
					}
					else if(mgm == null || !mgm.getType().equals("th")){
						player.sendMessage(ChatColor.RED + "There is no treasure hunt running by the name \"" + args[1] + "\"");
					}
					else{
						player.sendMessage(ChatColor.RED + "Error: You don't have permission minigame.hint");
					}
				}
				else{
					List<Minigame> mgs = new ArrayList<Minigame>();
					for(Minigame mg : mdata.getAllMinigames().values()){
						if(mg.getType().equals("th")){
							mgs.add(mg);
						}
					}
					if(player.hasPermission("minigame.treasure.hint") && !mgs.isEmpty()){
						if(mgs.size() > 1){
							//Set<String> tht = mdata.getAllTreasureHuntTimers();
							//Object[] thtimers = tht.toArray();
							player.sendMessage(ChatColor.LIGHT_PURPLE + "Currently running Treasure Hunts:");
							String treasures = "";
							for(int i = 0; i < mgs.size(); i++){
								treasures += mgs.get(i).getName();
								if(i != mgs.size() - 1){
									treasures += ", ";
								}
							}
							player.sendMessage(ChatColor.GRAY + treasures);
						}
						else{
							//Set<String> tht = mdata.getAllTreasureHuntTimers();
							//Object[] thtimers = tht.toArray();
							TreasureHuntTimer treasure = mgs.get(0).getThTimer();
							if(treasure.getActive() && treasure.getTreasureFound() == false){
								treasure.hints(player);
							}
							else{
								player.sendMessage(ChatColor.GRAY + mgs.get(0).getName() + " is currently not running.");
							}
						}
					}
					else if(mgs.isEmpty()){
						player.sendMessage(ChatColor.LIGHT_PURPLE + "There are no Treasure Hunt minigames currently running.");
					}
					else{
						player.sendMessage(ChatColor.RED + "Error: You don't have permission minigame.hint");
					}
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("info") && args.length > 1){
				Minigame mgm = mdata.getMinigame(args[1]);
//				String minigame = mgm.getName();
				
				if(mgm != null){
					if(player.hasPermission("minigame.info")){
						
						player.sendMessage(ChatColor.GRAY + "Checking " + mgm.getName() + " minigame...");
						if(!mgm.getType().equals("th")){
							if(!mgm.getType().equals("teamdm")){
								if(!mgm.getStartLocations().isEmpty()){
									player.sendMessage(ChatColor.GREEN + "Starting position set (" + mgm.getStartLocations().size() + ")");
								}
								else {
									player.sendMessage(ChatColor.RED + "Starting position is not set!");
								}
							}
							else{
								if(!mgm.getStartLocationsRed().isEmpty()){
									player.sendMessage(ChatColor.GREEN + "Red starting positions set (" + mgm.getStartLocationsRed().size() + ")");
								}
								else {
									player.sendMessage(ChatColor.RED + "Red starting positions are not set!");
								}
								
								if(!mgm.getStartLocationsBlue().isEmpty()){
									player.sendMessage(ChatColor.GREEN + "Blue starting positions set (" + mgm.getStartLocationsBlue().size() + ")");
								}
								else {
									player.sendMessage(ChatColor.RED + "Blue starting positions are not set!");
								}
							}
							
							if(mgm.getEndPosition() != null){
								player.sendMessage(ChatColor.GREEN + "Ending position set");
							}
							else {
								player.sendMessage(ChatColor.RED + "Ending position is not set!");
							}
							
							if(mgm.getQuitPosition() != null){
								player.sendMessage(ChatColor.GREEN + "Quit position set");
							}
							else {
								player.sendMessage(ChatColor.RED + "Quit position is not set!");
							}
							
							if(mgm.getRewardItem() != null){
								player.sendMessage(ChatColor.GREEN + "Reward Item: " + mgm.getRewardItem().getType().toString().toLowerCase().replace("_", " "));
							}
							else {
								player.sendMessage(ChatColor.RED + "Reward Item is not set!");
							}
							
							if(mgm.getRewardPrice() > 0 && econ != null){
								player.sendMessage(ChatColor.GREEN + "Reward Money: $" + mgm.getRewardPrice());
							}
							else if(econ != null){
								player.sendMessage(ChatColor.RED + "Reward money is not set!");
							}
							
							if(mgm.getSecondaryRewardItem() != null){
								player.sendMessage(ChatColor.GREEN + "Secondary Reward Item: " + mgm.getSecondaryRewardItem().getType().toString().toLowerCase().replace("_", " "));
							}
							else {
								player.sendMessage(ChatColor.RED + "Secondary reward is not set!");
							}
							
							if(mgm.getSecondaryRewardPrice() > 0 && econ != null){
								player.sendMessage(ChatColor.GREEN + "Secondary Reward Money: $" + mgm.getSecondaryRewardPrice());
							}
							else if(econ != null){
								player.sendMessage(ChatColor.RED + "Secondary Reward money is not set!");
							}
							
							if(mgm.isEnabled()){
								player.sendMessage(ChatColor.GREEN + "Enabled: true");
							}
							else{
								player.sendMessage(ChatColor.RED + "Enabled: false");
							}
							
							if(mgm.getType() != null){
								player.sendMessage(ChatColor.GREEN + "Type: (" + mgm.getType() + ")");
								
								if(mgm.getType().equalsIgnoreCase("spleef")){
									if(mgm.getSpleefFloor1() != null){
										player.sendMessage(ChatColor.GREEN + "Floor corner 1 set");
									}
									else {
										player.sendMessage(ChatColor.RED + "Floor corner 1 is not set!");
									}
									
									if(mgm.getSpleefFloor2() != null){
										player.sendMessage(ChatColor.GREEN + "Floor corner 2 set");
									}
									else {
										player.sendMessage(ChatColor.RED + "Floor corner 2 is not set!");
									}
									
									player.sendMessage(ChatColor.GREEN + "Floor material: " + mgm.getSpleefFloorMaterial().toString().toLowerCase().replace("_", " "));
								}
								
								if(!mgm.getType().equalsIgnoreCase("sp")){
									if(!mgm.getType().equals("teamdm")){
										if(mgm.bettingEnabled()){
											player.sendMessage(ChatColor.GREEN + "Betting enabled: true");
										}
										else{
											player.sendMessage(ChatColor.RED + "Betting enabled: false");
										}
									}
									
									if(mgm.getLobbyPosition() != null){
										player.sendMessage(ChatColor.GREEN + "Lobby Set");
									}
									else{
										player.sendMessage(ChatColor.RED + "Lobby is not set!");
									}
									
									
									player.sendMessage(ChatColor.GREEN + "Maximum players: " + mgm.getMaxPlayers());
									
									player.sendMessage(ChatColor.GREEN + "Minimum players: " + mgm.getMinPlayers());
								}
								
								if(mgm.getType().equals("teamdm")){
									player.sendMessage(ChatColor.GREEN + "Max Score: " + mgm.getMaxScore());
									player.sendMessage(ChatColor.GRAY + "Min Score: " + mgm.getMaxScorePerPlayer(mgm.getMinPlayers()));
								}
							}
							else {
								player.sendMessage(ChatColor.RED + "Type is not set!");
							}
							
							if(mgm.getType().equalsIgnoreCase("sp")){
								if(!mgm.getFlags().isEmpty()){
									player.sendMessage(ChatColor.GREEN + "Require flags: true");
									List<String> list = mgm.getFlags();
									String flags = "";
									for(String item : list){
										flags += item + ", ";
									}
									flags = flags.substring(0, flags.length() - 1);
									player.sendMessage(ChatColor.GREEN + "Flags: " + ChatColor.GRAY + flags);
								}
								else {
									player.sendMessage(ChatColor.RED + "Require flags: false");
								}
							}
							
							player.sendMessage(ChatColor.GREEN + "Use permissions: " + mgm.getUsePermissions());
							if(mgm.getUsePermissions())
								player.sendMessage(ChatColor.GRAY + "minigame.join." + mgm.getName().toLowerCase());
						}
						else{
							player.sendMessage(ChatColor.GREEN + "Maximum radius: " + mgm.getMaxRadius());
							
							if(mgm.getLocation() != null){
								player.sendMessage(ChatColor.GREEN + "Location: " + mgm.getLocation());
							}
							else {
								player.sendMessage(ChatColor.RED + "Location: Unset!");
							}
						}
						player.sendMessage(ChatColor.GRAY + "The minigame " + mgm.getName() + "s check is complete");
					}
					else if(player != null){
						player.sendMessage(ChatColor.RED + "Error: You don't have permission minigame.info");
					}
				}
				else{
					player.sendMessage(ChatColor.RED + "There is no Minigame by the name " + args[1]);
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("end")){
				if(args.length == 1 && player.hasPermission("minigame.end")){
					pdata.endMinigame(player);
				}
				else if(args.length == 1){
					player.sendMessage(ChatColor.RED + "Error: You don't have permission to perform this command!");
				}
				else if(args.length > 1){
					List<Player> plist = pdata.playersInMinigame();
					Player ply = null;
					for(Player p : plist){
						if(p.getName().toLowerCase().contains(args[1].toLowerCase())){
							ply = p;
						}
					}
					
					if(ply != null && player.hasPermission("minigame.end.other")){
						pdata.endMinigame(ply);
						player.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " to end the minigame.");
					}
					else{
						player.sendMessage(ChatColor.RED + "This player is not playing a minigame.");
					}
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("delete") && args.length > 1){
				Minigame mgm = mdata.getMinigame(args[1]);
//				String minigame = mgm.getName();
				
				if(mgm != null){
					MinigameSave save = new MinigameSave(mgm.getName(), "config");
					
					if(player.hasPermission("minigame.delete")){
						if(save.getConfig().get(mgm.getName()) != null){
							save.deleteFile();
							List<String> ls = getConfig().getStringList("minigames");
							ls.remove(mgm.getName());
							getConfig().set("minigames", ls);
							mdata.removeMinigame(mgm.getName());
							saveConfig();
							player.sendMessage(ChatColor.RED + "The minigame " + mgm.getName() + " has been removed");
						}
						else {
							player.sendMessage(ChatColor.RED + "That minigame does not exist!");
						}
					}
					else{
						player.sendMessage(ChatColor.RED + "Error: You don't have permission minigame.delete");
					}
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("sregen") && args.length > 1){
				Minigame mgm = mdata.getMinigame(args[1]);
				
				if(mgm != null && mgm.getType().equals("spleef")){
					if(player != null && player.hasPermission("minigame.sregen") || player == null){
						SpleefFloorGen floor = new SpleefFloorGen(mgm.getSpleefFloor1(), mgm.getSpleefFloor2());
						floor.regenFloor(mgm.getSpleefFloorMaterial());
						player.sendMessage(ChatColor.GRAY + "Regenerating " + mgm.getName() + " Spleef Floor");
					}
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("restoreinv") && args.length > 1){
				Set<String> set = pdata.getInventorySaveConfig().getConfigurationSection("inventories").getKeys(false);
				List<Player> players = new ArrayList<Player>();
				
				for(Player pl : getServer().getOnlinePlayers()){
					players.add(pl);
				}
				
				Player reqpl = null;
				
				for(Player pl : players){
					if(pl.getName().toLowerCase().contains(args[1].toLowerCase())){
						reqpl = pl;
					}
				}
				
				if(player.hasPermission("minigame.restoreinv") && !pdata.playerInMinigame(player) && set.contains(reqpl.getName())){
					pdata.restorePlayerData(reqpl);
					
					player.sendMessage(ChatColor.GRAY + "The inventory for " + reqpl.getName() + " has been restored.");
					reqpl.sendMessage(ChatColor.GRAY + "Your inventory has been restored.");
					pdata.saveItems(reqpl);
				}
				else if(!player.hasPermission("minigame.restoreinv")){
					player.sendMessage(ChatColor.RED + "You do not have permission minigame.restoreinv");
				}
				else if(set.contains(reqpl.getName())){
					player.sendMessage(ChatColor.RED + "This players inventory is not stored!");
				}
				else if(pdata.playerInMinigame(player)){
					player.sendMessage(ChatColor.RED + "This player is currently in a minigame, old inventory cannot be restored!");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("help")){
				if(player.hasPermission("minigame.help")){
					player.sendMessage(ChatColor.GREEN + "List of Minigame commands");
					player.sendMessage(ChatColor.BLUE + "/minigame");
					player.sendMessage(ChatColor.GRAY + "The default command (alias /mgm)");
					if(player.hasPermission("minigame.join")){
						player.sendMessage(ChatColor.BLUE + "/minigame join <minigame>");
						player.sendMessage(ChatColor.GRAY + "Joins a minigame");
					}
					if(player.hasPermission("minigame.quit")){
						player.sendMessage(ChatColor.BLUE + "/minigame quit");
						player.sendMessage(ChatColor.GRAY + "Quits your current minigame");
					}
					if(player.hasPermission("minigame.revert")){
						player.sendMessage(ChatColor.BLUE + "/minigame revert");
						player.sendMessage(ChatColor.GRAY + "Reverts you to the last checkpoint in a minigame (alias /mgm r)");
					}
					if(player.hasPermission("minigame.hint")){
						player.sendMessage(ChatColor.BLUE + "/minigame hint <minigame>");
						player.sendMessage(ChatColor.GRAY + "Gives you a hint for a treasure hunt minigame");
					}
					if(player.hasPermission("minigame.list")){
						player.sendMessage(ChatColor.BLUE + "/minigame list");
						player.sendMessage(ChatColor.GRAY + "Gives you a list of all Minigames");
					}
					
					//TODO Set help.
				}
				else{
					player.sendMessage(ChatColor.RED + "You do not have permission minigame.help");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("reload")){
				if(player.hasPermission("minigame.reload")){
					Player[] players = getServer().getOnlinePlayers();
					for(Player p : players){
						if(pdata.playerInMinigame(p)){
							pdata.quitMinigame(p, false);
						}
					}
					
					getServer().getPluginManager().disablePlugin(this);
					getServer().getPluginManager().enablePlugin(this);
					
					player.sendMessage(ChatColor.GREEN + "Reloaded Minigame configs");
				}
				else{
					player.sendMessage(ChatColor.RED + "You do not have permission minigame.reload");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("list")){
				if(player.hasPermission("minigame.list")){
					List<String> mglist = getConfig().getStringList("minigames");
					String minigames = "";
					
					if(args.length == 1){
						for(int i = 0; i < mglist.size(); i++){
							minigames += mglist.get(i);
							if(i != mglist.size() - 1){
								minigames += ", ";
							}
						}
					}
					player.sendMessage(ChatColor.GRAY + minigames);
				}
				else{
					player.sendMessage(ChatColor.RED + "You do not have permission minigame.list");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("toggletimer") && args.length == 2){
				if(player.hasPermission("minigame.toggletimer")){
					Minigame mgm = mdata.getMinigame(args[1]);
					if(mgm != null){
						if(mgm.getMpTimer() != null){
							if(mgm.getMpTimer().isPaused()){
								mgm.getMpTimer().resumeTimer();
								player.sendMessage(ChatColor.GRAY + "Resumed " + mgm.getName() + "'s countdown timer.");
							}
							else{
								mgm.getMpTimer().pauseTimer(player.getName() + " forced countdown pause.");
								player.sendMessage(ChatColor.GRAY + "Paused " + mgm.getName() + "'s countdown timer. (" + mgm.getMpTimer().getPlayerWaitTimeLeft() + "s)");
							}
						}
						else{
							player.sendMessage(ChatColor.RED + "Error: This minigame does not have a timer running!");
						}
					}
					else{
						player.sendMessage(ChatColor.RED + "Error: The Minigame " + args[1] + " does not exist!");
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public PlayerData getPlayerData(){
		return pdata;
	}
	
	public MySQL getSQL(){
		return sql;
	}
}
