package com.pauldavdesign.mineauz.minigames.signs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

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
			if(event.getLine(3).matches("(red)|(blue)")){
				if(event.getLine(3).equalsIgnoreCase("red"))
					event.setLine(3, ChatColor.RED + "Red");
				else
					event.setLine(3, ChatColor.BLUE + "Blue");
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
				String steam = ChatColor.stripColor(sign.getLine(3)).toLowerCase();
				String pteam = "red";
				if(mg.getBlueTeam().contains(player.getPlayer().getPlayer()))
					pteam = "blue";
				if(steam.equals("") || pteam.equals(steam)){
					player.addScore(score);
					mg.setScore(player, player.getScore());
					List<MinigamePlayer> winners = new ArrayList<MinigamePlayer>();
					List<MinigamePlayer> losers = new ArrayList<MinigamePlayer>();
					if(pteam.equals("red")){
						if(Minigames.plugin.mdata.hasClaimedScore(mg, sign.getLocation(), 0)){
							player.sendMessage(MinigameUtils.getLang("sign.score.alreadyUsedTeam"), "error");
							return true;
						}
						mg.setRedTeamScore(mg.getRedTeamScore() + score);
						Minigames.plugin.mdata.addClaimedScore(mg, sign.getLocation(), 0);
						if(mg.getMaxScore() != 0 && mg.getMaxScorePerPlayer() <= mg.getRedTeamScore()){
							for(OfflinePlayer pl : mg.getRedTeam()){
								winners.add(Minigames.plugin.pdata.getMinigamePlayer(pl.getName()));
							}
							for(OfflinePlayer pl : mg.getBlueTeam()){
								losers.add(Minigames.plugin.pdata.getMinigamePlayer(pl.getName()));
							}
						}
					}
					else{
						if(Minigames.plugin.mdata.hasClaimedScore(mg, sign.getLocation(), 1)){
							player.sendMessage(MinigameUtils.getLang("sign.score.alreadyUsedTeam"), "error");
							return true;
						}
						mg.setBlueTeamScore(mg.getBlueTeamScore() + score);
						Minigames.plugin.mdata.addClaimedScore(mg, sign.getLocation(), 1);
						if(mg.getMaxScore() != 0 && mg.getMaxScorePerPlayer() <= mg.getBlueTeamScore()){
							for(OfflinePlayer pl : mg.getRedTeam()){
								losers.add(Minigames.plugin.pdata.getMinigamePlayer(pl.getName()));
							}
							for(OfflinePlayer pl : mg.getBlueTeam()){
								winners.add(Minigames.plugin.pdata.getMinigamePlayer(pl.getName()));
							}
						}
					}
					if(!winners.isEmpty()){
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
