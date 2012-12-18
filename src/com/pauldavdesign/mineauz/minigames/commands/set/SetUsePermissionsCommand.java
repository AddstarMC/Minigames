package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetUsePermissionsCommand implements ICommand {

	@Override
	public String getName() {
		return "usepermissions";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"useperms"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Sets whether a player needs a specific permission to join a Minigame. " +
				"\nPermissions as follows: \n\"minigame.join.<minigame>\" - must be all lower case";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> usepermissions <true/false>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to change whether this minigame uses permissions (Permissionception?)";
	}

	@Override
	public String getPermission() {
		return "minigame.set.usepermissions";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			boolean bool = Boolean.parseBoolean(args[0]);
			minigame.setUsePermissions(bool);
			sender.sendMessage(ChatColor.GRAY + "Use permissions has been set to " + bool + " for " + minigame.getName());
			return true;
		}
		return false;
	}

}
