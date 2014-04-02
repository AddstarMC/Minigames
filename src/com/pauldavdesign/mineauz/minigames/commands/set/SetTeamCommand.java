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

public class SetTeamCommand implements ICommand {

	@Override
	public String getName() {
		return "team";
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
		return "Adds, removes and modifies a team for a specific Minigame.";
	}

	@Override
	public String[] getParameters() {
		return new String[] {"add", "rename", "remove", "list"};
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> team add <TeamColor> [Display Name]",
				"/minigame set <Minigame> team rename <TeamColor> <Display Name>",
				"/minigame set <Minigame> team remove <TeamColor>"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to modify Minigame teams.";
	}

	@Override
	public String getPermission() {
		return "minigame.set.team";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			if(args[0].equalsIgnoreCase("add")){
				if(args.length >= 2){
					TeamColor col = TeamColor.matchColor(args[1]);
					String name = null;
					if(col != null){
						if(args.length > 2){
							name = "";
							for(int i = 2; i < args.length; i++){
								name += args[i];
								if(i != args.length - 1)
									name += " ";
							}
						}
						if(name != null){
							minigame.addTeam(col, name);
							sender.sendMessage(ChatColor.GRAY + "Added " + MinigameUtils.capitalize(col.toString()) + 
									" team to " + minigame.getName(false) + " with the display name " + name);
						}
						else{
							minigame.addTeam(col);
							sender.sendMessage(ChatColor.GRAY + "Added " + MinigameUtils.capitalize(col.toString()) + 
									" team to " + minigame.getName(false));
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + "Invalid team color! Valid options:");
						List<String> cols = new ArrayList<String>(TeamColor.values().length);
						for(TeamColor c : TeamColor.values()){
							cols.add(c.toString());
						}
						sender.sendMessage(MinigameUtils.listToString(cols));
					}
				}
				else{
					sender.sendMessage(ChatColor.RED + "Valid team color options:");
					List<String> cols = new ArrayList<String>(TeamColor.values().length);
					for(TeamColor c : TeamColor.values()){
						cols.add(c.toString());
					}
					sender.sendMessage(MinigameUtils.listToString(cols));
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("list")){
				List<String> teams = new ArrayList<String>(minigame.getTeams().size());
				for(Team t : minigame.getTeams()){
					teams.add(t.getChatColor() + t.getColor().toString() + ChatColor.GRAY + 
							"(" + t.getChatColor() + t.getDisplayName() + ChatColor.GRAY + ")");
				}
				String teamsString = "";
				for(String t : teams){
					teamsString += t;
					if(!t.equals(teams.get(teams.size() - 1)))
						teamsString += ", ";
				}
				sender.sendMessage(ChatColor.GRAY + "List of teams in " + minigame.getName(false) + ":");
				sender.sendMessage(teamsString);
				return true;
			}
			else if(args[0].equalsIgnoreCase("remove")){
				if(args.length >= 2){
					TeamColor col = TeamColor.matchColor(args[1]);
					if(col != null){
						if(minigame.hasTeam(col)){
							minigame.removeTeam(col);
							sender.sendMessage(ChatColor.GRAY + "Removed " + MinigameUtils.capitalize(col.toString()) + " from " + minigame.getName(false));
						}
						else{
							sender.sendMessage(ChatColor.RED + minigame.getName(false) + " does not have the team " + MinigameUtils.capitalize(col.toString()));
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + "Invalid team color! Valid options:");
						List<String> cols = new ArrayList<String>(TeamColor.values().length);
						for(Team c : minigame.getTeams()){
							cols.add(c.getColor().toString());
						}
						sender.sendMessage(MinigameUtils.listToString(cols));
					}
				}
				else{
					sender.sendMessage(ChatColor.RED + "Valid teams:");
					List<String> cols = new ArrayList<String>(TeamColor.values().length);
					for(Team c : minigame.getTeams()){
						cols.add(c.getColor().toString());
					}
					sender.sendMessage(MinigameUtils.listToString(cols));
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("rename")){
				if(args.length > 2){
					TeamColor col = TeamColor.matchColor(args[1]);
					String name = "";
					for(int i = 2; i < args.length; i++){
						name += args[i];
						if(i != args.length - 1)
							name += " ";
					}
					if(col != null){
						if(minigame.hasTeam(col)){
							minigame.getTeam(col).setDisplayName(name);
							sender.sendMessage(ChatColor.GRAY + "Set " + MinigameUtils.capitalize(col.toString()) + " display name to " + name + " for " + minigame.getName(false));
						}
						else{
							sender.sendMessage(ChatColor.RED + minigame.getName(false) + " does not have the team " + MinigameUtils.capitalize(col.toString()));
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + "Invalid team color! Valid options:");
						List<String> cols = new ArrayList<String>(TeamColor.values().length);
						for(Team c : minigame.getTeams()){
							cols.add(c.getColor().toString());
						}
						sender.sendMessage(MinigameUtils.listToString(cols));
					}
				}
				else{
					sender.sendMessage(ChatColor.RED + "Valid teams:");
					List<String> cols = new ArrayList<String>(TeamColor.values().length);
					for(Team c : minigame.getTeams()){
						cols.add(c.getColor().toString());
					}
					sender.sendMessage(MinigameUtils.listToString(cols));
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1)
			return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("add;rename;remove;list"), args[0]);
		else if(args.length == 2){
			if(args[0].equalsIgnoreCase("add")){
				List<String> cols = new ArrayList<String>(TeamColor.values().length);
				for(TeamColor c : TeamColor.values()){
					cols.add(c.toString());
				}
				return MinigameUtils.tabCompleteMatch(cols, args[1]);
			}
			else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rename")){
				List<String> cols = new ArrayList<String>(TeamColor.values().length);
				for(Team c : minigame.getTeams()){
					cols.add(c.getColor().toString());
				}
				return MinigameUtils.tabCompleteMatch(cols, args[1]);
			}
		}
		return null;
	}

}
