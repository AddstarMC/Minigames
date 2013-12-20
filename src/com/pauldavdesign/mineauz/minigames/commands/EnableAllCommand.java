package com.pauldavdesign.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class EnableAllCommand implements ICommand {

	@Override
	public String getName() {
		return "enableall";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"enall"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Enables all Minigames, unless it's added to exclude list.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame enableall [ExcludedMinigame]..."};
	}

	@Override
	public String getPermissionMessage() {
		return "You don't have permission to enable all Minigames!";
	}

	@Override
	public String getPermission() {
		return "minigame.enableall";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		MinigameData mdata = Minigames.plugin.mdata;
		List<Minigame> minigames = new ArrayList<Minigame>(mdata.getAllMinigames().values());
		if(args != null){
			for(String arg : args){
				if(mdata.hasMinigame(arg))
					minigames.remove(mdata.getMinigame(arg));
				else
					sender.sendMessage(ChatColor.RED + "No Minigame found by the name \"" + arg + "\"; Ignoring...");
			}
		}
		for(Minigame mg : minigames){
			mg.setEnabled(true);
			sender.sendMessage(ChatColor.GRAY + String.valueOf(minigames.size()) + " Minigames enabled!");
		}
		return true;
	}

}
