package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetLoadoutCommand implements ICommand {

	@Override
	public String getName() {
		return "loadout";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Creates, edits, deletes and lists loadouts. Custom loadouts can be equipt via [Loadout] signs.\n" +
				"Typing \"ME\" in capital letters in the add command will add all the items in your inventory for that loadout.\n" +
				"Typing \"SELECTED\" or \"SLOT\" in capital letters in the add command will add the item you are holding to the loadout.\n" +
				"Note: Loadout names are case sensitive.";
	}

	@Override
	public String[] getParameters() {
		return new String[] {"create", "delete", "usepermissions", "add", "remove", "clear", "list"};
	}

	@Override
	public String[] getUsage() {
		return new String[] {
				"/minigame set <Minigame> loadout add <Item Name / ID> [Quantity] [-l loadoutName]",
				"/minigame set <Minigame> loadout add <ME / SELECTED / SLOT>",
				"/minigame set <Minigame> loadout remove <Item Name / ID> [-l loadoutName]", 
				"/minigame set <Minigame> loadout clear [-l loadoutName]",
				"/minigame set <Minigame> loadout create <loadoutName>",
				"/minigame set <Minigame> loadout delete <loadoutName>",
				"/minigame set <Minigame> loadout usepermissions <loadoutName> <true/false>",
				"/minigame set <Minigame> loadout list"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to edit a Minigames loadouts!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.loadout";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			if(args[0].equalsIgnoreCase("add") && args.length >= 2){
				String loadout = "default";
				int argloadout = 0;
				int quantity = 1;
				for(String arg : args){
					if(arg.equalsIgnoreCase("-l")){
						argloadout++;
						loadout = args[argloadout];
						break;
					}
					argloadout++;
				}
				
				if(args[1].equals("ME")){
					if(sender instanceof Player){
						Player player = (Player)sender;
						
						for(ItemStack item : player.getInventory().getContents()){
							if(loadout.equals("default")){
								if(item != null){
									minigame.getDefaultPlayerLoadout().addItemToLoadout(item);
								}
							}
							else{
								if(minigame.hasLoadout(loadout)){
									if(item != null){
										minigame.getLoadout(loadout).addItemToLoadout(item);
									}
								}
								else{
									sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in " + minigame);
									return true;
								}
							}
						}
						
						for(ItemStack item : player.getInventory().getArmorContents()){
							if(loadout.equals("default")){
								if(item.getType() != Material.AIR){
									minigame.getDefaultPlayerLoadout().addItemToLoadout(item);
								}
							}
							else{
								if(minigame.hasLoadout(loadout)){
									minigame.getLoadout(loadout).addItemToLoadout(item);
								}
								else{
									sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in " + minigame);
									return true;
								}
							}
						}
						if(minigame.hasLoadout(loadout)){
							sender.sendMessage(ChatColor.GRAY + "Using your inventory as the " + loadout + " loadout for " + minigame);
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + "You must be a player to use the \"ME\" variable!");
					}
					return true;
				}
				else if(args[1].equals("SELECTED") || args[1].equals("SLOT")){
					if(sender instanceof Player){
						Player player = (Player)sender;
						
						ItemStack item = player.getItemInHand();
						
						if(item.getType() != Material.AIR){
							if(loadout.equals("default")){
								minigame.getDefaultPlayerLoadout().addItemToLoadout(item);
							}
							else{
								if(minigame.hasLoadout(loadout)){
									minigame.getLoadout(loadout).addItemToLoadout(item);
								}
								else{
									sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in " + minigame);
									return true;
								}
							}
							
							if(minigame.hasLoadout(loadout)){
								sender.sendMessage(ChatColor.GRAY + "Added " + MinigameUtils.getItemStackName(item) + " to the " + loadout + " loadout for " + minigame);
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
					if(args.length == 5 || args.length == 3){
						if(args[2].matches("[0-9]+")){
							quantity = Integer.parseInt(args[2]);
						}
						else{
							return false;
						}
					}
					
					ItemStack item = MinigameUtils.stringToItemStack(args[1], quantity);
					if(item.getType() != Material.AIR){
						if(loadout.equals("default")){
							minigame.getDefaultPlayerLoadout().addItemToLoadout(item);
							sender.sendMessage(ChatColor.GRAY + "Added " + quantity + " of item " + MinigameUtils.getItemStackName(item) + " to " + minigame);
						}
						else{
							if(minigame.hasLoadout(loadout)){
								minigame.getLoadout(loadout).addItemToLoadout(item);
								sender.sendMessage(ChatColor.GRAY + "Added " + quantity + " of item " + MinigameUtils.getItemStackName(item) + " to the " + loadout + " loadout in " + minigame);
							}
							else{
								sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in " + minigame);
							}
						}
						return true;
					}
					else{
						sender.sendMessage(ChatColor.RED + args[1] + " is an invalid item!");
						return true;
					}
				}
					
			}
			else if(args[0].equalsIgnoreCase("remove") && args.length >= 2){
				String loadout = "default";
				int argloadout = 0;
				for(String arg : args){
					if(arg.equalsIgnoreCase("-l")){
						argloadout++;
						loadout = args[argloadout];
						break;
					}
					argloadout++;
				}
				
				if(loadout.equals("default")){
					minigame.getDefaultPlayerLoadout().removeItemFromLoadout(MinigameUtils.stringToItemStack(args[1], 1));
					sender.sendMessage(ChatColor.GRAY + "Removed " + MinigameUtils.getItemStackName(MinigameUtils.stringToItemStack(args[1], 1)) + " from " + minigame);
				}
				else{
					if(minigame.hasLoadout(loadout)){
						minigame.getLoadout(loadout).removeItemFromLoadout(MinigameUtils.stringToItemStack(args[1], 1));
						sender.sendMessage(ChatColor.GRAY + "Removed " + MinigameUtils.getItemStackName(MinigameUtils.stringToItemStack(args[1], 1)) + " from the " + loadout + " loadout from " + minigame);
					}
					else{
						sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in " + minigame);
					}
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("clear")){
				String loadout = "default";
				int argloadout = 0;
				for(String arg : args){
					if(arg.equalsIgnoreCase("-l")){
						argloadout++;
						loadout = args[argloadout];
						break;
					}
					argloadout++;
				}
				
				if(loadout.equals("default")){
					minigame.getDefaultPlayerLoadout().clearLoadout();
					sender.sendMessage(ChatColor.GRAY + "Cleared all items from " + minigame);
					
				}
				else{
					if(minigame.hasLoadout(loadout)){
						minigame.getLoadout(loadout).clearLoadout();
						sender.sendMessage(ChatColor.GRAY + "Cleared all items in the " + loadout + " loadout from " + minigame);
					}
					else{
						sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in " + minigame);
					}
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("create") && args.length >= 2){
				if(!minigame.hasLoadout(args[1])){
					minigame.addLoadout(args[1]);
					sender.sendMessage(ChatColor.GRAY + "Added the " + args[1] + " loadout to " + minigame);
				}
				else{
					sender.sendMessage(ChatColor.RED + "There is already a loadout called " + args[1] + " in " + minigame);
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("delete") && args.length >= 2){
				if(minigame.hasLoadout(args[1])){
					minigame.deleteLoadout(args[1]);
					sender.sendMessage(ChatColor.GRAY + "Deleted the " + args[1] + " loadout from " + minigame);
				}
				else{
					sender.sendMessage(ChatColor.RED + "There is no loadout called " + args[1] + " in " + minigame);
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("list")){
				String list = "default";
				for(String loadout : minigame.getLoadouts()){
					list += ", " + loadout;
				}
				sender.sendMessage(ChatColor.GRAY + "List of loadouts:");
				sender.sendMessage(ChatColor.GRAY + list);
				return true;
			}
			else if(args[0].equalsIgnoreCase("usepermissions") && args.length >= 3){
				boolean bool = Boolean.parseBoolean(args[2]);
				if(minigame.hasLoadout(args[1])){
					minigame.getLoadout(args[1]).setUsePermissions(bool);
					if(bool){
						sender.sendMessage(ChatColor.GRAY + args[1] + " now uses the permission: minigame.loadout." + args[1].toLowerCase());
					}
					else{
						sender.sendMessage(ChatColor.GRAY + args[1] + " no longer needs a permission to use.");
					}
				}
				else{
					sender.sendMessage(ChatColor.RED + "There is no loadout by the name " + args[1]);
				}
				return true;
			}
		}
		return false;
	}

}
