package com.pauldavdesign.mineauz.minigames.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;

public class ScoreCommand implements ICommand {

	@Override
	public String getName() {
		return "score";
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
		return "Gets, sets or adds to a player's or team's score. The Minigame name is only required if not assigning the score to a player.";
	}

	@Override
	public String[] getParameters() {
		return new String[] {"get", "set", "add"};
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame score get <Player or Team> [Minigame]",
				"/minigame score set <Player or Team> <NewScore> [Minigame]",
				"/minigame score add <Player or Team> [ExtraPoints] [Minigame]"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to interact with a Minigames score!";
	}

	@Override
	public String getPermission() {
		return "minigame.score";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null && args.length >= 2){
			int team = -1;
			MinigamePlayer ply = null;
			
			if(args[1].equalsIgnoreCase("red")){
				team = 0;
			}
			else if(args[1].equalsIgnoreCase("blue")){
				team = 1;
			}
			else{
				List<Player> plys = plugin.getServer().matchPlayer(args[1]);
				if(!plys.isEmpty()){
					ply = plugin.pdata.getMinigamePlayer(plys.get(0));
				}
				else{
					sender.sendMessage(ChatColor.RED + "No player found by the name " + args[1]);
					return true;
				}
			}
			
			
			if(args[0].equalsIgnoreCase("get") && args.length >= 2){
				
				if(ply != null){
					if(ply.isInMinigame()){
						sender.sendMessage(ChatColor.GRAY + ply.getName() + "'s Score: " + ChatColor.GREEN + ply.getScore());
					}
					else{
						sender.sendMessage(ChatColor.RED + ply.getName() + " is not playing a Minigame!");
					}
				}
				else if(team != -1){
					if(args.length >= 3){
						Minigame mg = null;
						if(plugin.mdata.hasMinigame(args[2])){
							mg = plugin.mdata.getMinigame(args[2]);
						}
						else{
							sender.sendMessage(ChatColor.RED + "No Minigame found by the name " + args[2]);
							return true;
						}
						
						if(mg.getType().equals("teamdm")){
							if(team == 0){
								sender.sendMessage(ChatColor.RED + "Red Teams " + ChatColor.GRAY + "score in " + mg.getName() + ": " 
										+ ChatColor.GREEN + mg.getRedTeamScore());
							}
							else{
								sender.sendMessage(ChatColor.BLUE + "Blue Teams " + ChatColor.GRAY + "score in " + mg.getName() + ": " 
										+ ChatColor.GREEN + mg.getBlueTeamScore());
							}
						}
						else{
							sender.sendMessage(ChatColor.RED + mg.getName() + " is not a team Minigame!");
							return true;
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + "This command requires a Minigame name as the last argument!");
					}
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("set") && args.length >= 3){
				
				int score = 0;
				
				if(args[2].matches("[0-9]+")){
					score = Integer.parseInt(args[2]);
				}
				else{
					sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
					return true;
				}
				
				if(ply != null){
					if(ply.isInMinigame()){
						ply.setScore(score);
						sender.sendMessage(ChatColor.GRAY + ply.getName() + "'s score has been set to " + score);
						
						if(ply.getMinigame().getMaxScore() != 0 && score >= ply.getMinigame().getMaxScorePerPlayer()){
							plugin.pdata.endMinigame(ply);
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + ply.getName() + " is not playing a Minigame!");
					}
				}
				else if(team != -1){
					if(args.length >= 4){
						Minigame mg = null;
						if(plugin.mdata.hasMinigame(args[3])){
							mg = plugin.mdata.getMinigame(args[3]);
						}
						else{
							sender.sendMessage(ChatColor.RED + "No Minigame found by the name " + args[2]);
							return true;
						}
						
						if(mg.getType().equals("teamdm")){
							if(team == 0){
								mg.setRedTeamScore(score);
								sender.sendMessage(ChatColor.RED + "Red Team's " + ChatColor.GRAY + " score has been set to " + score);
							}
							else{
								mg.setBlueTeamScore(score);
								sender.sendMessage(ChatColor.BLUE + "Blue Team's " + ChatColor.GRAY + " score has been set to " + score);
							}
							
							if(mg.getMaxScore() != 0 && score >= mg.getMaxScorePerPlayer()){
								plugin.pdata.endTeamMinigame(team, mg);
							}
						}
						else{
							sender.sendMessage(ChatColor.RED + mg.getName() + " is not a team Minigame!");
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + "This command requires a Minigame name as the last argument!");
					}
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("add") && args.length >= 3){
				int score = 0;
				
				if(args[2].matches("[0-9]+")){
					score = Integer.parseInt(args[2]);
				}
				else{
					score = 1;
				}
				
				if(ply != null){
					if(ply.isInMinigame()){
						ply.addScore(score);
						sender.sendMessage(ChatColor.GRAY + "Added " + score + " to " + ply.getName() + "'s score, new score: " + ply.getScore());
						
						if(ply.getMinigame().getMaxScore() != 0 && ply.getScore() >= ply.getMinigame().getMaxScorePerPlayer()){
							plugin.pdata.endMinigame(ply);
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + ply.getName() + " is not playing a Minigame!");
					}
				}
				else if(team != -1){
					Minigame mg = null;
					String mgName = null;
					
					if(args.length == 4){
						mgName = args[3];
					}
					else{
						mgName = args[2];
					}

					if(plugin.mdata.hasMinigame(mgName)){
						mg = plugin.mdata.getMinigame(mgName);
					}
					else{
						sender.sendMessage(ChatColor.RED + "No Minigame found by the name " + mgName);
						return true;
					}
					
					if(mg.getType().equals("teamdm")){
						int totalscore = 0;
						if(team == 0){
							mg.incrementRedTeamScore(score);
							sender.sendMessage(ChatColor.GRAY + "Added " + score + " to " + ChatColor.RED + "Red Team's " + ChatColor.GRAY + " score, new score: " + mg.getRedTeamScore());
							totalscore = mg.getRedTeamScore();
						}
						else{
							mg.incrementBlueTeamScore(score);
							sender.sendMessage(ChatColor.GRAY + "Added " + score + " to " + ChatColor.BLUE + "Blue Team's " + ChatColor.GRAY + " score, new score: " + mg.getBlueTeamScore());
							totalscore = mg.getBlueTeamScore();
						}
						
						if(mg.getMaxScore() != 0 && totalscore >= mg.getMaxScorePerPlayer()){
							plugin.pdata.endTeamMinigame(team, mg);
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + mg.getName() + " is not a team Minigame!");
					}
					return true;
				}
			}
		}
		return false;
	}

}
