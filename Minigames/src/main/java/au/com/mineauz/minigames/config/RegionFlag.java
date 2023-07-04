package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.objects.MgRegion;
import io.papermc.paper.math.Position;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RegionFlag extends Flag<MgRegion> {
    private final @Nullable String legacyFistPointLabel, legacySecondPointLabel;

    public RegionFlag(MgRegion value, String name, @Nullable String legacyFirstPoint, @Nullable String legacySecondPoint) {
        this.legacyFistPointLabel = legacyFirstPoint;
        this.legacySecondPointLabel = legacySecondPoint;

        setFlag(value);
        setDefaultFlag(value);
        setName(name);
    }

    public RegionFlag(MgRegion value, String name) {
        this.legacyFistPointLabel = null;
        this.legacySecondPointLabel = null;

        setFlag(value);
        setDefaultFlag(value);
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        config.set(path + "." + getName() + ".name", getFlag().getName());
        config.set(path + "." + getName() + ".world", getFlag().getWorld().getName());
        config.set(path + "." + getName() + ".pos1", getFlag().getPos1().x() + ":" + getFlag().getPos1().y() + ":" + getFlag().getPos1().z());
        config.set(path + "." + getName() + ".pos2", getFlag().getPos2().x() + ":" + getFlag().getPos2().y() + ":" + getFlag().getPos2().z());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        String name = config.getString(path + "." + getName() + ".name");
        if (name != null) {
            String world = config.getString(path + "." + getName() + ".world");

            String[] sliptPos1 = config.getString(path + "." + getName() + ".pos1").split(":");
            String[] sliptPos2 = config.getString(path + "." + getName() + ".pos2").split(":");

            double x1 = Double.parseDouble(sliptPos1[0]);
            double y1 = Double.parseDouble(sliptPos1[1]);
            double z1 = Double.parseDouble(sliptPos1[2]);

            double x2 = Double.parseDouble(sliptPos2[0]);
            double y2 = Double.parseDouble(sliptPos2[1]);
            double z2 = Double.parseDouble(sliptPos2[2]);

            setFlag(new MgRegion(Bukkit.getWorld(world), name, Position.fine(x1, y1, z1), Position.fine(x2, y2, z2)));
        } else {
            //import legacy regions from before regions existed
            if (legacyFistPointLabel != null && legacySecondPointLabel != null) {
                SimpleLocationFlag locFlag1 = new SimpleLocationFlag(null, legacyFistPointLabel);
                SimpleLocationFlag locFlag2 = new SimpleLocationFlag(null, legacySecondPointLabel);

                if (locFlag1.getFlag() != null && locFlag2.getFlag() != null) {
                    setFlag(new MgRegion("legacy", locFlag1.getFlag(), locFlag2.getFlag()));
                }
            }
        }
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem) {
        return null;
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem, List<String> description) {
        return null;
    }
}
