package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetSpleefFloorMatCommand implements ICommand{

	@Override
	public String getName() {
		return "spleeffloormat";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"sfloormat"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Changes the material the spleef floor will use. (Default: Snow block)";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> spleeffloormat <Block Name / ID>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to change a Minigames spleef floor material!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.spleeffloormat";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			ItemStack floormat = MinigameUtils.stringToItemStack(args[0], 1);
			if(floormat != null && floormat.getType().isBlock()){
				minigame.setSpleefFloorMaterial(floormat.getType());
			}
			else{
				sender.sendMessage(ChatColor.RED + "The floor material must be a block!");
			}
			return true;
		}
		return false;
	}
	
}
