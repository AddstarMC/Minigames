package au.com.mineauz.minigames.sounds;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Sound;

public class MGSounds {
	
	private static Map<String, MGSound> sounds = new HashMap<String, MGSound>();
	
	static{
		addSound("timerTick", new MGSound(Sound.NOTE_STICKS, 10f, 1.5f));
		addSound("win", new MGSound(Sound.LEVEL_UP));
		addSound("lose", new MGSound(Sound.NOTE_PIANO, 10f, 0.2f, 3, 5L));
		addSound("gameStart", new MGSound(Sound.ORB_PICKUP));
	}
	
	private static void addSound(String name, MGSound sound){
		sounds.put(name, sound);
	}
	
	public static MGSound getSound(String name){
		return sounds.get(name);
	}

}
