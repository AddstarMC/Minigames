package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;

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
				"Note: Loadout names are case sensitive.";
	}

	@Override
	public String[] getParameters() {
		return new String[] {"create", "delete", "add", "remove", "clear", "list"};
	}

	@Override
	public String[] getUsage() {
		return new String[] {
				"/minigame globalloadout <loadoutName> add <Item Name / ID / ME> [Quantity]",
				"/minigame globalloadout <loadoutName> remove <Item Name / ID>", 
				"/minigame globalloadout <loadoutName> clear",
				"/minigame globalloadout <loadoutName> create",
				"/minigame globalloadout <loadoutName> delete",
				"/minigame globalloadout <loadoutName> usepermissions <true/false>",
				"/minigame globalloadout list"
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
			if(args.length >= 3 && args[1].equalsIgnoreCase("add")){
				String loadout = args[0];
				int quantity = 1;
				
				if(!args[2].equals("ME")){
					if(args.length == 4){
						if(args[3].matches("[0-9]+")){
							quantity = Integer.parseInt(args[2]);
						}
						else{
							return false;
						}
					}
					
					ItemStack item = MinigameUtils.stringToItemStack(args[1], quantity);
					if(item != null){
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
						return false;
					}
				}
				else{
					if(sender instanceof Player){
						Player player = (Player)sender;
						
						for(ItemStack item : player.getInventory().getContents()){
							if(mdata.hasLoadout(loadout)){
								if(item != null){
									mdata.getLoadout(loadout).addItemToLoadout(item);
								}
							}
							else{
								sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in global loadouts!");
								break;
							}
						}
						
						for(ItemStack item : player.getInventory().getArmorContents()){
							if(mdata.hasLoadout(loadout)){
								mdata.getLoadout(loadout).addItemToLoadout(item);
							}
							else{
								sender.sendMessage(ChatColor.RED + "There is no loadout by the name of \"" + loadout + "\" in global loadouts!");
								break;
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
					
			}
			else if(args.length >= 3 && args[0].equalsIgnoreCase("remove")){
				String loadout = args[0];
				
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
				String loadout = args[0];
				
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
			else if(args[0].equalsIgnoreCase("delete") && args.length >= 2){
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
				String list = "default";
				for(String loadout : mdata.getLoadouts()){
					list += ", " + loadout;
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
		}
		return false;
	}

}
