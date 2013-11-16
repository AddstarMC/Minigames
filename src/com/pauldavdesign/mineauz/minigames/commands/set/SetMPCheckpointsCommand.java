package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.commands.ICommand;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class SetMPCheckpointsCommand implements ICommand {

	@Override
	public String getName() {
		return "allowmpcheckpoints";
	}

	@Override
	public String[] getAliases() {
		return new String[] {
				"mpcheckpoints",
				"mpcp",
				"checkpoints",
				"cp"
		};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Sets whether checkpoints should be enabled or not in a multiplayer game. Mainly used for race gametypes";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {
				"/minigame set <Minigame> allowmpcheckpoints <true / false>"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to allow checkpoints in a multiplayer game!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.mpcheckpoints";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			boolean bool = Boolean.parseBoolean(args[0]);
			minigame.setAllowMPCheckpoints(bool);
			if(bool)
				sender.sendMessage(ChatColor.GRAY + "Enabled multiplayer checkpoints in " + minigame);
			else
				sender.sendMessage(ChatColor.RED + "Disabled multiplayer checkpoints in " + minigame);
		}
		return false;
	}

}
