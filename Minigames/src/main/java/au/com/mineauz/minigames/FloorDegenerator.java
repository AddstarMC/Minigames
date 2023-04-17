package au.com.mineauz.minigames;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MgRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

public class FloorDegenerator {
    private static final Minigames plugin = Minigames.getPlugin();
    private final Location topCorner;
    private final Location bottomCorner;
    private final Location xSideNeg1;
    private final Location xSidePos1;
    private final Location zSideNeg1;
    private final Location zSidePos1;
    private final Location xSideNeg2;
    private final Location xSidePos2;
    private final Location zSideNeg2;
    private final Location zSidePos2;
    private int timeDelay = 30;
    private Minigame mgm = null;
    private int taskID = -1;

    private int radiusModifier = 0;

    public FloorDegenerator(MgRegion region, Minigame mgm) {
        timeDelay = mgm.getFloorDegenTime();
        this.mgm = mgm;

        topCorner = new Location(region.getWorld(), region.getMaxX(), region.getMaxY(), region.getMaxZ());
        bottomCorner = new Location(region.getWorld(), region.getMinX(), region.getMaxY(), region.getMinZ());

        xSideNeg1 = new Location(region.getWorld(), region.getMinX(), region.getMinY(), region.getMinZ());
        xSideNeg2 = new Location(region.getWorld(), region.getMaxX(), region.getMaxY(), region.getMinZ());
        zSideNeg1 = new Location(region.getWorld(), region.getMinX(), region.getMinY(), region.getMinZ());
        zSideNeg2 = new Location(region.getWorld(), region.getMinX(), region.getMaxY(), region.getMaxZ());
        xSidePos1 = new Location(region.getWorld(), region.getMinX(), region.getMinY(), region.getMaxZ());
        xSidePos2 = new Location(region.getWorld(), region.getMaxX(), region.getMaxY(), region.getMaxZ());
        zSidePos1 = new Location(region.getWorld(), region.getMaxX(), region.getMinY(), region.getMinZ());
        zSidePos2 = new Location(region.getWorld(), region.getMaxX(), region.getMaxY(), region.getMaxZ());
    }

    public void startDegeneration() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            switch (mgm.getDegenType()) {
                case "inward" -> {
                    degenerateSide(xSideNeg1, xSideNeg2);
                    degenerateSide(xSidePos1, xSidePos2);
                    degenerateSide(zSideNeg1, zSideNeg2);
                    degenerateSide(zSidePos1, zSidePos2);
                    incrementSide();
                    if (xSideNeg1.getZ() >= xSidePos1.getZ() || zSideNeg1.getX() >= zSidePos1.getX()) {
                        stopDegenerator();
                    }
                }
                case "random" -> degenerateRandom(bottomCorner, topCorner, mgm.getDegenRandomChance());
                case "circle" -> degenerateCircle(bottomCorner, topCorner);
            }
        }, timeDelay * 20L, timeDelay * 20L);
    }

    private void incrementSide() {
        xSideNeg1.setZ(xSideNeg1.getZ() + 1);
        xSideNeg2.setZ(xSideNeg2.getZ() + 1);
        xSidePos1.setZ(xSidePos1.getZ() - 1);
        xSidePos2.setZ(xSidePos2.getZ() - 1);
        zSideNeg1.setX(zSideNeg1.getX() + 1);
        zSideNeg2.setX(zSideNeg2.getX() + 1);
        zSidePos1.setX(zSidePos1.getX() - 1);
        zSidePos2.setX(zSidePos2.getX() - 1);
    }

    private void degenerateSide(Location loc1, Location loc2) {
        Location curblock = loc1.clone();
        int x = curblock.getBlockX();
        int z = curblock.getBlockZ();
        int y = curblock.getBlockY();
        do {
            curblock.setZ(z);
            curblock.setX(x);
            curblock.setY(y);
            for (int i = loc1.getBlockX(); i <= loc2.getBlockX() + 1; i++) {
                for (int k = loc1.getBlockZ(); k <= loc2.getBlockZ() + 1; k++) {
                    if (curblock.getBlock().getType() != Material.AIR) {
                        mgm.getRecorderData().addBlock(curblock.getBlock(), null);
                        curblock.getBlock().setType(Material.AIR);
                    }
                    curblock.setZ(k);
                }
                curblock.setX(i);
                curblock.setZ(z);
            }
            y++;
        } while (y <= loc2.getBlockY());
    }

    private void degenerateRandom(Location lowest, Location highest, int chance) {
        Location curblock = lowest.clone();
        int x = curblock.getBlockX();
        int z = curblock.getBlockZ();
        int y = curblock.getBlockY();
        Random random = new Random();
        do {
            curblock.setZ(z);
            curblock.setX(x);
            curblock.setY(y);
            for (int i = lowest.getBlockX(); i <= highest.getBlockX() + 1; i++) {
                for (int k = lowest.getBlockZ(); k <= highest.getBlockZ() + 1; k++) {
                    if (curblock.getBlock().getType() != Material.AIR && random.nextInt(100) < chance) {
                        mgm.getRecorderData().addBlock(curblock.getBlock(), null);
                        curblock.getBlock().setType(Material.AIR);
                    }
                    curblock.setZ(k);
                }
                curblock.setX(i);
                curblock.setZ(z);
            }
            y++;
        } while (y <= highest.getBlockY());
    }

    private void degenerateCircle(Location lowest, Location highest) {
        int middledist = (int) Math.abs(Math.floor((double) (highest.getBlockX() - lowest.getBlockX()) / 2));
        int radius = middledist - radiusModifier;
        Location centerBlock = lowest.clone();
        centerBlock.setX(centerBlock.getX() + middledist);
        centerBlock.setZ(centerBlock.getZ() + middledist);
        Location curBlock = centerBlock.clone();

        int size = (int) Math.pow(radius, 3) + 8;

        for (int i = 0; i < size; i++) {
            double cirPoint = 2 * Math.PI * i / size;
            double cx = centerBlock.getX() - 0.5 + Math.round(radius * Math.cos(cirPoint));
            double cz = centerBlock.getZ() - 0.5 + Math.round(radius * Math.sin(cirPoint));
            curBlock.setX(cx);
            curBlock.setZ(cz);
            for (int k = lowest.getBlockY(); k <= highest.getBlockY(); k++) {
                curBlock.setY(k);
                mgm.getRecorderData().addBlock(curBlock.getBlock(), null);
                curBlock.getBlock().setType(Material.AIR);
            }
        }

        radiusModifier++;

        if (middledist == radiusModifier) {
            stopDegenerator();
        }
    }

    public void stopDegenerator() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }
}
