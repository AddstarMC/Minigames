package au.com.mineauz.minigames;

import org.bukkit.Location;
import org.bukkit.Material;

public class RestoreBlock {
    private String name;
    private Material block;
    private Location location = null;

    public RestoreBlock(String name, Material block, Location loc) {
        this.name = name;
        this.block = block;
        setLocation(loc);
    }

    public Material getBlock() {
        return block;
    }

    public void setBlock(Material block) {
        this.block = block;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
