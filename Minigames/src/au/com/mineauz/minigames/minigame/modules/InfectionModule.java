package au.com.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.Minigame;

public class InfectionModule extends MinigameModule{
	
	private IntegerFlag infectedPercent = new IntegerFlag(18, "infectedPercent");
	
	//Unsaved Data
	private List<MinigamePlayer> infected = new ArrayList<MinigamePlayer>();

	public InfectionModule(Minigame mgm) {
		super(mgm);
	}

	@Override
	public String getName() {
		return "Infection";
	}

	@Override
	public Map<String, Flag<?>> getFlags() {
		Map<String, Flag<?>> flags = new HashMap<String, Flag<?>>();
		flags.put("infectedPercent", infectedPercent);
		return flags;
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
	}

	@Override
	public boolean displayMechanicSettings(Menu previous) {
		Menu m = new Menu(6, "Infection Settings", previous.getViewer());
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		
		m.addItem(infectedPercent.getMenuItem("Infected Percent", Material.SKULL_ITEM, 
				MinigameUtils.stringToList("The percentage of players;chosen to start as;infected"), 1, 99));
		m.displayMenu(previous.getViewer());
		return true;
	}
	
	public static InfectionModule getMinigameModule(Minigame mgm){
		return (InfectionModule) mgm.getModule("Infection");
	}
	
	public void setInfectedPercent(int amount){
		infectedPercent.setFlag(amount);
	}
	
	public int getInfectedPercent(){
		return infectedPercent.getFlag();
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
