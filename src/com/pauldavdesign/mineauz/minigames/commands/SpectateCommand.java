package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class SpectateCommand implements ICommand {

	@Override
	public String getName() {
		return "spectate";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"spec"};
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Allows a player to force spectate a Minigame.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame spectate <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to use the spectate command!";
	}

	@Override
	public String getPermission() {
		return "minigame.spectate";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			if(plugin.mdata.hasMinigame(args[0])){
				Player ply = (Player) sender;
				Minigame mgm = plugin.mdata.getMinigame(args[0]);
				plugin.pdata.spectateMinigame(ply, mgm);
			}
			else{
				sender.sendMessage(ChatColor.RED + "No Minigame found by the name: " + args[0]);
			}
			return true;
		}
		return false;
	}

}
