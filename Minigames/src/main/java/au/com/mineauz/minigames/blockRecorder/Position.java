package au.com.mineauz.minigames.blockRecorder;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

//io.papermc.paper.math.FinePosition
//please note: This is a copy of an experimental implementation. It is most likely not compatible with later versions or even the original.
public record Position(double x, double y, double z) {

    /**
     * Gets the block x value for this position
     * Returns:
     * the block x value
     */
    public int blockX() {
        return NumberConversions.floor(this.x());
    }

    /**
     * Gets the block x value for this position
     * Returns:
     * the block y value
     */
    public int blockY() {
        return NumberConversions.floor(this.y());
    }

    /**
     * Gets the block x value for this position
     * Returns:
     * the block z value
     */
    public int blockZ() {
        return NumberConversions.floor(this.z());
    }

    /**
     * Checks of this position represents a BlockPosition
     * Returns:
     * true if block
     */
    public boolean isBlock() {
        return false;
    }

    /**
     * Checks if this position represents a FinePosition
     * Returns:
     * true if fine
     */
    public boolean isFine() {
        return true;
    }

    /**
     * Returns a position offset by the specified amounts.
     * Params:
     * x – x value to offset y – y value to offset z – z value to offset
     * Returns:
     * the offset position
     */
    public Position offset(int x, int y, int z) {
        return this.offset((double) x, y, z);
    }

    /**
     * Returns a position offset by the specified amounts.
     * Params:
     * x – x value to offset y – y value to offset z – z value to offset
     * Returns:
     * the offset position
     */
    public Position offset(double x, double y, double z) {
        return x == 0.0 && y == 0.0 && z == 0.0 ? this : new Position(this.x() + x, this.y() + y, this.z() + z);
    }

    /**
    * Returns a new position at the center of the block position this represents
    * Returns:
    * a new center position
    */
    public Position toCenter() {
        return new Position(this.blockX() + 0.5, this.blockY() + 0.5, this.blockZ() + 0.5);
    }

    /**
     * Returns the block position of this position
     *
     * @return the block position
     */
    @Contract(pure = true)
    public @NotNull Position toBlock(){
        return new Position(blockX(), blockY(), blockZ());
    }

    /**
     * Converts this position to a vector
     *
     * @return a new vector
     */
    @Contract(value = "-> new", pure = true)
    public Vector toVector() {
        return new Vector(this.x(), this.y(), this.z());
    }

    /**
     * Creates a new location object at this position with the specified world
     *
     * @param world the world for the location object
     * @return a new location
     */
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Location toLocation(@NotNull World world) {
        return new Location(world, this.x(), this.y(), this.z());
    }


    /**
     * Creates a position at the coordinates
     *
     * @param x x coord
     * @param y y coord
     * @param z z coord
     * @return a position with those coords
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull Position block(int x, int y, int z) {
        return new Position(x, y, z);
    }

    /**
     * Creates a position from the location.
     *
     * @param location the location to copy the position of
     * @return a new position at that location
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Position block(@NotNull Location location) {
        return new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Creates a position at the coordinates
     *
     * @param x x coord
     * @param y y coord
     * @param z z coord
     * @return a position with those coords
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull Position fine(double x, double y, double z) {
        return new Position(x, y, z);
    }

    /**
     * Creates a position from the location.
     *
     * @param location the location to copy the position of
     * @return a new position at that location
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Position fine(@NotNull Location location) {
        return new Position(location.getX(), location.getY(), location.getZ());
    }
}
