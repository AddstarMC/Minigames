package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.commands.ICommand;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class SetUnlimitedAmmoCommand implements ICommand {

	@Override
	public String getName() {
		return "unlimitedammo";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"infammo"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Allows unlimited snowballs or eggs to be thrown.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> unlimitedammo <true/false>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to enable unlimited ammo!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.unlimitedammo";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			Boolean bool = Boolean.parseBoolean(args[0]);
			minigame.setUnlimitedAmmo(bool);
			if(bool){
				sender.sendMessage(ChatColor.GRAY + "Unlimited ammo has been turned on for " + minigame);
			}
			else{
				sender.sendMessage(ChatColor.GRAY + "Unlimited ammo has been turned off for " + minigame);
			}
			return true;
		}
		return false;
	}

}
