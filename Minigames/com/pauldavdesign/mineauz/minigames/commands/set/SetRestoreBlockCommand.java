package com.pauldavdesign.mineauz.minigames.commands.set;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.RestoreBlock;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class SetRestoreBlockCommand implements ICommand {

	@Override
	public String getName() {
		return "restoreblock";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"resblock", "rblock"};
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Adds, removes, lists or clears all restore blocks in a Minigame. Note: Only needed for blocks a player won't interact with (ie: Dispensers). " +
				"You must be standing on the block to assign it.";
	}

	@Override
	public String[] getParameters() {
		return new String[] {"add", "remove", "clear", "list"};
	}

	@Override
	public String[] getUsage() {
		return new String[] {
				"/minigame set <Minigame> restoreblock add <Name>",
				"/minigame set <Minigame> restoreblock remove <Name>",
				"/minigame set <Minigame> restoreblock list",
				"/minigame set <Minigame> restoreblock clear"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You don't have permission to modify restore blocks within a Minigame!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.restoreblock";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			Player player = (Player)sender;
			if(args.length >= 2 && args[0].equalsIgnoreCase("add")){
				Location loc;
				loc = player.getLocation().getBlock().getLocation().clone();
				loc.setY(loc.getY() - 0.1);
				
				RestoreBlock rb = new RestoreBlock(args[1], loc.getBlock().getType(), loc);
				minigame.addRestoreBlock(rb);
				player.sendMessage(ChatColor.GRAY + "Saved block \"" + loc.getBlock().getType().toString() + "\" for " + minigame.getName(false) + " under the name " + args[1]);
				return true;
			}
			else if(args.length == 2 && args[0].equalsIgnoreCase("remove")){
				if(minigame.getRestoreBlocks().containsKey(args[1])){
					minigame.removeRestoreBlock(args[1]);
					player.sendMessage(ChatColor.GRAY + "Removed the " + args[1] + " restore block.");
				}
				else{
					player.sendMessage(ChatColor.RED + "There is no restore block by the name \"" + args[1] + "\"");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("clear")){
				if(minigame.hasRestoreBlocks()){
					minigame.getRestoreBlocks().clear();
					player.sendMessage(ChatColor.GRAY + "Removed all restore blocks from " + minigame.getName(false));
				}
				else{
					player.sendMessage(ChatColor.RED + "There are no restore blocks in the Minigame \"" + minigame.getName(false) + "\"");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("list")){
				if(minigame.hasRestoreBlocks()){
					String list = "";
					for(String block : minigame.getRestoreBlocks().keySet()){
						list += block + ", ";
					}
					list = list.substring(0, list.length() - 2);
					player.sendMessage(ChatColor.GRAY + "List of restore blocks:");
					player.sendMessage(ChatColor.GRAY + list);
				}
				else{
					player.sendMessage(ChatColor.RED + "There are no restore blocks in the Minigame \"" + minigame.getName(false) + "\"");
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1)
			return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("add;remove;clear;list"), args[0]);
		else if(args.length == 2 && args[0].equalsIgnoreCase("remove")){
			List<String> ls = new ArrayList<String>(minigame.getRestoreBlocks().keySet());
			return MinigameUtils.tabCompleteMatch(ls, args[1]);
		}
		return null;
	}

}
