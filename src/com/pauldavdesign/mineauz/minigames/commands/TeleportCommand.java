package com.pauldavdesign.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class TeleportCommand implements ICommand {

	@Override
	public String getName() {
		return "teleport";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"tp"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Teleports a defined player to specific coordinates, another player or a specific Minigame point. " + 
				"Supports the use of ~ in coordinates to teleport them relative to where they are standing. " +
				"\n Eg: \"~ ~5 ~\" will teleport a player 5 blocks above their current position.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {
				"/minigame teleport <Player> <x> <y> <z>",
				"/minigame teleport <Player> Start [id] [team]",
				"/minigame teleport <Player> Checkpoint",
				"/minigame teleport <Player> <Player>"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to teleport players!";
	}

	@Override
	public String getPermission() {
		return "minigame.teleport";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			List<Player> plys = plugin.getServer().matchPlayer(args[0]);
			MinigamePlayer ply = null;
			if(!plys.isEmpty()){
				ply = plugin.pdata.getMinigamePlayer(plys.get(0));
			}
			else{
				sender.sendMessage(ChatColor.RED + "No player found by the name " + args[0] + "!");
				return true;
			}
			
			if(args.length == 4 && args[1].matches("~?(-?[0-9]+)|~") && args[2].matches("~?(-?[0-9]+)|~") && args[3].matches("~?(-?[0-9]+)|~")){
				double x = 0;
				double y = 0;
				double z = 0;
				
				if(args[1].contains("~")){
					if(args[1].equals("~"))
						x = ply.getPlayer().getLocation().getX();
					else
						x = ply.getPlayer().getLocation().getX() + Double.parseDouble(args[1].replace("~", ""));
				}
				else{
					x = Double.parseDouble(args[1]);
				}
				
				if(args[2].contains("~")){
					if(args[2].equals("~"))
						y = ply.getPlayer().getLocation().getY();
					else
						y = ply.getPlayer().getLocation().getY() + Double.parseDouble(args[2].replace("~", ""));
				}
				else{
					y = Double.parseDouble(args[2]);
				}
				
				if(args[3].contains("~")){
					if(args[3].equals("~"))
						z = ply.getPlayer().getLocation().getZ();
					else
						z = ply.getPlayer().getLocation().getZ() + Double.parseDouble(args[3].replace("~", ""));
				}
				else{
					z = Double.parseDouble(args[3]);
				}
				
				sender.sendMessage(ChatColor.GRAY + "Teleported " + ply.getName() + " to assigned coordinates.");
				ply.teleport(new Location(ply.getPlayer().getWorld(), x, y, z, ply.getPlayer().getLocation().getYaw(), ply.getPlayer().getLocation().getPitch()));
				return true;
			}
			else if(args.length >= 2 && args[1].equalsIgnoreCase("start")){
				if(ply.isInMinigame()){
					int pos = 0;
					String team = "none";
					if(args.length >= 3 && args[2].matches("[0-9]+") && !args[2].equals("0")){
						pos = Integer.parseInt(args[2]) - 1;
					}
					
					if(args.length == 4 && args[3].matches("red|blue") && !ply.getMinigame().getStartLocationsBlue().isEmpty() && !ply.getMinigame().getStartLocationsRed().isEmpty()){
						team = args[3];
					}
					
					if(pos > ply.getMinigame().getStartLocations().size() && team.equals("none")){
						pos = 0;
					}
					else if(team.equals("red") && pos > ply.getMinigame().getStartLocationsRed().size()){
						pos = 0;
					}
					else if(team.equals("blue") && pos > ply.getMinigame().getStartLocationsBlue().size()){
						pos = 0;
					}
					
					if(!team.equals("none")){
						if(team.equals("red")){
							ply.teleport(ply.getMinigame().getStartLocationsRed().get(pos));
							sender.sendMessage(ChatColor.GRAY + "Teleported " + ply.getName() + " to red start position " + (pos + 1) + ".");
						}
						else{
							ply.teleport(ply.getMinigame().getStartLocationsBlue().get(pos));
							sender.sendMessage(ChatColor.GRAY + "Teleported " + ply.getName() + " to blue start position " + (pos + 1) + ".");
						}
					}
					else{
						ply.teleport(ply.getMinigame().getStartLocations().get(pos));
						sender.sendMessage(ChatColor.GRAY + "Teleported " + ply.getName() + " to start position " + (pos + 1) + ".");
					}
				}
				else{
					sender.sendMessage(ChatColor.RED + ply.getName() + " is not in a Minigame!");
				}
				return true;
			}
			else if(args.length == 2 && args[1].equalsIgnoreCase("checkpoint")){
				if(ply.isInMinigame()){
					ply.teleport(ply.getCheckpoint());
					sender.sendMessage(ChatColor.GRAY + "Teleported " + ply.getName() + " to their checkpoint.");
				}
				else{
					sender.sendMessage(ChatColor.RED + ply.getName() + " is not in a Minigame!");
				}
				return true;
			}
			else if(args.length == 2){
				plys = plugin.getServer().matchPlayer(args[1]);
				MinigamePlayer ply2 = null;
				
				if(!plys.isEmpty()){
					ply2 = plugin.pdata.getMinigamePlayer(plys.get(0));
				}
				else{
					sender.sendMessage(ChatColor.RED + "No player found by the name " + args[1] + "!");
					return true;
				}
				
				ply.teleport(ply2.getPlayer().getLocation());
				sender.sendMessage(ChatColor.GRAY + "Teleported " + ply.getName() + " to " + ply2.getName() + ".");
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1){
			List<String> pl = new ArrayList<String>();
			for(Player p : plugin.getServer().getOnlinePlayers()){
				pl.add(p.getName());
			}
			return MinigameUtils.tabCompleteMatch(pl, args[0]);
		}
		else if(args.length == 2){
			List<String> pl = new ArrayList<String>(plugin.getServer().getOnlinePlayers().length + 2);
			for(Player ply : plugin.getServer().getOnlinePlayers()){
				pl.add(ply.getName());
			}
			pl.add("Start");
			pl.add("Checkpoint");
			return MinigameUtils.tabCompleteMatch(pl, args[1]);
		}
		return null;
	}

}
