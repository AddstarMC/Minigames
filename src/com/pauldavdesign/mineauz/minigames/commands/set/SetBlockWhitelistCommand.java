package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetBlockWhitelistCommand implements ICommand {

	@Override
	public String getName() {
		return "blockwhitelist";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"bwl", "blockwl"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Adds, removes and changes whitelist mode on or off (off by default). " +
				"When off, it is in blacklist mode, meaning the blocks in the list are the only blocks that list can't be placed or destroyed";
	}

	@Override
	public String[] getParameters() {
		return new String[] {"add", "remove", "list", "clear"};
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				"/minigame set <Minigame> blockwhitelist <true/false>",
				"/minigame set <Minigame> blockwhitelist add <Block type>",
				"/minigame set <Minigame> blockwhitelist remove <Block type>",
				"/minigame set <Minigame> blockwhitelist list",
				"/minigame set <Minigame> blockwhitelist clear"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to edit the block whitelist!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.blockwhitelist";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			if(args[0].equalsIgnoreCase("add") && args.length >= 2){
				if(Material.matchMaterial(args[1].toUpperCase()) != null || (args[1].matches("[0-9]+") && Material.getMaterial(Integer.parseInt(args[1])) != null)){
					Material mat = null;
					if(!args[1].matches("[0-9]+")){
						mat = Material.matchMaterial(args[1].toUpperCase());
					}
					else{
						mat = Material.getMaterial(Integer.parseInt(args[1]));
					}
					
					minigame.getBlockRecorder().addWBBlock(mat);
					
					if(minigame.getBlockRecorder().getWhitelistMode()){
						sender.sendMessage(ChatColor.GRAY + "Added " + mat.toString().replace("_", " ").toLowerCase() + " to the whitelist for " + minigame);
					}
					else{
						sender.sendMessage(ChatColor.GRAY + "Added " + mat.toString().replace("_", " ").toLowerCase() + " to the blacklist for " + minigame);
					}
				}
				else{
					sender.sendMessage(ChatColor.RED + "Invalid item name or ID!");
				}
			}
			else if(args[0].equalsIgnoreCase("remove") && args.length >= 2){
				if(Material.matchMaterial(args[1].toUpperCase()) != null || (args[1].matches("[0-9]+") && Material.getMaterial(Integer.parseInt(args[1])) != null)){
					Material mat = null;
					if(!args[1].matches("[0-9]+")){
						mat = Material.matchMaterial(args[1].toUpperCase());
					}
					else{
						mat = Material.getMaterial(Integer.parseInt(args[1]));
					}
					
					minigame.getBlockRecorder().removeWBBlock(mat);
					
					if(minigame.getBlockRecorder().getWhitelistMode()){
						sender.sendMessage(ChatColor.GRAY + "Removed " + mat.toString().replace("_", " ").toLowerCase() + " from the whitelist for " + minigame);
					}
					else{
						sender.sendMessage(ChatColor.GRAY + "Removed " + mat.toString().replace("_", " ").toLowerCase() + " from the blacklist for " + minigame);
					}
				}
				else{
					sender.sendMessage(ChatColor.RED + "Invalid item name or ID!");
				}
			}
			else if(args[0].equalsIgnoreCase("clear")){
				minigame.getBlockRecorder().getWBBlocks().clear();
				if(minigame.getBlockRecorder().getWhitelistMode()){
					sender.sendMessage(ChatColor.GRAY + "Cleared all blocks from the whitelist for " + minigame);
				}
				else{
					sender.sendMessage(ChatColor.GRAY + "Cleared all blocks from the blacklist for " + minigame);
				}
			}
			else if(args[0].equalsIgnoreCase("list")){
				String blocks = "";
				boolean switchColour = false;
				for(Material block : minigame.getBlockRecorder().getWBBlocks()){
					if(switchColour){
						blocks += ChatColor.WHITE + block.toString();
						if(!block.toString().equalsIgnoreCase(minigame.getBlockRecorder().getWBBlocks().get(minigame.getBlockRecorder().getWBBlocks().size() - 1).toString())){
							blocks += ChatColor.WHITE + ", ";
						}
						switchColour = false;
					}
					else{
						blocks += ChatColor.GRAY + block.toString();
						if(!block.toString().equalsIgnoreCase(minigame.getBlockRecorder().getWBBlocks().get(minigame.getBlockRecorder().getWBBlocks().size() - 1).toString())){
							blocks += ChatColor.WHITE + ", ";
						}
						switchColour = true;
					}
				}
				if(minigame.getBlockRecorder().getWhitelistMode()){
					sender.sendMessage(ChatColor.GRAY + "All blocks on the whitelist:");
				}
				else{
					sender.sendMessage(ChatColor.GRAY + "All blocks on the blacklist:");
				}
				sender.sendMessage(blocks);
			}
			else{
				boolean bool = Boolean.parseBoolean(args[0]);
				minigame.getBlockRecorder().setWhitelistMode(bool);
				if(bool){
					sender.sendMessage(ChatColor.GRAY + "Block placement and breaking is now on whitelist mode for " + minigame);
				}
				else{
					sender.sendMessage(ChatColor.GRAY + "Block placement and breaking is now on blacklist mode for " + minigame);
				}
			}
			return true;
		}
		return false;
	}

}
