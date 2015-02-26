package au.com.mineauz.minigames.mechanics;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

public class CustomMechanic extends GameMechanicBase{

	@Override
	public String getMechanic() {
		return "custom";
	}
	
	@Override
	public void addRequiredModules(Minigame minigame) {
		if (minigame.getType() == MinigameType.MULTIPLAYER) {
			minigame.addModule(TeamsModule.class);
		}
	}

	@Override
	public EnumSet<MinigameType> validTypes() {
		return EnumSet.of(MinigameType.MULTIPLAYER, MinigameType.SINGLEPLAYER);
	}
	
	@Override
	public boolean checkCanStart(Minigame minigame){
		return true;
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
	}

	@Override
	public void endMinigame(Minigame minigame, List<MinigamePlayer> winners,
			List<MinigamePlayer> losers) {
	}
	
	@EventHandler
	public void playerAutoBalance(PlayerDeathEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getMinigame().isTeamGame()){
			Minigame mgm = ply.getMinigame();
			
			if(mgm.getMechanicName().equals("custom")){
				Team smt = null;
				Team lgt = ply.getTeam();
				for(Team t : mgm.getModule(TeamsModule.class).getTeams()){
					if(smt == null || t.getPlayers().size() < smt.getPlayers().size() - 1)
						smt = t;
				}
				if(lgt.getPlayers().size() - smt.getPlayers().size() > 1){
					MultiplayerType.switchTeam(mgm, ply, smt);
					ply.sendMessage(String.format(smt.getAutobalanceMessage(), smt.getChatColor() + smt.getDisplayName()), null);
					mdata.sendMinigameMessage(mgm, 
							String.format(smt.getGameAutobalanceMessage(), 
									ply.getName(), smt.getChatColor() + smt.getDisplayName()), null, ply);
				}
			}
		}
	}
	
	@Override
	public MinigameModule displaySettings(Minigame minigame){
		return null;
	}
}
