package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerLoadout;

public class GlobalLoadoutCommand implements ICommand {
	private MinigameData mdata = Minigames.plugin.mdata;

	@Override
	public String getName() {
		return "globalloadout";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"gloadout", "loadout"};
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Creates, edits, deletes and lists global loadouts. Custom loadouts can be equipt via [Loadout] signs.\n" +
				"Typing \"ME\" in capital letters in the add command will add all the items in your inventory for that loadout.\n" +
				"Typing \"SELECTED\" or \"SLOT\" in capital letters in the add command will add the item you are holding to the loadout.\n" +
				"Note: Loadout names are case sensitive.";
	}

	@Override
	public String[] getParameters() {
		return new String[] {"create", "delete", "add", "remove", "clear", "list", "addpotion", "removepotion"};
	}

	@Override
	public String[] getUsage() {
		return new String[] {
				"/minigame globalloadout <loadoutName> add <Item Name / ID> [Quantity]",
				"/minigame set <Minigame> loadout add <ME / SELECTED / SLOT>",
				"/minigame globalloadout <loadoutName> remove <Item Name / ID>", 
				"/minigame globalloadout <loadoutName> clear",
				"/minigame globalloadout <loadoutName> create",
				"/minigame globalloadout <loadoutName> delete",
				"/minigame globalloadout <loadoutName> usepermissions <true/false>",
				"/minigame globalloadout list",
				"/minigame globalloadout <loadoutName> addpotion <PotionName> <duration> <amplifier>",
				"/minigame globalloadout <loadoutName> removepotion <PotionName>"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to edit global loadouts!";
	}

	@Override
	public String getPermission() {
		return "minigame.globalloadout";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			String loadout = args[0];
			PlayerLoadout load = null;
			if(mdata.hasLoadout(loadout)){
				load = mdata.getLoadout(loadout);
			}
			else if(!args[0].equalsIgnoreCase("list") && (args.length >= 2 && !args[1].equalsIgnoreCase("create"))){
				sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in global loadouts!");
				return true;
			}
			
			if(args.length >= 3 && args[1].equalsIgnoreCase("add")){
				int quantity = 1;
				
				if(args[2].equals("ME")){
					if(sender instanceof Player){
						Player player = (Player)sender;
						
						for(ItemStack item : player.getInventory().getContents()){
							if(mdata.hasLoadout(loadout)){
								if(item != null){
									ItemStack newItem = new ItemStack(item);
									if(newItem.getAmount() == 0){
										newItem.setAmount(1);
									}
									mdata.getLoadout(loadout).addItemToLoadout(newItem);
								}
							}
							else{
								sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in global loadouts!");
								return true;
							}
						}
						
						for(ItemStack item : player.getInventory().getArmorContents()){
							if(mdata.hasLoadout(loadout)){
								if(item.getType() != Material.AIR){
									ItemStack newItem = new ItemStack(item);
									if(newItem.getAmount() == 0){
										newItem.setAmount(1);
									}
									mdata.getLoadout(loadout).addItemToLoadout(newItem);
								}
							}
							else{
								sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in global loadouts!");
								return true;
							}
						}
						if(mdata.hasLoadout(loadout)){
							sender.sendMessage(ChatColor.GRAY + "Using your inventory as the " + loadout + " loadout.");
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + "You must be a player to use the \"ME\" variable!");
					}
					return true;
				}
				else if(args[2].equals("SELECTED") || args[2].equals("SLOT")){
					if(sender instanceof Player){
						Player player = (Player)sender;
						
						ItemStack item = player.getItemInHand();
						
						if(item.getType() != Material.AIR){
							if(mdata.hasLoadout(loadout)){
								ItemStack newItem = new ItemStack(item);
								if(newItem.getAmount() == 0){
									newItem.setAmount(1);
								}
								mdata.getLoadout(loadout).addItemToLoadout(newItem);
							}
							else{
								sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in global loadouts!");
								return true;
							}
							
							if(mdata.hasLoadout(loadout)){
								sender.sendMessage(ChatColor.GRAY + "Added " + MinigameUtils.getItemStackName(item) + " to the " + loadout + " loadout in global loadouts");
							}
						}
						else{
							sender.sendMessage(ChatColor.RED + "Your hand is empty!");
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + "You must be a player to use the \"" + args[1] + "\" variable!");
					}
					return true;
				}
				else{
					if(args.length == 4){
						if(args[3].matches("[0-9]+")){
							quantity = Integer.parseInt(args[3]);
							if(quantity == 0){
								sender.sendMessage(ChatColor.RED + "The amount cannot be 0!");
								return false;
							}
						}
						else{
							sender.sendMessage(ChatColor.RED + "\"" + args[3] + "\" is not a valid number!");
							return false;
						}
					}
					
					ItemStack item = MinigameUtils.stringToItemStack(args[2], quantity);
					if(item.getType() != Material.AIR){
						if(mdata.hasLoadout(loadout)){
							mdata.getLoadout(loadout).addItemToLoadout(item);
							sender.sendMessage(ChatColor.GRAY + "Added " + quantity + " of item " + MinigameUtils.getItemStackName(item) + " to the " + loadout + " loadout in global loadouts");
						}
						else{
							sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in global loadouts!");
						}
						return true;
					}
					else{
						sender.sendMessage(ChatColor.RED + args[2] + " is an invalid item!");
						return true;
					}
				}
					
			}
			else if(args.length >= 3 && args[1].equalsIgnoreCase("remove")){
				
				if(mdata.hasLoadout(loadout)){
					mdata.getLoadout(loadout).removeItemFromLoadout(MinigameUtils.stringToItemStack(args[2], 1));
					sender.sendMessage(ChatColor.GRAY + "Removed " + MinigameUtils.getItemStackName(MinigameUtils.stringToItemStack(args[2], 1)) + " from the " + loadout + " loadout in global loadouts");
				}
				else{
					sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in global loadouts");
				}
				return true;
			}
			else if(args.length >= 2 && args[1].equalsIgnoreCase("clear")){
				if(mdata.hasLoadout(loadout)){
					mdata.getLoadout(loadout).clearLoadout();
					sender.sendMessage(ChatColor.GRAY + "Cleared all items in the " + loadout + " loadout from global loadouts");
				}
				else{
					sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in global loadouts!");
				}
				return true;
			}
			else if(args.length >= 2 && args[1].equalsIgnoreCase("create")){
				if(!mdata.hasLoadout(args[0])){
					mdata.addLoadout(args[0]);
					sender.sendMessage(ChatColor.GRAY + "Added the " + args[0] + " loadout to global loadouts");
				}
				else{
					sender.sendMessage(ChatColor.RED + "There is already a loadout called " + args[0] + " in global loadouts");
				}
				return true;
			}
			else if(args.length >= 2 && args[1].equalsIgnoreCase("delete")){
				if(mdata.hasLoadout(args[0])){
					mdata.deleteLoadout(args[0]);
					sender.sendMessage(ChatColor.GRAY + "Deleted the " + args[0] + " loadout from global loadouts");
				}
				else{
					sender.sendMessage(ChatColor.RED + "There is no loadout called " + args[0] + " in global loadouts");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("list")){
				String list = "";
				int count = 0;
				for(String lo : mdata.getLoadouts()){
					list += lo;
					count++;
					if(count != mdata.getLoadouts().size()){
						list += ", ";
					}
				}
				sender.sendMessage(ChatColor.GRAY + "List of global loadouts:");
				sender.sendMessage(ChatColor.GRAY + list);
				return true;
			}
			else if(args[1].equalsIgnoreCase("usepermissions") && args.length >= 3){
				boolean bool = Boolean.parseBoolean(args[2]);
				if(mdata.hasLoadout(args[0])){
					mdata.getLoadout(args[0]).setUsePermissions(bool);
					if(bool){
						sender.sendMessage(ChatColor.GRAY + args[0] + " now uses the permission: minigame.loadout." + args[0].toLowerCase());
					}
					else{
						sender.sendMessage(ChatColor.GRAY + args[0] + " no longer needs a permission to use.");
					}
				}
				else{
					sender.sendMessage(ChatColor.RED + "There is no global loadout by the name " + args[0]);
				}
				return true;
			}
			else if(args[1].equalsIgnoreCase("addpotion") && args.length >= 5){
				PotionEffectType potion = PotionEffectType.getByName(args[2]);
				int duration;
				int amplifier;
				
				if(args[3].matches("[0-9]+")){
					duration = Integer.parseInt(args[3]);
					duration = duration * 20;
					if(duration > 1000000){
						duration = 1000000;
					}
				}
				else{
					return false;
				}
				
				if(args[3].matches("[0-9]+")){
					amplifier = Integer.parseInt(args[4]);
					amplifier -= 1;
					if(amplifier > 1000000){
						amplifier = 1000000;
					}
					else if(amplifier < 0){
						amplifier = 0;
					}
				}
				else{
					return false;
				}
				
				if(potion != null){
					PotionEffect eff = new PotionEffect(potion, duration, amplifier, true);
					load.addPotionEffect(eff);
					sender.sendMessage(ChatColor.GRAY + "Added potion effect \"" + potion.getName().toLowerCase() + "\" to the " + loadout + " loadout");
				}
				else{
					sender.sendMessage(ChatColor.RED + "Invalid potion effect!");
				}
				return true;
			}
			else if(args[1].equalsIgnoreCase("removepotion") && args.length >= 3){
				PotionEffectType potion = PotionEffectType.getByName(args[2]);
				
				if(potion != null){
					PotionEffect eff = new PotionEffect(potion, 0, 0);
					load.removePotionEffect(eff);
					sender.sendMessage(ChatColor.GRAY + "Removed potion effect \"" + potion.getName().toLowerCase() + "\" from the " + loadout + " loadout");
				}
				else{
					sender.sendMessage(ChatColor.RED + "Invalid potion effect!");
				}
				return true;
			}
		}
		return false;
	}

}
