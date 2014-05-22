package com.pauldavdesign.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

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
		return "Ends the game a player is currently playing. If the game is a team game, the whole team will win. " +
				"This can also be used to end a specific team from a game, an example is shown in the usage section.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame end [Player]",
				"/minigame end <TeamName> <Minigame>"};
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
				if(ply.getMinigame().getType() != MinigameType.SINGLEPLAYER){
					List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(1);
					List<MinigamePlayer> l = new ArrayList<MinigamePlayer>(ply.getMinigame().getPlayers().size());
					w.add(ply);
					l.addAll(ply.getMinigame().getPlayers());
					l.remove(ply);
					
					plugin.pdata.endMinigame(ply.getMinigame(), w, l);
					sender.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " to win the Minigame.");
				}
				else{
					plugin.pdata.endMinigame(ply);
					sender.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " to win the Minigame.");
				}
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
					if(ply.getMinigame().getType() != MinigameType.SINGLEPLAYER){
						List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(1);
						List<MinigamePlayer> l = new ArrayList<MinigamePlayer>(ply.getMinigame().getPlayers().size());
						w.add(ply);
						l.addAll(ply.getMinigame().getPlayers());
						l.remove(ply);
						
						plugin.pdata.endMinigame(ply.getMinigame(), w, l);
						sender.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " to win the Minigame.");
					}
					else{
						plugin.pdata.endMinigame(ply);
						sender.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " to win the Minigame.");
					}
				}
				else if(args.length >= 2 && teamnum != -1 && plugin.mdata.hasMinigame(args[1])){
					if(plugin.mdata.getMinigame(args[1]).hasPlayers()){
						List<MinigamePlayer> w;
						List<MinigamePlayer> l;
						if(teamnum == 0){
							w = new ArrayList<MinigamePlayer>(minigame.getRedTeam().size());
							l = new ArrayList<MinigamePlayer>(minigame.getBlueTeam().size());
							for(OfflinePlayer pl : minigame.getRedTeam()){
								w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
							}
							for(OfflinePlayer pl : ply.getMinigame().getBlueTeam()){
								l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
							}
						}
						else{
							l = new ArrayList<MinigamePlayer>(minigame.getRedTeam().size());
							w = new ArrayList<MinigamePlayer>(minigame.getBlueTeam().size());
							for(OfflinePlayer pl : minigame.getRedTeam()){
								l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
							}
							for(OfflinePlayer pl : minigame.getBlueTeam()){
								w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
							}
						}
						plugin.pdata.endMinigame(minigame, w, l);
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

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1){
			List<String> plt = new ArrayList<String>(plugin.getServer().getOnlinePlayers().length + 2);
			for(Player pl : plugin.getServer().getOnlinePlayers()){
				plt.add(pl.getName());
			}
			plt.add("red");
			plt.add("blue");
			return MinigameUtils.tabCompleteMatch(plt, args[0]);
		}
		else if(args.length == 2 && args[0].matches("red|blue")){
			List<String> mgs = new ArrayList<String>(plugin.mdata.getAllMinigames().keySet());
			return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
		}
		return null;
	}

}
