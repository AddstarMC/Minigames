package au.com.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.types.IntegerProperty;

public class InfectionModule extends MinigameModule{
	private final ConfigPropertyContainer properties;
	private IntegerProperty infectedPercent = new IntegerProperty(18, "infectedPercent");
	
	//Unsaved Data
	private List<MinigamePlayer> infected = new ArrayList<MinigamePlayer>();

	public InfectionModule(Minigame mgm) {
		super(mgm);
		
		properties = new ConfigPropertyContainer();
		properties.addProperty(infectedPercent);
	}

	@Override
	public String getName() {
		return "Infection";
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
	public Menu createSettingsMenu() {
		Menu m = new Menu(6, "Infection Settings");
		
		m.addItem(new MenuItemInteger("Infected Percent", "The percentage of players;chosen to start as;infected", Material.SKULL_ITEM, infectedPercent, 1, 99));
		return m;
	}
	
	@Deprecated
	public static InfectionModule getMinigameModule(Minigame mgm){
		return (InfectionModule) mgm.getModule(InfectionModule.class);
	}
	
	public void setInfectedPercent(int amount){
		infectedPercent.setValue(amount);
	}
	
	public int getInfectedPercent(){
		return infectedPercent.getValue();
	}
	
	public void addInfectedPlayer(MinigamePlayer ply){
		infected.add(ply);
	}
	
	public void removeInfectedPlayer(MinigamePlayer ply){
		infected.remove(ply);
	}
	
	public boolean isInfectedPlayer(MinigamePlayer ply){
		if(infected.contains(ply))
			return true;
		return false;
	}
	
	public void clearInfectedPlayers(){
		infected.clear();
	}
}
