package au.com.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;

public class GameOverModule extends MinigameModule{
	
	private final ConfigPropertyContainer properties;
	private IntegerProperty timer = new IntegerProperty(0, "gameOver.timer");
	private BooleanProperty invincible = new BooleanProperty(false, "gameOver.invincible");
	private BooleanProperty humiliation = new BooleanProperty(false, "gameOver.humiliation");
	private BooleanProperty interact = new BooleanProperty(false, "gameOver.interact");
	
	private List<MinigamePlayer> winners = new ArrayList<MinigamePlayer>();
	private List<MinigamePlayer> losers = new ArrayList<MinigamePlayer>();
	private int task = -1;

	public GameOverModule(Minigame mgm) {
		super(mgm);
		
		properties = new ConfigPropertyContainer();
		properties.addProperty(timer);
		properties.addProperty(invincible);
		properties.addProperty(humiliation);
		properties.addProperty(interact);
	}

	@Override
	public String getName() {
		return "GameOver";
	}

	@Override
	public ConfigPropertyContainer getProperties() {
		return properties;
	}

	@Override
	public boolean useSeparateConfig() {
		return false;
	}

	@Override
	public void addEditMenuOptions(Menu menu) {
		Menu m = new Menu(6, "Game Over Settings");
		m.addItem(new MenuItemTime("Time Length", Material.WATCH, timer, 0, Integer.MAX_VALUE));
		m.addItem(new MenuItemBoolean("Invincibility", Material.ENDER_PEARL, invincible));
		m.addItem(new MenuItemBoolean("Humiliation Mode", "Losers are stripped;of weapons and can't kill", Material.DIAMOND_SWORD, humiliation));
		m.addItem(new MenuItemBoolean("Allow Interact", Material.STONE_PLATE, interact));
		
		menu.addItem(new MenuItemSubMenu("Game Over Settings", Material.WOOD_DOOR, m));
	}

	@Deprecated
	public static GameOverModule getMinigameModule(Minigame minigame){
		return (GameOverModule) minigame.getModule(GameOverModule.class);
	}
	
	public void startEndGameTimer(){
		getMinigame().broadcast(MinigameUtils.formStr("minigame.gameOverQuit", timer.getValue()), MessageType.Normal);
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
		
		if(timer.getValue() > 0){
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
			}, timer.getValue() * 20);
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
		timer.setValue(amount);
	}
	
	public int getTimer(){
		return timer.getValue();
	}
	
	public boolean isInvincible(){
		return invincible.getValue();
	}
	
	public void setInvincible(boolean bool){
		invincible.setValue(bool);
	}
	
	public boolean isHumiliationMode(){
		return humiliation.getValue();
	}
	
	public void setHumiliationMode(boolean bool){
		humiliation.setValue(bool);
	}
	
	public boolean isInteractAllowed(){
		return interact.getValue();
	}
	
	public void setInteractAllowed(boolean bool){
		interact.setValue(bool);
	}

}
