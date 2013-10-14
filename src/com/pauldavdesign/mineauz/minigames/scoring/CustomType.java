package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamDMMinigame;

public class CustomType extends ScoreTypeBase{

	@Override
	public String getType() {
		return "custom";
	}

	@Override
	public void balanceTeam(List<MinigamePlayer> players, Minigame minigame) {
		for(int i = 0; i < players.size(); i++){
			if(minigame.getType().equals("teamdm") && minigame.getScoreType().equals("custom")){
				int team = -1;
				if(minigame.getBlueTeam().contains(players.get(i))){
					team = 1;
				}
				else if(minigame.getRedTeam().contains(players.get(i))){
					team = 0;
				}
				
				if(team == 1){
					if(minigame.getRedTeam().size() < minigame.getBlueTeam().size() - 1){
						minigame.getBlueTeam().remove(players.get(i));
						minigame.addRedTeamPlayer(players.get(i));
						team = 0;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.RED + "Red Team"), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.autobalance.minigameMsg", players.get(i).getName(), ChatColor.RED + "Red Team"), null, players.get(i));
					}
				}
				else if(team == 0){
					if(minigame.getBlueTeam().size() < minigame.getRedTeam().size() - 1){
						minigame.getRedTeam().remove(players.get(i));
						minigame.addBlueTeamPlayer(players.get(i));
						team = 1;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.BLUE + "Blue Team"), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.autobalance.minigameMsg", players.get(i).getName(), ChatColor.BLUE + "Blue Team"), null, players.get(i));
					}
				}
				else{
					if(minigame.getRedTeam().size() <= minigame.getBlueTeam().size()){
						minigame.addRedTeamPlayer(players.get(i));
						team = 0;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.RED + "Red Team"), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.autobalance.minigameMsg", players.get(i).getName(), ChatColor.RED + "Red Team"), null, players.get(i));
					}
					else if(minigame.getBlueTeam().size() <= minigame.getRedTeam().size()){
						minigame.addBlueTeamPlayer(players.get(i));
						team = 1;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.BLUE + "Blue Team"), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.autobalance.minigameMsg", players.get(i).getName(), ChatColor.BLUE + "Blue Team"), null, players.get(i));
					}
				}
			}
		}
	}
	
	
	@EventHandler
	public void playerAutoBalance(PlayerDeathEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getMinigame().getType().equals("teamdm")){
			int pteam = 0;
			if(ply.getMinigame().getBlueTeam().contains(ply.getPlayer())){
				pteam = 1;
			}
			final Minigame mgm = ply.getMinigame();
			
			if(mgm.getScoreType().equals("custom")){
				if(pteam == 1){
					if(mgm.getRedTeam().size() < mgm.getBlueTeam().size() - 1){
						TeamDMMinigame.switchTeam(mgm, ply);
						ply.sendMessage(MinigameUtils.formStr("player.autobalance.plyMsg", ChatColor.RED + "Red Team"), null);
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.autobalance.minigameMsg", ply.getName(), ChatColor.RED + "Red Team"), null, ply);
					}
				}
				else{
					if(mgm.getBlueTeam().size() < mgm.getRedTeam().size()  - 1){
						TeamDMMinigame.switchTeam(mgm, ply);
						ply.sendMessage(String.format(MinigameUtils.getLang("player.autobalance.plyMsg"), ChatColor.BLUE + "Blue Team"), null);
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.autobalance.minigameMsg", ply.getName(), ChatColor.BLUE + "Blue Team"), null, ply);
					}
				}
			}
		}
	}
}
