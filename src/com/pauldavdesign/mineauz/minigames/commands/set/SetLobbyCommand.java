package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.commands.ICommand;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class SetLobbyCommand implements ICommand{

	@Override
	public String getName() {
		return "lobby";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Sets the lobby position of a Minigame to where you are standing.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> lobby"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set the Minigames Lobby Position!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.lobby";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		minigame.setLobbyPosition(((Player)sender).getLocation());
		sender.sendMessage(ChatColor.GRAY + "Lobby position has been set for " + minigame);
		return true;
	}
}
