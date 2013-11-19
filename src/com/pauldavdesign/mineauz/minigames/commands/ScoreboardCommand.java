package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
		return new String[] {"/minigame scoreboard <Minigame> <Result Type> <asc/desc> [limit]"};
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
					if(args[2].matches("(asc)|(desc)")){
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

}
