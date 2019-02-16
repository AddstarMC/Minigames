package au.com.mineauz.minigames.script;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.google.common.collect.ImmutableSet;

public class ScriptWrapper {
    public static ScriptObject wrap(final Location object) {
        return new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return ImmutableSet.of("x", "y", "z", "bx", "by", "bz", "world", "yaw", "pitch", "block");
            }

            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("x")) {
                    return ScriptValue.of(object.getX());
                } else if (name.equalsIgnoreCase("y")) {
                    return ScriptValue.of(object.getY());
                } else if (name.equalsIgnoreCase("z")) {
                    return ScriptValue.of(object.getZ());
                } else if (name.equalsIgnoreCase("bx")) {
                    return ScriptValue.of(object.getBlockX());
                } else if (name.equalsIgnoreCase("by")) {
                    return ScriptValue.of(object.getBlockY());
                } else if (name.equalsIgnoreCase("bz")) {
                    return ScriptValue.of(object.getBlockZ());
                } else if (name.equalsIgnoreCase("world")) {
                    return wrap(object.getWorld());
                } else if (name.equalsIgnoreCase("yaw")) {
                    return ScriptValue.of(object.getYaw());
                } else if (name.equalsIgnoreCase("pitch")) {
                    return ScriptValue.of(object.getPitch());
                } else if (name.equalsIgnoreCase("block")) {
                    return wrap(object.getBlock());
                }
                return null;
            }

            @Override
            public String getAsString() {
                return String.format("%.1f,%.1f,%.1f,%s", object.getX(), object.getY(), object.getZ(), object.getWorld().getName());
            }
        };
    }

    public static ScriptObject wrap(final Block object) {
        return new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return ImmutableSet.of("pos", "type", "data", "temperature", "light", "blocklight", "skylight", "redstone");
            }

            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("pos")) {
                    return wrap(object.getLocation());
                } else if (name.equalsIgnoreCase("type")) {
                    return ScriptValue.of(object.getType());
                } else if (name.equalsIgnoreCase("data")) {
                    return ScriptValue.of(object.getBlockData());
                } else if (name.equalsIgnoreCase("temperature")) {
                    return ScriptValue.of(object.getTemperature());
                } else if (name.equalsIgnoreCase("light")) {
                    return ScriptValue.of(object.getLightLevel());
                } else if (name.equalsIgnoreCase("blocklight")) {
                    return ScriptValue.of(object.getLightFromBlocks());
                } else if (name.equalsIgnoreCase("skylight")) {
                    return ScriptValue.of(object.getLightFromSky());
                } else if (name.equalsIgnoreCase("redstone")) {
                    return ScriptValue.of(object.getBlockPower());
                }

                return null;
            }

            @Override
            public String getAsString() {
                return String.format("%d,%d,%d,%s %s:%s", object.getX(), object.getY(), object.getZ(), object.getWorld().getName(), object.getType(), object.getBlockData().getAsString());
            }
        };
    }

    public static ScriptObject wrap(final World object) {
        return new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return ImmutableSet.of("name", "time");
            }

            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("name")) {
                    return ScriptValue.of(object.getName());
                } else if (name.equalsIgnoreCase("time")) {
                    return ScriptValue.of(object.getTime());
                }

                return null;
            }

            @Override
            public String getAsString() {
                return object.getName();
            }
        };
    }
}
