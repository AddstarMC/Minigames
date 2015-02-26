package au.com.mineauz.minigames.mechanics;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.StartMinigameEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

public class LivesMechanic extends GameMechanicBase{

	@Override
	public String getMechanic() {
		return "lives";
	}

	@Override
	public EnumSet<MinigameType> validTypes() {
		return EnumSet.of(MinigameType.MULTIPLAYER);
	}
	
	@Override
	public void addRequiredModules(Minigame minigame) {
		minigame.addModule(TeamsModule.class);
	}

	@Override
	public boolean checkCanStart(Minigame minigame) throws IllegalStateException {
		if(minigame.getLives() > 0){
			return true;
		}
		throw new IllegalStateException("The Minigame must have more than 0 lives to use this type");
	}

	@Override
	public MinigameModule displaySettings(Minigame minigame) {
		return null;
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
	private void minigameStart(StartMinigameEvent event){
		if(event.getMinigame().getMechanicName().equals(getMechanic())){
			final List<MinigamePlayer> players = event.getPlayers();
			final Minigame minigame = event.getMinigame();
			for(MinigamePlayer player : players){
				player.setScore(minigame.getLives());
				minigame.setScore(player, minigame.getLives());
			}
		}
	}
	
	@EventHandler
	private void playerDeath(PlayerDeathEvent event){
		MinigamePlayer ply = Minigames.plugin.getPlayerData().getMinigamePlayer(event.getEntity());
		if(ply == null)return;
		if(ply.isInMinigame() && ply.getMinigame().getMechanicName().equals(getMechanic())){
			ply.addScore(-1);
			ply.getMinigame().setScore(ply, ply.getScore());
		}
	}

}
