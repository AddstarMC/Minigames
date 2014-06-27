package com.pauldavdesign.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class StopCommand implements ICommand{

	@Override
	public String getName() {
		return "stop";
	}
	
	@Override
	public String[] getAliases(){
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Stops a currently running Global Minigame.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame stop <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to stop Global Minigames!";
	}

	@Override
	public String getPermission() {
		return "minigame.stop";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			Minigame mgm = plugin.mdata.getMinigame(args[0]);
			
			if(mgm != null && mgm.isEnabled() && mgm.getType() == MinigameType.GLOBAL){
				MinigamePlayer caller = null;
				if(sender instanceof Player)
					caller = plugin.pdata.getMinigamePlayer((Player)sender);
				plugin.mdata.stopGlobalMinigame(mgm, caller);
			}
			else if(mgm == null || mgm.getType() != MinigameType.GLOBAL){
				sender.sendMessage(ChatColor.RED + "There is no Global Minigame by the name \"" + args[0] + "\"");
			}
			else{
				sender.sendMessage(ChatColor.RED + mgm.getName(false) + " is not running!");
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		List<String> mgs = new ArrayList<String>();
		for(Minigame mg : plugin.mdata.getAllMinigames().values()){
			if(mg.getType() == MinigameType.GLOBAL)
				mgs.add(mg.getName(false));
		}
		return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
	}
}
