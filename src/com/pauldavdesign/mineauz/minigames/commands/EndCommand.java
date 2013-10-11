package com.pauldavdesign.mineauz.minigames.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;

public class EndCommand implements ICommand{

	@Override
	public String getName() {
		return "end";
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
		return "Ends a players Minigame.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame end [Player]"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to force end your Minigame!";
	}

	@Override
	public String getPermission() {
		return "minigame.end";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args == null && sender instanceof Player){
			MinigamePlayer ply = plugin.pdata.getMinigamePlayer((Player)sender);
			if(ply.isInMinigame()){
				plugin.pdata.endMinigame(ply);
			}
			else {
				sender.sendMessage(ChatColor.RED + "Error: You are not in a minigame!");
			}
			return true;
		}
		else if(args != null){
			Player player = null;
			if(sender instanceof Player){
				player = (Player)sender;
			}
			if(player == null || player.hasPermission("minigame.end.other")){
				List<Player> players = plugin.getServer().matchPlayer(args[0]);
				MinigamePlayer ply = null;
				int teamnum = -1;
				if(players.isEmpty() && !args[0].equalsIgnoreCase("red") && !args[0].equalsIgnoreCase("blue")){
					sender.sendMessage(ChatColor.RED + "No player found by the name " + args[0]);
					return true;
				}
				else if(args[0].equalsIgnoreCase("red")){
					teamnum = 0;
				}
				else if(args[0].equalsIgnoreCase("blue")){
					teamnum = 1;
				}
				else{
					ply = plugin.pdata.getMinigamePlayer(players.get(0));
				}
				
				if(ply != null && ply.isInMinigame()){
					if(ply.getMinigame().getType().equals("teamdm")){
						int team = 0;
						for(OfflinePlayer pl : ply.getMinigame().getBlueTeam()){
							if(pl.getName().equals(ply.getName())){
								team = 1;
								break;
							}
						}
						plugin.pdata.endTeamMinigame(team, ply.getMinigame());
						sender.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " and their team to win the Minigame.");
					}
					else{
						plugin.pdata.endMinigame(ply);
						sender.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " to win the Minigame.");
					}
				}
				else if(args.length >= 2 && teamnum != -1 && plugin.mdata.hasMinigame(args[1])){
					if(plugin.mdata.getMinigame(args[1]).hasPlayers()){
						plugin.pdata.endTeamMinigame(teamnum, plugin.mdata.getMinigame(args[1]));
						if(teamnum == 1){
							sender.sendMessage(ChatColor.GRAY + "You forced " + ChatColor.RED + "Red Team" + ChatColor.WHITE + " to win the Minigame.");
						}
						else{
							sender.sendMessage(ChatColor.GRAY + "You forced " + ChatColor.BLUE + "Blue Team" + ChatColor.WHITE + " to win the Minigame.");
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + "This Minigame has no players!");
					}
				}
				else{
					sender.sendMessage(ChatColor.RED + "This player is not playing a Minigame.");
				}
			}
			else if(player != null){
				sender.sendMessage(ChatColor.RED + "Error: You don't have permission to force end another players Minigame!");
				sender.sendMessage(ChatColor.RED + "minigame.end.other");
			}
			return true;
		}
		return false;
	}

}
