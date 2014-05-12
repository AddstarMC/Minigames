package com.pauldavdesign.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.TreasureHuntTimer;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class HintCommand implements ICommand{

	@Override
	public String getName() {
		return "hint";
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
		return "Hints a player to the whereabouts of a treasure hunt treasure. If more than one, the name of the Minigame must be entered. (Will be listed)";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame hint [Minigame Name]"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to view a hint!";
	}

	@Override
	public String getPermission() {
		return "minigame.treasure.hint";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		Player player = (Player)sender;
		if(args != null){
			Minigame mgm = plugin.mdata.getMinigame(args[0]);
			
			if(mgm != null && mgm.getThTimer() != null && mgm.getType() == MinigameType.TREASURE_HUNT){
				TreasureHuntTimer treasure = mgm.getThTimer();
				if(treasure.getChestInWorld() && treasure.getTreasureFound() == false){
					treasure.hints(player);
				}
				else{
					player.sendMessage(ChatColor.GRAY + mgm.getName(false) + " is currently not running.");
				}
			}
			else if(mgm == null || mgm.getType() != MinigameType.TREASURE_HUNT){
				player.sendMessage(ChatColor.RED + "There is no treasure hunt running by the name \"" + args[0] + "\"");
			}
		}
		else{
			List<Minigame> mgs = new ArrayList<Minigame>();
			for(Minigame mg : plugin.mdata.getAllMinigames().values()){
				if(mg.getType() != null && mg.getType() == MinigameType.TREASURE_HUNT){
					mgs.add(mg);
				}
			}
			if(!mgs.isEmpty()){
				if(mgs.size() > 1){
					player.sendMessage(ChatColor.LIGHT_PURPLE + "Currently running Treasure Hunts:");
					String treasures = "";
					for(int i = 0; i < mgs.size(); i++){
						treasures += mgs.get(i).getName(false);
						if(i != mgs.size() - 1){
							treasures += ", ";
						}
					}
					player.sendMessage(ChatColor.GRAY + treasures);
				}
				else{
					TreasureHuntTimer treasure = mgs.get(0).getThTimer();
					if(treasure.getChestInWorld() && treasure.getChestInWorld() && !treasure.getTreasureFound()){
						treasure.hints(player);
					}
					else{
						player.sendMessage(ChatColor.GRAY + mgs.get(0).getName(false) + " is currently not running.");
					}
				}
			}
			else if(mgs.isEmpty()){
				player.sendMessage(ChatColor.LIGHT_PURPLE + "There are no Treasure Hunt minigames currently running.");
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1){
			List<String> mgs = new ArrayList<String>();
			for(Minigame mg : plugin.mdata.getAllMinigames().values()){
				if(mg.getType() == MinigameType.TREASURE_HUNT)
					mgs.add(mg.getName(false));
			}
			return MinigameUtils.tabCompleteMatch(mgs, args[0]);
		}
		return null;
	}

}
