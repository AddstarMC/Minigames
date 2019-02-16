package au.com.mineauz.minigames;

import au.com.mineauz.minigames.minigame.Minigame;
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

    public FloorDegenerator(Location point1, Location point2, Minigame mgm) {
        timeDelay = mgm.getFloorDegenTime();
        this.mgm = mgm;
        double minX;
        double maxX;
        double minY;
        double maxY;
        double minZ;
        double maxZ;

        Double x1 = point1.getX();
        Double x2 = point2.getX();
        Double y1 = point1.getY();
        Double y2 = point2.getY();
        Double z1 = point1.getZ();
        Double z2 = point2.getZ();

        if (x1 < x2) {
            minX = x1;
            maxX = x2;
        } else {
            minX = x2;
            maxX = x1;
        }

        if (y1 < y2) {
            minY = y1;
            maxY = y2;
        } else {
            minY = y2;
            maxY = y1;
        }

        if (z1 < z2) {
            minZ = z1;
            maxZ = z2;
        } else {
            minZ = z2;
            maxZ = z1;
        }

        topCorner = new Location(point1.getWorld(), maxX, maxY, maxZ);
        bottomCorner = new Location(point1.getWorld(), minX, minY, minZ);

        xSideNeg1 = new Location(point1.getWorld(), minX, minY, minZ);
        xSideNeg2 = new Location(point1.getWorld(), maxX, maxY, minZ);
        zSideNeg1 = new Location(point1.getWorld(), minX, minY, minZ);
        zSideNeg2 = new Location(point1.getWorld(), minX, maxY, maxZ);
        xSidePos1 = new Location(point1.getWorld(), minX, minY, maxZ);
        xSidePos2 = new Location(point1.getWorld(), maxX, maxY, maxZ);
        zSidePos1 = new Location(point1.getWorld(), maxX, minY, minZ);
        zSidePos2 = new Location(point1.getWorld(), maxX, maxY, maxZ);
    }

    public void startDegeneration() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            switch (mgm.getDegenType()) {
                case "inward":
                    degenerateSide(xSideNeg1, xSideNeg2);
                    degenerateSide(xSidePos1, xSidePos2);
                    degenerateSide(zSideNeg1, zSideNeg2);
                    degenerateSide(zSidePos1, zSidePos2);

                    incrementSide();
                    if (xSideNeg1.getZ() >= xSidePos1.getZ() || zSideNeg1.getX() >= zSidePos1.getX()) {
                        stopDegenerator();
                    }
                    break;
                case "random":
                    degenerateRandom(bottomCorner, topCorner, mgm.getDegenRandomChance());
                    break;
                case "circle":
                    degenerateCircle(bottomCorner, topCorner);
                    break;
            }
        }, timeDelay * 20, timeDelay * 20);
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
                        mgm.getBlockRecorder().addBlock(curblock.getBlock(), null);
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
                        mgm.getBlockRecorder().addBlock(curblock.getBlock(), null);
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
        int middledist = (int) Math.abs(Math.floor((highest.getBlockX() - lowest.getBlockX()) / 2));
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
                mgm.getBlockRecorder().addBlock(curBlock.getBlock(), null);
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
