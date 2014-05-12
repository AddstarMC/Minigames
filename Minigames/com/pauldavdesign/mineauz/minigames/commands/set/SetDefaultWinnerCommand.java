package com.pauldavdesign.mineauz.minigames.commands.set;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.Team;
import com.pauldavdesign.mineauz.minigames.minigame.TeamColor;
import com.pauldavdesign.mineauz.minigames.minigame.modules.TeamsModule;

public class SetDefaultWinnerCommand implements ICommand {

	@Override
	public String getName() {
		return "defaultwinner";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"defwin"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Sets which team will win when the timer expires and neither team has won. (Useful for attack/defend modes of CTF) (Default: none).";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> defaultwinner <TeamColor>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set the default winner of a Minigame!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.defaultwinner";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			if(args[0].equalsIgnoreCase("none")){
				TeamsModule.getMinigameModule(minigame).setDefaultWinner(null);
				sender.sendMessage(ChatColor.GRAY + "The default winner of " + minigame + " has been set to none.");
			}
			else{
				if(TeamsModule.getMinigameModule(minigame).hasTeam(TeamColor.matchColor(args[0]))){
					TeamsModule.getMinigameModule(minigame).setDefaultWinner(TeamsModule.getMinigameModule(minigame).getTeam(TeamColor.matchColor(args[0])));
					sender.sendMessage(ChatColor.GRAY + "The default winner of " + minigame + " has been set to " + args[0] + ".");
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
			List<String> teams = new ArrayList<String>();
			for(Team t : TeamsModule.getMinigameModule(minigame).getTeams()){
				teams.add(t.getColor().toString().toLowerCase());
			}
			return MinigameUtils.tabCompleteMatch(teams, args[0]);
		}
		return null;
	}

}
