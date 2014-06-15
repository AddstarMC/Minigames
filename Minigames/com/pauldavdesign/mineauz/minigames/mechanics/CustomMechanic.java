package com.pauldavdesign.mineauz.minigames.mechanics;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.gametypes.MultiplayerType;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.Team;
import com.pauldavdesign.mineauz.minigames.minigame.modules.TeamsModule;

public class CustomMechanic extends GameMechanicBase{

	@Override
	public String getMechanic() {
		return "custom";
	}

	@Override
	public void balanceTeam(List<MinigamePlayer> players, Minigame minigame) {
		if(minigame.isTeamGame()){
			boolean sorted = false;
			for(MinigamePlayer ply : players){
				if(ply.getTeam() == null){
					Team smt = null;
					for(Team t : TeamsModule.getMinigameModule(minigame).getTeams()){
						if(smt == null || (t.getPlayers().size() < smt.getPlayers().size() && t.getPlayers().size() != t.getMaxPlayers()))
							smt = t;
					}
					if(smt == null){
						pdata.quitMinigame(ply, false);
						ply.sendMessage(MinigameUtils.getLang("minigame.full"), "error");
					}
					smt.addPlayer(ply);
					ply.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", smt.getChatColor() + smt.getDisplayName()), null);
					mdata.sendMinigameMessage(minigame, 
							MinigameUtils.formStr("player.team.autobalance.minigameMsg", 
									ply.getName(), smt.getChatColor() + smt.getDisplayName()), null, ply);
				}
			}
			
			while(!sorted){
				Team smt = null;
				Team lgt = null;
				for(Team t : TeamsModule.getMinigameModule(minigame).getTeams()){
					if(smt == null || (t.getPlayers().size() < smt.getPlayers().size() - 1 && !t.isFull()))
						smt = t;
					if((lgt == null || (t.getPlayers().size() > lgt.getPlayers().size() && !t.isFull())) && t != smt)
						lgt = t;
				}
				if(smt != null && lgt != null && lgt.getPlayers().size() - smt.getPlayers().size() > 1){
					MinigamePlayer pl = lgt.getPlayers().get(0);
					MultiplayerType.switchTeam(minigame, pl, smt);
					pl.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", smt.getChatColor() + smt.getDisplayName()), null);
					mdata.sendMinigameMessage(minigame, 
							MinigameUtils.formStr("player.team.autobalance.minigameMsg", 
									pl.getName(), smt.getChatColor() + smt.getDisplayName()), null, pl);
				}
				else{
					sorted = true;
				}
			}
		}
	}
	
	
	@EventHandler
	public void playerAutoBalance(PlayerDeathEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getMinigame().isTeamGame()){
			Minigame mgm = ply.getMinigame();
			
			if(mgm.getScoreType().equals("custom")){
				Team smt = null;
				Team lgt = ply.getTeam();
				for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
					if(smt == null || t.getPlayers().size() < smt.getPlayers().size() - 1)
						smt = t;
				}
				if(lgt.getPlayers().size() - smt.getPlayers().size() > 1){
					MultiplayerType.switchTeam(mgm, ply, smt);
					ply.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", smt.getChatColor() + smt.getDisplayName()), null);
					mdata.sendMinigameMessage(mgm, 
							MinigameUtils.formStr("player.team.autobalance.minigameMsg", 
									ply.getName(), smt.getChatColor() + smt.getDisplayName()), null, ply);
				}
			}
		}
	}
	
	@Override
	public boolean displaySettings(Menu menu){
		return false;
	}
}
