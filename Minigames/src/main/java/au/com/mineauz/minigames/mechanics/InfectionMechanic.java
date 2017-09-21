package au.com.mineauz.minigames.mechanics;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.InfectionModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

public class InfectionMechanic extends GameMechanicBase{

	@Override
	public String getMechanic() {
		return "infection";
	}

	@Override
	public EnumSet<MinigameType> validTypes() {
		return EnumSet.of(MinigameType.MULTIPLAYER);
	}
	
	@Override
	public boolean checkCanStart(Minigame minigame, MinigamePlayer caller){
		if(!minigame.isTeamGame() || 
				TeamsModule.getMinigameModule(minigame).getTeams().size() != 2 || 
				!TeamsModule.getMinigameModule(minigame).hasTeam(TeamColor.RED) || 
				!TeamsModule.getMinigameModule(minigame).hasTeam(TeamColor.BLUE)){
			if(caller != null)
				caller.sendMessage(MinigameUtils.getLang("minigame.error.noInfection"), "error");
			return false;
		}
		return true;
	}

	@Override
	public void balanceTeam(List<MinigamePlayer> players, Minigame minigame) {
		for(int i = 0; i < players.size(); i++){
			MinigamePlayer ply = players.get(i);
			Team red = TeamsModule.getMinigameModule(minigame).getTeam(TeamColor.RED);
			Team blue = TeamsModule.getMinigameModule(minigame).getTeam(TeamColor.BLUE);
			Team team = ply.getTeam();
			
			if(team == blue){
				if(red.getPlayers().size() < Math.ceil(players.size() * 
						(((Integer)InfectionModule.getMinigameModule(minigame).getInfectedPercent()).doubleValue() / 100d)) && !red.isFull()){
					MultiplayerType.switchTeam(minigame, ply, red);
					players.get(i).sendMessage(String.format(red.getAssignMessage(), red.getChatColor() + red.getDisplayName()), null);
					mdata.sendMinigameMessage(minigame, String.format(red.getGameAssignMessage(), players.get(i).getName(), red.getChatColor() + red.getDisplayName()), null, players.get(i));
				}
			}
			else if(team == null){
				if(red.getPlayers().size() < Math.ceil(players.size() *
						(((Integer)InfectionModule.getMinigameModule(minigame).getInfectedPercent()).doubleValue() / 100d)) && !red.isFull()){
					red.addPlayer(ply);
					players.get(i).sendMessage(String.format(red.getAssignMessage(), red.getChatColor() + red.getDisplayName()), null);
					mdata.sendMinigameMessage(minigame, String.format(red.getGameAssignMessage(), players.get(i).getName(), red.getChatColor() + red.getDisplayName()), null, players.get(i));
				}
				else if(!blue.isFull()){
					blue.addPlayer(ply);
					ply.sendMessage(String.format(blue.getAssignMessage(), blue.getChatColor() + blue.getDisplayName()), null);
					mdata.sendMinigameMessage(minigame, String.format(blue.getGameAssignMessage(), players.get(i).getName(), blue.getChatColor() + blue.getDisplayName()), null, players.get(i));
				}
				else{
					pdata.quitMinigame(ply, false);
					ply.sendMessage(MinigameUtils.getLang("minigame.full"), "error");
				}
			}
		}
	}
	
	@Override
	public MinigameModule displaySettings(Minigame minigame){
		return InfectionModule.getMinigameModule(minigame);
	}

	@Override
	public void startMinigame(Minigame minigame, MinigamePlayer caller) {
	}

	@Override
	public void stopMinigame(Minigame minigame, MinigamePlayer caller) {
	}

	@Override
	public void joinMinigame(Minigame minigame, MinigamePlayer player) {
	}

	@Override
	public void quitMinigame(Minigame minigame, MinigamePlayer player,
			boolean forced) {
		if(InfectionModule.getMinigameModule(minigame).isInfectedPlayer(player)){
			InfectionModule.getMinigameModule(minigame).removeInfectedPlayer(player);
		}
	}

	@Override
	public void endMinigame(Minigame minigame, List<MinigamePlayer> winners,
			List<MinigamePlayer> losers) {
		List<MinigamePlayer> wins = new ArrayList<MinigamePlayer>(winners);
		for(MinigamePlayer ply : wins){
			if(InfectionModule.getMinigameModule(minigame).isInfectedPlayer(ply)){
				winners.remove(ply);
				losers.add(ply);
				InfectionModule.getMinigameModule(minigame).removeInfectedPlayer(ply);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void playerDeath(PlayerDeathEvent event){
		MinigamePlayer player = pdata.getMinigamePlayer(event.getEntity());
		if(player == null) return;
		if(player.isInMinigame()){
			Minigame mgm = player.getMinigame();
			if(mgm.isTeamGame() && mgm.getMechanicName().equals("infection")){
				Team blue = TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.BLUE);
				Team red = TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.RED);
				if(blue.getPlayers().contains(player)){
					if(!red.isFull()){
						MultiplayerType.switchTeam(mgm, player, red);
						InfectionModule.getMinigameModule(mgm).addInfectedPlayer(player);
						if(event.getEntity().getKiller() != null){
							MinigamePlayer killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
							killer.addScore();
							mgm.setScore(killer, killer.getScore());
						}
						player.resetScore();
						mgm.setScore(player, player.getScore());
						
						if(mgm.getLives() != player.getDeaths()){
							mdata.sendMinigameMessage(mgm, String.format(red.getGameAssignMessage(), player.getName(), red.getChatColor() + red.getDisplayName()), "error", null);
						}
						if(blue.getPlayers().isEmpty()){
							List<MinigamePlayer> w;
							List<MinigamePlayer> l;
							w = new ArrayList<MinigamePlayer>(red.getPlayers());
							l = new ArrayList<MinigamePlayer>();
							pdata.endMinigame(mgm, w, l);
						}
					}
					else{
						pdata.quitMinigame(player, false);
					}
				}
				else{
					if(event.getEntity().getKiller() != null){
						MinigamePlayer killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
						killer.addScore();
						mgm.setScore(killer, killer.getScore());
					}
				}
			}
		}
	}
}
