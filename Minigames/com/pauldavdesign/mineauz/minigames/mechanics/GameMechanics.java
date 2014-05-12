package com.pauldavdesign.mineauz.minigames.mechanics;

import java.util.HashMap;
import java.util.Map;

public class GameMechanics {
	private static Map<String, GameMechanicBase> gameMechanics = new HashMap<String, GameMechanicBase>();
	
	static{
		addGameMechanic(new PlayerKillsMechanic());
		addGameMechanic(new CTFMechanic());
		addGameMechanic(new InfectionMechanic());
		addGameMechanic(new CustomMechanic());
	}
	
	public static void addGameMechanic(GameMechanicBase type){
		gameMechanics.put(type.getMechanic(), type);
	}
	
	public static GameMechanicBase getGameMechanic(String type){
		if(gameMechanics.containsKey(type)){
			return gameMechanics.get(type);
		}
		return null;
	}
	
	public Map<String, GameMechanicBase> getGameMechanics(){
		return gameMechanics;
	}
}
