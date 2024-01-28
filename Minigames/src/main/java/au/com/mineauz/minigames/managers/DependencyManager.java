package au.com.mineauz.minigames.managers;

import au.com.mineauz.minigames.Minigames;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits.forActor;

public class DependencyManager {
    private static Plugin worldEditPlugin = null; // cached for test if still enabled
    private static Boolean worldEditEnabled = null; // this flags if we ever (successfully) testet on WorldEdit

    public static boolean isWorldEditEnabled() {
        if (worldEditEnabled == null) {
            Plugin worldEditPlugin = Minigames.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit");
            worldEditEnabled = worldEditPlugin != null && worldEditPlugin.isEnabled() && WorldEdit.getInstance() != null;

            if (worldEditEnabled) {
                DependencyManager.worldEditPlugin = worldEditPlugin;
            }
        }

        return worldEditEnabled && worldEditPlugin != null && worldEditPlugin.isEnabled();
    }

    public static boolean hasSelection(@NotNull Player player) {
        SelectedRegionStatusWrapper statusWrapper = getSelectedRegion(player);
        return statusWrapper.status() == SelectedRegionStatus.SUCCESS && statusWrapper.pos1() != null && statusWrapper.pos2() != null;
    }

    public static @Nullable Location getLocation1(@NotNull Player player) {
        World world = BukkitAdapter.adapt(player.getWorld());
        RegionSelector regionSelector = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player)).getRegionSelector(world);

        if (regionSelector.getIncompleteRegion() instanceof CuboidRegion cuboidRegion) {
            BlockVector3 pos1 = cuboidRegion.getPos1();
            if (!pos1.equals(BlockVector3.ZERO)) {
                return new Location(player.getWorld(), pos1.getX(), pos1.getY(), pos1.getZ());
            }
        }

        return null;
    }

    public static @Nullable Location getLocation2(@NotNull Player player) {
        World world = BukkitAdapter.adapt(player.getWorld());
        RegionSelector regionSelector = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player)).getRegionSelector(world);

        if (regionSelector.getIncompleteRegion() instanceof CuboidRegion cuboidRegion) {
            BlockVector3 pos2 = cuboidRegion.getPos2();
            if (!pos2.equals(BlockVector3.ZERO)) {
                return new Location(player.getWorld(), pos2.getX(), pos2.getY(), pos2.getZ());
            }
        }

        return null;
    }

    public static @NotNull SelectedRegionStatusWrapper getSelectedRegion(@NotNull Player player) {
        World world = BukkitAdapter.adapt(player.getWorld());
        RegionSelector regionSelector = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player)).getRegionSelector(world);

        if (regionSelector.getIncompleteRegion() instanceof CuboidRegion cuboidRegion) {
            if (regionSelector.getIncompleteRegion().getWorld() != null && cuboidRegion.getWorld().equals(world)) {

                BlockVector3 pos1 = cuboidRegion.getPos1();
                BlockVector3 pos2 = cuboidRegion.getPos2();
                return new SelectedRegionStatusWrapper(SelectedRegionStatus.SUCCESS,
                        new Location(player.getWorld(), pos1.getX(), pos1.getY(), pos1.getZ()),
                        new Location(player.getWorld(), pos2.getX(), pos2.getY(), pos2.getZ()));
            } else {
                return new SelectedRegionStatusWrapper(SelectedRegionStatus.INCOMPLETE, null, null);
            }
        } else {
            return new SelectedRegionStatusWrapper(SelectedRegionStatus.SHAPE_UNSUPPORTED, null, null);
        }
    }

    public static void setPos1(@NotNull Player player, @NotNull Location location1) {
        World world = BukkitAdapter.adapt(location1.getWorld());
        Actor actor = BukkitAdapter.adapt(player);
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(actor);

        RegionSelector oldRegionSelector = session.getRegionSelector(world);
        CuboidRegionSelector cuboidRegionSelector = new CuboidRegionSelector(oldRegionSelector);
        WorldEdit.getInstance().getSessionManager().get(actor).setRegionSelector(world, cuboidRegionSelector);

        cuboidRegionSelector.selectPrimary(BlockVector3.at(location1.x(), location1.y(), location1.z()), ActorSelectorLimits.forActor(actor));
        cuboidRegionSelector.learnChanges();
        cuboidRegionSelector.explainRegionAdjust(actor, session);
    }

    public static void setPos2(@NotNull Player player, @NotNull Location location2) {
        World world = BukkitAdapter.adapt(location2.getWorld());
        Actor actor = BukkitAdapter.adapt(player);
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(actor);

        RegionSelector oldRegionSelector = session.getRegionSelector(world);
        CuboidRegionSelector cuboidRegionSelector = new CuboidRegionSelector(oldRegionSelector);
        WorldEdit.getInstance().getSessionManager().get(actor).setRegionSelector(world, cuboidRegionSelector);

        cuboidRegionSelector.selectSecondary(BlockVector3.at(location2.x(), location2.y(), location2.z()), forActor(actor));
        cuboidRegionSelector.learnChanges();
        cuboidRegionSelector.explainRegionAdjust(actor, session);
    }

    public static void clearSelection(Player player) {
        Actor actor = BukkitAdapter.adapt(player);
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(actor);
        RegionSelector regionSelector = session.getRegionSelector(BukkitAdapter.adapt(player.getWorld()));

        regionSelector.clear();
        regionSelector.learnChanges();
        regionSelector.explainRegionAdjust(actor, session);
    }

    public enum SelectedRegionStatus {
        SHAPE_UNSUPPORTED,
        INCOMPLETE,
        SUCCESS
    }

    public record SelectedRegionStatusWrapper(@NotNull SelectedRegionStatus status, @Nullable Location pos1,
                                              @Nullable Location pos2) {
    }
}
