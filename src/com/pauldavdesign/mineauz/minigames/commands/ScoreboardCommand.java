package com.pauldavdesign.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.ScoreboardOrder;
import com.pauldavdesign.mineauz.minigames.minigame.ScoreboardSortThread;
import com.pauldavdesign.mineauz.minigames.minigame.ScoreboardType;

public class ScoreboardCommand implements ICommand{
	
	private Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "scoreboard";
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
		return "Displays a scoreboard of the desired Minigame, SQL must be enabled!";
	}

	@Override
	public String[] getParameters() {
		ScoreboardType[] types = ScoreboardType.values();
		String[] tStr = new String[types.length];
		for(int i = 0; i < types.length; i++){
			tStr[i] = types[i].toString().toLowerCase();
		}
		return tStr;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame scoreboard <Minigame> <Result Type> <asc/desc> [limit] [-p <PlayerName>]"};
	}

	@Override
	public String getPermissionMessage() {
		return "You don't have permission to view the scoreboard!";
	}

	@Override
	public String getPermission() {
		return "minigame.scoreboard";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null && args.length >= 3 && plugin.getSQL() != null && plugin.getSQL().getSql() != null){
			if(plugin.mdata.hasMinigame(args[0])){
				Minigame mg = plugin.mdata.getMinigame(args[0]);
				ScoreboardType type;
				try{
					type = ScoreboardType.valueOf(args[1].toUpperCase());
				}
				catch(IllegalArgumentException e){
					type = null;
				}
				
				if(type != null){
					String ply = null;
					int c = 0;
					for(String arg : args){
						if(arg.equals("-p") && args.length - 1 > c){
							ply = args[c + 1];
							break;
						}
						c++;
					}
					if(ply != null){
						if(mg.getScoreboardData().hasPlayer(ply)){
							if(args[2].matches("(asc)|(desc)")){
								ScoreboardOrder ord = ScoreboardOrder.DESCENDING;
								if(args[2].equals("asc")){
									ord = ScoreboardOrder.ASCENDING;
								}
								ScoreboardSortThread sorter = new ScoreboardSortThread(mg.getScoreboardData().getPlayers(), type, ord, 
										mg.getName(), sender);
								sorter.setSpecificPlayer(ply);
								sorter.start();
							}
							else
								sender.sendMessage(ChatColor.RED + "Order must be asc (ascending) or desc (descending)");
						}
						else
							sender.sendMessage(ChatColor.RED + ply + " does not have any data stored in " + mg.getName());
						return true;
					}
					else if(args[2].matches("(asc)|(desc)")){
						ScoreboardOrder ord = ScoreboardOrder.DESCENDING;
						if(args[2].equals("asc")){
							ord = ScoreboardOrder.ASCENDING;
						}
						
						ScoreboardSortThread sorter;
						if(args.length == 4 && args[3].matches("[0-9]+"))
							sorter = new ScoreboardSortThread(mg.getScoreboardData().getPlayers(), type, ord, mg.getName(), sender, Integer.parseInt(args[3]));
						else
							sorter = new ScoreboardSortThread(mg.getScoreboardData().getPlayers(), type, ord, mg.getName(), sender);
						
						sorter.start();
						sender.sendMessage(ChatColor.GRAY + "Preparing scoreboard...");
						return true;
					}
					else
						sender.sendMessage(ChatColor.RED + "Order must be asc (ascending) or desc (descending)");
				}
				else
					sender.sendMessage(ChatColor.RED + "No result type found by the name " + args[1]);
			}
			else
				sender.sendMessage(ChatColor.RED + "No Minigame found by the name " + args[0]);
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1){
			List<String> mgs = new ArrayList<String>(plugin.mdata.getAllMinigames().keySet());
			return MinigameUtils.tabCompleteMatch(mgs, args[0]);
		}
		else if(args.length == 2){
			List<String> ts = new ArrayList<String>(ScoreboardType.values().length);
			for(ScoreboardType t : ScoreboardType.values()){
				ts.add(t.toString());
			}
			return MinigameUtils.tabCompleteMatch(ts, args[1]);
		}
		else if(args.length == 3){
			return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("asc;desc"), args[2]);
		}
		else if(args.length >= 5 && args[args.length - 2].equals("-p")){
			List<String> pl = new ArrayList<String>();
			for(Player p : plugin.getServer().getOnlinePlayers()){
				pl.add(p.getName());
			}
			return MinigameUtils.tabCompleteMatch(pl, args[5]);
		}
		return null;
	}

}
