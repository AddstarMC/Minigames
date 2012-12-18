package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class HelpCommand implements ICommand{

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return null;
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to use the help command!";
	}

	@Override
	public String getPermission() {
		return "minigame.help";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		Player player = null;
		if(sender instanceof Player){
			player = (Player)sender;
		}
		sender.sendMessage(ChatColor.GREEN + "List of Minigame commands");
		sender.sendMessage(ChatColor.BLUE + "/minigame");
		sender.sendMessage(ChatColor.GRAY + "The default command (alias /mgm)");
		if(player == null || player.hasPermission("minigame.join")){
			sender.sendMessage(ChatColor.BLUE + "/minigame join <minigame>");
			sender.sendMessage(ChatColor.GRAY + "Joins a minigame");
		}
		if(player == null || player.hasPermission("minigame.quit")){
			sender.sendMessage(ChatColor.BLUE + "/minigame quit");
			sender.sendMessage("Quits your current minigame");
			if(player == null || player.hasPermission("minigame.quit.other")){
				sender.sendMessage("Optionally add a player at the end to force quit that player");
			}
		}
		if(player == null || player.hasPermission("minigame.end")){
			sender.sendMessage(ChatColor.BLUE + "/minigame end [Player]");
			sender.sendMessage("Ends yours or another players Minigame (Debug Only)");
		}
		if(player == null || player.hasPermission("minigame.revert")){
			sender.sendMessage(ChatColor.BLUE + "/minigame revert");
			sender.sendMessage("Reverts you to the last checkpoint in a minigame (alias /mgm r)");
		}
		if(player == null || player.hasPermission("minigame.delete")){
			sender.sendMessage(ChatColor.BLUE + "/minigame delete <Minigame>");
			sender.sendMessage("Deletes a Minigame permanently");
		}
		if(player == null || player.hasPermission("minigame.sregen")){
			sender.sendMessage(ChatColor.BLUE + "/minigame sregen <Minigame>");
			sender.sendMessage("Regenerates a Minigames spleef floor");
		}
		if(player == null || player.hasPermission("minigame.restoreinv")){
			sender.sendMessage(ChatColor.BLUE + "/minigame restoreinv <Player>");
			sender.sendMessage("Restores a players inventory if lost in the Minigame due to a crash (or bug)");
		}
		if(player == null || player.hasPermission("minigame.hint")){
			sender.sendMessage(ChatColor.BLUE + "/minigame hint <minigame>");
			sender.sendMessage("Gives you a hint for a treasure hunt minigame");
		}
		if(player == null || player.hasPermission("minigame.toggletimer")){
			sender.sendMessage(ChatColor.BLUE + "/minigame toggletimer <Minigame>");
			sender.sendMessage("Toggles a Minigames countdown timer (Pauses and Unpauses)");
		}
		if(player == null || player.hasPermission("minigame.list")){
			sender.sendMessage(ChatColor.BLUE + "/minigame list");
			sender.sendMessage("Gives you a list of all Minigames");
		}
		if(player == null || player.hasPermission("minigame.reload")){
			sender.sendMessage(ChatColor.BLUE + "/minigame reload");
			sender.sendMessage("Reloads all Minigame files.");
		}
		
		sender.sendMessage(ChatColor.BLUE + "/minigame set <Minigame> <parameter>...");
		sender.sendMessage("Modifies a Minigame, type \"/minigame set\" to view a list of parameters");
		return true;
	}

}
