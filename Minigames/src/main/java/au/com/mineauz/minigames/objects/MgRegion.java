package au.com.mineauz.minigames.objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MgRegion {
    private final @NotNull String name;
    private @NotNull World world;
    private @NotNull Position pos1;
    private @NotNull Position pos2;

    public MgRegion(@NotNull World world, @NotNull String name, @NotNull Position pos1, @NotNull Position pos2) {
        this.name = name;
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public MgRegion(@NotNull String name, @NotNull Location loc1, @NotNull Location loc2) {
        this.name = name;
        this.world = loc1.getWorld();
        this.pos1 = Position.block(loc1);
        this.pos2 = Position.block(loc2);
    }

    public @NotNull String getName() {
        return name;
    }

    public void setWorld(@NotNull World world) {
        this.world = world;
    }

    public @NotNull World getWorld() {
        return world;
    }

    public void setFirstPos(@NotNull Position pos1) {
        this.pos1 = pos1;
    }

    public void setFirstPos(@NotNull Location loc1) {
        this.pos1 = Position.block(loc1);
        this.world = loc1.getWorld();
    }

    public void setSecondPos(@NotNull Position pos2) {
        this.pos2 = pos2;
    }

    public void setSecondPos(@NotNull Location loc2) {
        this.pos2 = Position.block(loc2);
        this.world = loc2.getWorld();
    }

    public @NotNull Position getPos1() {
        return pos1;
    }

    public @NotNull Position getPos2() {
        return pos2;
    }

    public @NotNull Location getLocation1() {
        return pos1.toLocation(world);
    }

    public @NotNull Location getLocation2() {
        return pos2.toLocation(world);
    }

    public void updateRegion(Location loc1, Location loc2) {
        this.world = loc1.getWorld();

        this.pos1 = Position.block(loc1);
        this.pos2 = Position.block(loc2);
    }

    public double getMinX() {
        return Math.min(pos1.x(), pos2.x());
    }

    public double getMaxX() {
        return Math.max(pos1.x(), pos2.x());
    }

    public double getMinY() {
        return Math.min(pos1.y(), pos2.y());
    }

    public double getMaxY() {
        return Math.max(pos1.y(), pos2.y());
    }

    public double getMinZ() {
        return Math.min(pos1.z(), pos2.z());
    }

    public double getMaxZ() {
        return Math.max(pos1.z(), pos2.z());
    }

    public boolean isInRegen(Location location) {
        return location.getWorld().getUID() == world.getUID() &&
                location.getBlockX() >= getMinX() && location.getBlockX() <= getMaxX() &&
                location.getBlockY() >= getMinY() && location.getBlockY() <= getMaxY() &&
                location.getBlockZ() >= getMinZ() && location.getBlockZ() <= getMaxZ();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MgRegion) obj;
        return Objects.equals(this.world, that.world) &&
                Objects.equals(this.pos1, that.pos1) &&
                Objects.equals(this.pos2, that.pos2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, pos1, pos2);
    }

    @Override
    public String toString() {
        return "MgRegion[" +
                "name=" + name + ", " +
                "world=" + world + ", " +
                "pos1=" + pos1 + ", " +
                "pos2=" + pos2 + ']';
    }
}
