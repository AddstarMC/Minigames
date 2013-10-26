package com.pauldavdesign.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
				"You must be standing on the block to assign it, or type \"-l\" to assign it to what you are looking at.";
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
			boolean looking = false;
			for(String arg : args){
				if(arg.equalsIgnoreCase("-l")){
					looking = true;
				}
			}
			if(args.length >= 2 && args[0].equalsIgnoreCase("add")){
				Location loc;
				if(!looking){
					loc = player.getLocation().getBlock().getLocation().clone();
					loc.setY(loc.getY() - 0.1);
				}
				else{
					List<Block> blocks = player.getLineOfSight(null, 20);
					loc = blocks.get(blocks.size() - 1).getLocation();
				}
				
				RestoreBlock rb = new RestoreBlock(args[1], loc.getBlock().getType(), loc);
				minigame.addRestoreBlock(rb);
				player.sendMessage(ChatColor.GRAY + "Saved block \"" + loc.getBlock().getType().toString() + "\" for " + minigame.getName() + " under the name " + args[1]);
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
					player.sendMessage(ChatColor.GRAY + "Removed all restore blocks from " + minigame.getName());
				}
				else{
					player.sendMessage(ChatColor.RED + "There are no restore blocks in the Minigame \"" + minigame.getName() + "\"");
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
					player.sendMessage(ChatColor.RED + "There are no restore blocks in the Minigame \"" + minigame.getName() + "\"");
				}
				return true;
			}
		}
		return false;
	}

}
