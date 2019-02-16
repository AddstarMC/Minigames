package au.com.mineauz.minigames.script;

import java.util.Set;

public interface ScriptObject extends ScriptReference {
    ScriptReference get(String name);

    Set<String> getKeys();

    String getAsString();
}
