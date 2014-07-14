package au.com.mineauz.minigames.mechanics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	
	/**
	 * Adds a new game mechanic to Minigames
	 * @param mechanic A game mechanic extending GameMechanicBase
	 */
	public static void addGameMechanic(GameMechanicBase mechanic){
		gameMechanics.put(mechanic.getMechanic(), mechanic);
	}
	
	/**
	 * Removes an existing game mechanic from Minigames
	 * @param mechanic The name of the mechanic to be removed
	 * @throws NullPointerException if the mechanic cannot be found.
	 */
	public static void removeGameMechanic(String mechanic) throws NullPointerException{
		if(gameMechanics.containsKey(mechanic)){
			HandlerList.unregisterAll(gameMechanics.get(mechanic));
			gameMechanics.remove(mechanic);
		}
		else
			throw new NullPointerException("No GameMechanic of that name has been added!");
	}
	
	/**
	 * Gets a specific game mechanic by name
	 * @param mechanic The name of the mechanic
	 * @return A game mechanic extending GameMechanicBase or Null if none found.
	 */
	public static GameMechanicBase getGameMechanic(String mechanic){
		if(gameMechanics.containsKey(mechanic)){
			return gameMechanics.get(mechanic);
		}
		return null;
	}
	
	/**
	 * Gets all the registered game mechanics in Minigames
	 * @return a Set containing the game mechanics
	 */
	public static Set<GameMechanicBase> getGameMechanics(){
		return new HashSet<GameMechanicBase>(gameMechanics.values());
	}
}
