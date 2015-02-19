package au.com.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;

public class GameOverModule extends MinigameModule{
	
	private IntegerFlag timer = new IntegerFlag(0, "gameOver.timer");
	private BooleanFlag invincible = new BooleanFlag(false, "gameOver.invincible");
	private BooleanFlag humiliation = new BooleanFlag(false, "gameOver.humiliation");
	private BooleanFlag interact = new BooleanFlag(false, "gameOver.interact");
	
	private List<MinigamePlayer> winners = new ArrayList<MinigamePlayer>();
	private List<MinigamePlayer> losers = new ArrayList<MinigamePlayer>();
	private int task = -1;

	public GameOverModule(Minigame mgm) {
		super(mgm);
	}

	@Override
	public String getName() {
		return "GameOver";
	}

	@Override
	public Map<String, Flag<?>> getFlags() {
		Map<String, Flag<?>> map = new HashMap<String, Flag<?>>();
		map.put(timer.getName(), timer);
		map.put(invincible.getName(), invincible);
		map.put(humiliation.getName(), humiliation);
		return map;
	}

	@Override
	public boolean useSeparateConfig() {
		return false;
	}

	@Override
	public void save(FileConfiguration config) {
	}

	@Override
	public void load(FileConfiguration config) {
	}

	@Override
	public void addEditMenuOptions(Menu menu) {
		Menu m = new Menu(6, "Game Over Settings");
		m.addItem(timer.getMenuItem("Time Length", Material.WATCH, 0, null));
		m.addItem(invincible.getMenuItem("Invincibility", Material.ENDER_PEARL));
		m.addItem(humiliation.getMenuItem("Humiliation Mode", Material.DIAMOND_SWORD, MinigameUtils.stringToList("Losers are stripped;of weapons and can't kill")));
		m.addItem(interact.getMenuItem("Allow Interact", Material.STONE_PLATE));
		
		menu.addItem(new MenuItemSubMenu("Game Over Settings", Material.WOOD_DOOR, m));
	}

	public static GameOverModule getMinigameModule(Minigame minigame){
		return (GameOverModule) minigame.getModule("GameOver");
	}
	
	public void startEndGameTimer(){
		Minigames.plugin.mdata.sendMinigameMessage(getMinigame(), MinigameUtils.formStr("minigame.gameOverQuit", timer.getFlag()), null, null);
		getMinigame().setState(MinigameState.ENDED);
		
		List<MinigamePlayer> allPlys = new ArrayList<MinigamePlayer>(winners.size() + losers.size());
		allPlys.addAll(losers);
		allPlys.addAll(winners);
		
		for(MinigamePlayer p : allPlys){
			if(!isInteractAllowed()){
				p.setCanInteract(false);
			}
			if(isHumiliationMode() && losers.contains(p)){
				p.getPlayer().getInventory().clear();
				p.getPlayer().getInventory().setHelmet(null);
				p.getPlayer().getInventory().setChestplate(null);
				p.getPlayer().getInventory().setLeggings(null);
				p.getPlayer().getInventory().setBoots(null);
				
				for(PotionEffect potion : p.getPlayer().getActivePotionEffects()){
					p.getPlayer().removePotionEffect(potion.getType());
				}
			}
			if(isInvincible()){
				p.setInvincible(true);
			}
		}
		
		if(timer.getFlag() > 0){
			if(task != -1)
				stopEndGameTimer();
			task = Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
				
				@Override
				public void run() {
					for(MinigamePlayer loser : new ArrayList<MinigamePlayer>(losers)){
						if(loser.isInMinigame())
							Minigames.plugin.pdata.quitMinigame(loser, true);
					}
					for(MinigamePlayer winner : new ArrayList<MinigamePlayer>(winners)){
						if(winner.isInMinigame())
							Minigames.plugin.pdata.quitMinigame(winner, true);
					}
					
					clearLosers();
					clearWinners();
				}
			}, timer.getFlag() * 20);
		}
	}
	
	public void stopEndGameTimer(){
		if(task != -1)
			Bukkit.getScheduler().cancelTask(task);
	}
	
	public void setWinners(List<MinigamePlayer> winners){
		this.winners.addAll(winners);
	}
	
	public void clearWinners(){
		winners.clear();
	}
	
	public List<MinigamePlayer> getWinners(){
		return winners;
	}
	
	public void setLosers(List<MinigamePlayer> losers){
		this.losers.addAll(losers);
	}
	
	public void clearLosers(){
		losers.clear();
	}
	
	public List<MinigamePlayer> getLosers(){
		return losers;
	}
	
	public void setTimer(int amount){
		timer.setFlag(amount);
	}
	
	public int getTimer(){
		return timer.getFlag();
	}
	
	public boolean isInvincible(){
		return invincible.getFlag();
	}
	
	public void setInvincible(boolean bool){
		invincible.setFlag(bool);
	}
	
	public boolean isHumiliationMode(){
		return humiliation.getFlag();
	}
	
	public void setHumiliationMode(boolean bool){
		humiliation.setFlag(bool);
	}
	
	public boolean isInteractAllowed(){
		return interact.getFlag();
	}
	
	public void setInteractAllowed(boolean bool){
		interact.setFlag(bool);
	}

}
