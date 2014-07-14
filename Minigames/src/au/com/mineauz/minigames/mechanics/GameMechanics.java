package au.com.mineauz.minigames.mechanics;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.HandlerList;

public class GameMechanics {
	private static Map<String, GameMechanicBase> gameMechanics = new HashMap<String, GameMechanicBase>();
	
	static{
		addGameMechanic(new PlayerKillsMechanic());
		addGameMechanic(new CTFMechanic());
		addGameMechanic(new InfectionMechanic());
		addGameMechanic(new CustomMechanic());
		addGameMechanic(new TreasureHuntMechanic());
	}
	
	public static void addGameMechanic(GameMechanicBase mechanic){
		gameMechanics.put(mechanic.getMechanic(), mechanic);
	}
	
	public static void removeGameMechanic(String mechanic) throws NullPointerException{
		if(gameMechanics.containsKey(mechanic)){
			HandlerList.unregisterAll(gameMechanics.get(mechanic));
			gameMechanics.remove(mechanic);
		}
		else
			throw new NullPointerException("No GameMechanic of that name has been added!");
	}
	
	public static GameMechanicBase getGameMechanic(String mechanic){
		if(gameMechanics.containsKey(mechanic)){
			return gameMechanics.get(mechanic);
		}
		return null;
	}
	
	public Map<String, GameMechanicBase> getGameMechanics(){
		return gameMechanics;
	}
}
