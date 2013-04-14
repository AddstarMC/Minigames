package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamDMMinigame;

public class TeamSign implements MinigameSign {
	
	private Minigames plugin = Minigames.plugin;
	
	@Override
	public String getName() {
		return "Team";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.team";
	}

	@Override
	public String getCreatePermissionMessage() {
		return "You do not have permission to create a Minigame team sign!";
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.team";
	}

	@Override
	public String getUsePermissionMessage() {
		return "You do not have permission to use a Minigame team sign!";
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		event.setLine(1, ChatColor.GREEN + "Team");
		if(event.getLine(2).equalsIgnoreCase("red") || event.getLine(2).equalsIgnoreCase("r") ||
				event.getLine(2).equalsIgnoreCase("blue") || event.getLine(2).equalsIgnoreCase("b") ||
				event.getLine(2).equalsIgnoreCase("neutral")){
			if(event.getLine(2).equalsIgnoreCase("red") || event.getLine(2).equalsIgnoreCase("r")){
				event.setLine(2, ChatColor.RED + "Red");
			}
			else if(event.getLine(2).equalsIgnoreCase("blue") || event.getLine(2).equalsIgnoreCase("b")){
				event.setLine(2, ChatColor.BLUE + "Blue");
			}
			else if(event.getLine(2).equalsIgnoreCase("neutral")){
				event.setLine(2, ChatColor.GRAY + "Neutral");
			}
			return true;
		}
		event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Line 3 must be \"red\", \"blue\" or \"neutral\"!");
		return false;
	}

	@Override
	public boolean signUse(Sign sign, Player player) {
		if(plugin.pdata.playerInMinigame(player)){
			Minigame mgm = plugin.pdata.getPlayersMinigame(player);
			if(mgm.getType().equals("teamdm")){
				if(mgm.hasStarted() && !sign.getLine(2).equals(ChatColor.GRAY + "Neutral") &&
						((mgm.getRedTeam().contains(player) && sign.getLine(2).equals(ChatColor.BLUE + "Blue") || 
								(mgm.getBlueTeam().contains(player) && sign.getLine(2).equals(ChatColor.RED + "Red"))))){
					player.damage(player.getHealth());
				}
				if(mgm.getBlueTeam().contains(player)){
					if(sign.getLine(2).equals(ChatColor.RED + "Red")){
						if(mgm.getRedTeam().size() <= mgm.getBlueTeam().size()){
//							TeamDMMinigame.applyTeam(player, 0);
							TeamDMMinigame.switchTeam(mgm, player);
							plugin.mdata.sendMinigameMessage(mgm, player.getName() + " has joined " + ChatColor.RED + "Red Team.", null, player);
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have joined " + ChatColor.RED + "Red Team.");
						}
						else{
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You cannot unbalance the teams!");
						}
					}
					else if(sign.getLine(2).equals(ChatColor.GRAY + "Neutral") && !mgm.hasStarted()){
//						TeamDMMinigame.removeTeam(player);
//						mgm.getRedTeam().remove(player);
//						mgm.getBlueTeam().remove(player);
						mgm.removeRedTeamPlayer(player);
						mgm.removeBlueTeamPlayer(player);
						plugin.mdata.sendMinigameMessage(mgm, player.getName() + " will be automatically assigned to a team.", null, player);
						player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You will be automatically assigned to a team.");
					}
					return true;
				}
				else if(mgm.getRedTeam().contains(player)){
					if(sign.getLine(2).equals(ChatColor.BLUE + "Blue")){
						if(mgm.getRedTeam().size() >= mgm.getBlueTeam().size()){
//							TeamDMMinigame.applyTeam(player, 1);
							TeamDMMinigame.switchTeam(mgm, player);
							plugin.mdata.sendMinigameMessage(mgm, player.getName() + " has joined " + ChatColor.BLUE + "Blue Team.", null, player);
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have joined " + ChatColor.BLUE + "Blue Team.");
						}
						else{
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You cannot unbalance the teams!");
						}
					}
					else if(sign.getLine(2).equals(ChatColor.GRAY + "Neutral") && !mgm.hasStarted()){
//						TeamDMMinigame.removeTeam(player);
//						mgm.getRedTeam().remove(player);
						mgm.removeRedTeamPlayer(player);
//						mgm.getBlueTeam().remove(player);
						mgm.removeBlueTeamPlayer(player);
						plugin.mdata.sendMinigameMessage(mgm, player.getName() + " will be automatically assigned to a team.", null, player);
						player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You will be automatically assigned to a team.");
					}
					return true;
				}
				else{
					if(!mgm.hasStarted()){
						if(sign.getLine(2).equals(ChatColor.RED + "Red")){
							if(mgm.getRedTeam().size() <= mgm.getBlueTeam().size()){
//								TeamDMMinigame.applyTeam(player, 0);
								mgm.addRedTeamPlayer(player);
								mgm.removeBlueTeamPlayer(player);
								plugin.mdata.sendMinigameMessage(mgm, player.getName() + " has joined " + ChatColor.RED + "Red Team.", null, player);
								player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have joined " + ChatColor.RED + "Red Team.");
							}
							else{
								player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You cannot unbalance the teams!");
							}
						}
						else if(sign.getLine(2).equals(ChatColor.BLUE + "Blue")){
							if(mgm.getRedTeam().size() >= mgm.getBlueTeam().size()){
//								TeamDMMinigame.applyTeam(player, 1);
								mgm.addBlueTeamPlayer(player);
								mgm.removeRedTeamPlayer(player);
								plugin.mdata.sendMinigameMessage(mgm, player.getName() + " has joined " + ChatColor.BLUE + "Blue Team.", null, player);
								player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have joined " + ChatColor.BLUE + "Blue Team.");
							}
							else{
								player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You cannot unbalance the teams!");
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

}
