package au.com.mineauz.minigames.script;

import java.util.Set;

public interface ScriptObject extends ScriptReference {
	public ScriptReference get(String name);
	public Set<String> getKeys();
	
	public String getAsString();
}
