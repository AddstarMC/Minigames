package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamsType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

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
		return MinigameUtils.getLang("sign.team.createPermission");
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.team";
	}

	@Override
	public String getUsePermissionMessage() {
		return MinigameUtils.getLang("sign.team.usePermission");
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
		event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("sign.team.invalidFormat", "\"red\", \"blue\" or \"neutral\""));
		return false;
	}

	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		if(player.isInMinigame()){
			Minigame mgm = player.getMinigame();
			if(mgm.getType() == MinigameType.TEAMS){
				if(mgm.isNotWaitingForPlayers() && !sign.getLine(2).equals(ChatColor.GRAY + "Neutral") &&
						((mgm.getRedTeam().contains(player.getPlayer()) && sign.getLine(2).equals(ChatColor.BLUE + "Blue") || 
								(mgm.getBlueTeam().contains(player.getPlayer()) && sign.getLine(2).equals(ChatColor.RED + "Red"))))){
					player.getPlayer().damage(player.getPlayer().getHealth());
				}
				if(mgm.getBlueTeam().contains(player.getPlayer())){
					if(sign.getLine(2).equals(ChatColor.RED + "Red")){
						if(mgm.getRedTeam().size() <= mgm.getBlueTeam().size()){
							TeamsType.switchTeam(mgm, player);
							plugin.mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.assign.joinAnnounce", player.getName(), ChatColor.RED + "Red Team."), null, player);
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("player.team.assign.joinTeam", ChatColor.RED + "Red Team."));
						}
						else{
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.team.noUnbalance"));
						}
					}
					else if(sign.getLine(2).equals(ChatColor.GRAY + "Neutral") && !mgm.isNotWaitingForPlayers()){
						mgm.removeRedTeamPlayer(player);
						mgm.removeBlueTeamPlayer(player);
						plugin.mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("sign.team.autoAssignAnnounce", player.getName()), null, player);
						player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.team.autoAssign"));
					}
					return true;
				}
				else if(mgm.getRedTeam().contains(player.getPlayer())){
					if(sign.getLine(2).equals(ChatColor.BLUE + "Blue")){
						if(mgm.getRedTeam().size() >= mgm.getBlueTeam().size()){
							TeamsType.switchTeam(mgm, player);
							plugin.mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.assign.joinAnnounce", player.getName(), ChatColor.BLUE + "Blue Team."), null, player);
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("player.team.assign.joinTeam", ChatColor.BLUE + "Blue Team."));
						}
						else{
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.team.noUnbalance"));
						}
					}
					else if(sign.getLine(2).equals(ChatColor.GRAY + "Neutral") && !mgm.isNotWaitingForPlayers()){
						mgm.removeRedTeamPlayer(player);
						mgm.removeBlueTeamPlayer(player);
						plugin.mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("sign.team.autoAssignAnnounce", player.getName()), null, player);
						player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.team.autoAssign"));
					}
					return true;
				}
				else{
					if(!mgm.isNotWaitingForPlayers()){
						if(sign.getLine(2).equals(ChatColor.RED + "Red")){
							if(mgm.getRedTeam().size() <= mgm.getBlueTeam().size()){
								mgm.addRedTeamPlayer(player);
								mgm.removeBlueTeamPlayer(player);
								plugin.mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.assign.joinAnnounce", player.getName(), ChatColor.RED + "Red Team."), null, player);
								player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("player.team.assign.joinTeam", ChatColor.RED + "Red Team."));
							}
							else{
								player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.team.noUnbalance"));
							}
						}
						else if(sign.getLine(2).equals(ChatColor.BLUE + "Blue")){
							if(mgm.getRedTeam().size() >= mgm.getBlueTeam().size()){
								mgm.addBlueTeamPlayer(player);
								mgm.removeRedTeamPlayer(player);
								plugin.mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.assign.joinAnnounce", player.getName(), ChatColor.BLUE + "Blue Team."), null, player);
								player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("player.team.assign.joinTeam", ChatColor.BLUE + "Blue Team."));
							}
							else{
								player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.team.noUnbalance"));
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
