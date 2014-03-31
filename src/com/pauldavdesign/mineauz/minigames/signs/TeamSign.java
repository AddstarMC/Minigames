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
import com.pauldavdesign.mineauz.minigames.minigame.Team;
import com.pauldavdesign.mineauz.minigames.minigame.TeamColor;

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
		if(TeamColor.matchColor(event.getLine(2)) != null ||
				event.getLine(2).equalsIgnoreCase("neutral")){
			if(event.getLine(2).equalsIgnoreCase("neutral")){
				event.setLine(2, ChatColor.GRAY + "Neutral");
			}
			else{
				TeamColor col = TeamColor.matchColor(event.getLine(2));
				event.setLine(2, col.getColor() + MinigameUtils.capitalize(col.toString().replace("_", " ")));
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
				if(player.getTeam() != matchTeam(mgm, sign.getLine(2))){
					if(mgm.isNotWaitingForPlayers() && !sign.getLine(2).equals(ChatColor.GRAY + "Neutral")){
						Team sm = null;
						Team nt = matchTeam(mgm, sign.getLine(2));
						for(Team t : mgm.getTeams()){
							if(sm == null || t.getPlayers().size() < sm.getPlayers().size())
								sm = t;
						}
						if(nt.getPlayers().size() - sm.getPlayers().size() < 2){
							TeamsType.switchTeam(mgm, player, nt);
							plugin.mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.assign.joinAnnounce", player.getName(), nt.getChatColor() + nt.getDisplayName()), null, player);
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("player.team.assign.joinTeam", nt.getChatColor() + nt.getDisplayName()));
						}
						else{
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.team.noUnbalance"));
						}
						
						player.getPlayer().damage(player.getPlayer().getHealth());
					}
					else if(sign.getLine(2).equals(ChatColor.GRAY + "Neutral") || matchTeam(mgm, sign.getLine(2)) != player.getTeam()){
						Team cur = player.getTeam();
						Team nt = matchTeam(mgm, sign.getLine(2));
						if(nt != null){
							if(nt.getPlayers().size() - cur.getPlayers().size() < 2){
								TeamsType.switchTeam(mgm, player, nt);
								plugin.mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.assign.joinAnnounce", player.getName(), nt.getChatColor() + nt.getDisplayName()), null, player);
								player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("player.team.assign.joinTeam", nt.getChatColor() + nt.getDisplayName()));
							}
							else{
								player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.team.noUnbalance"));
							}
						}
						else{
							if(player.getTeam() != null){
								player.removeTeam();
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void signBreak(Sign sign, MinigamePlayer player) {
		
	}
	
	private Team matchTeam(Minigame mgm, String text){
		TeamColor col = TeamColor.matchColor(ChatColor.stripColor(text).replace(" ", "_"));
		if(mgm.hasTeam(col))
			return mgm.getTeam(col);
		return null;
	}

}
