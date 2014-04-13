package com.pauldavdesign.mineauz.minigames.signs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.Team;
import com.pauldavdesign.mineauz.minigames.minigame.TeamColor;
import com.pauldavdesign.mineauz.minigames.minigame.modules.TeamsModule;

public class ScoreSign implements MinigameSign{

	@Override
	public String getName() {
		return "score";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.score";
	}

	@Override
	public String getCreatePermissionMessage() {
		return MinigameUtils.getLang("sign.score.createPermission");
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.score";
	}

	@Override
	public String getUsePermissionMessage() {
		return MinigameUtils.getLang("sign.score.usePermission");
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		if(event.getLine(2).matches("[0-9]+")){
			event.setLine(1, ChatColor.GREEN + "Score");
			if(TeamColor.matchColor(event.getLine(3)) != null){
				TeamColor col = TeamColor.matchColor(event.getLine(3));
				event.setLine(3, col.getColor() + MinigameUtils.capitalize(col.toString()));
			}
			else
				event.setLine(3, "");
			return true;
		}
		return false;
	}

	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		if(player.isInMinigame()){
			Minigame mg = player.getMinigame();
			int score = Integer.parseInt(sign.getLine(2));
			if(mg.getType() != MinigameType.TEAMS){
				if(player.hasClaimedScore(sign.getLocation())){
					player.sendMessage(MinigameUtils.getLang("sign.score.alreadyUsed"), "error");
					return true;
				}
				player.addScore(score);
				mg.setScore(player, player.getScore());
				if(mg.getMaxScore() != 0 && mg.getMaxScorePerPlayer() <= player.getScore()){
					Minigames.plugin.pdata.endMinigame(player);
				}
				player.addClaimedScore(sign.getLocation());
			}
			else{
				TeamColor steam = TeamColor.matchColor(ChatColor.stripColor(sign.getLine(3)));
				Team pteam = player.getTeam();
				if(steam == null || !TeamsModule.getMinigameModule(mg).hasTeam(steam) || pteam.getColor() == steam){
					if(Minigames.plugin.mdata.hasClaimedScore(mg, sign.getLocation(), 0)){
						player.sendMessage(MinigameUtils.getLang("sign.score.alreadyUsedTeam"), "error");
						return true;
					}
					player.addScore(score);
					mg.setScore(player, player.getScore());
					
					pteam.addScore(score);
					Minigames.plugin.mdata.addClaimedScore(mg, sign.getLocation(), 0);
					if(mg.getMaxScore() != 0 && mg.getMaxScorePerPlayer() <= pteam.getScore()){
						List<MinigamePlayer> winners = new ArrayList<MinigamePlayer>(pteam.getPlayers());
						List<MinigamePlayer> losers = new ArrayList<MinigamePlayer>(mg.getPlayers().size() - pteam.getPlayers().size());
						for(Team t : TeamsModule.getMinigameModule(mg).getTeams()){
							if(t != pteam)
								losers.addAll(t.getPlayers());
						}
						Minigames.plugin.pdata.endMinigame(mg, winners, losers);
					}
				}
			}
		}
		return true;
	}

	@Override
	public void signBreak(Sign sign, MinigamePlayer player) {
		//Eh...
		
	}

}
