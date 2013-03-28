package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetAutoPotionCommand implements ICommand {

	@Override
	public String getName() {
		return "autopotion";
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
		return "Automatically applies all potion effects within a loadout to the player upon equipping that loadout for an unlimited amount of time.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> autopotion <true/false>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to change potion auto equipping in a Minigame!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.autopotion";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			boolean bool = Boolean.parseBoolean(args[0]);
			minigame.setAutoEquipPotion(bool);
			
			if(bool){
				sender.sendMessage(ChatColor.GRAY + "Auto potion equipping has been enabled for " + minigame);
			}
			else{
				sender.sendMessage(ChatColor.GRAY + "Auto potion equipping has been disabled for " + minigame);
			}
			return true;
		}
		return false;
	}

}
