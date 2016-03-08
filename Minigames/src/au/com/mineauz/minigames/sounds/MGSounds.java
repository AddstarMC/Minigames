package au.com.mineauz.minigames.sounds;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Sound;

public class MGSounds {
	
	private static Map<String, MGSound> sounds = new HashMap<String, MGSound>();
	
	static{
		addSound("timerTick", new MGSound(Sound.BLOCK_NOTE_HAT, 10f, 1.5f));
		addSound("win", new MGSound(Sound.ENTITY_PLAYER_LEVELUP));
		addSound("lose", new MGSound(Sound.BLOCK_NOTE_PLING, 10f, 0.2f, 3, 5L));
		addSound("gameStart", new MGSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP));
	}
	
	private static void addSound(String name, MGSound sound){
		sounds.put(name, sound);
	}
	
	public static MGSound getSound(String name){
		return sounds.get(name);
	}

}
