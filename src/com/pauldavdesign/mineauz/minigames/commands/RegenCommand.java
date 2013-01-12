package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class RegenCommand implements ICommand{

	@Override
	public String getName() {
		return "regen";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"sregen", "regenerate"};
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Regenerates anything that is a saved block for that Minigame.\nEg: Spleef floor or restore blocks";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame regen <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to regenerate a Minigame!";
	}

	@Override
	public String getPermission() {
		return "minigame.regen";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			Minigame mgm = plugin.mdata.getMinigame(args[0]);
			
			if(mgm != null){
				if(mgm.getType().equals("spleef")){
					//SpleefFloorGen floor = new SpleefFloorGen(mgm.getSpleefFloor1(), mgm.getSpleefFloor2());
					//floor.regenFloor(mgm.getSpleefFloorMaterial(), true);
					sender.sendMessage(ChatColor.GRAY + "Regenerating " + mgm.getName() + "'s spleef floor");
				}
				
				if(mgm.hasRestoreBlocks()){
					//mdata.restoreMinigameBlocks(mgm);
					sender.sendMessage(ChatColor.GRAY + "Regenerating " + mgm.getName() + "'s restore blocks");
				}
			}
			else{
				sender.sendMessage(ChatColor.RED + "Error: There is no Minigame by the name " + args[0]);
			}
			return true;
		}
		return false;
	}

}
