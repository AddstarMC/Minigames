package com.pauldavdesign.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.MinigameSave;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class DeleteCommand implements ICommand{

	@Override
	public String getName() {
		return "delete";
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
		return "Deletes a Minigame from existance. It will be gone forever! (A very long time)";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame delete <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to delete Minigames!";
	}

	@Override
	public String getPermission() {
		return "minigame.delete";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			Minigame mgm = plugin.mdata.getMinigame(args[0]);
			
			if(mgm != null){
				MinigameSave save = new MinigameSave(mgm.getName(false), "config");
				
				if(save.getConfig().get(mgm.getName(false)) != null){
					save.deleteFile();
					List<String> ls = plugin.getConfig().getStringList("minigames");
					ls.remove(mgm.getName(false));
					plugin.getConfig().set("minigames", ls);
					plugin.mdata.removeMinigame(mgm.getName(false));
					plugin.saveConfig();
					sender.sendMessage(ChatColor.RED + "The minigame " + mgm.getName(false) + " has been removed");
				}
				else {
					sender.sendMessage(ChatColor.RED + "That minigame does not exist!");
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1){
			List<String> mgs = new ArrayList<String>(Minigames.plugin.mdata.getAllMinigames().keySet());
			return MinigameUtils.tabCompleteMatch(mgs, args[0]);
		}
		return null;
	}

}
