package au.com.mineauz.minigames.objects;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MessageManager;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;

/**
 * Flag of Capture the Flag.
 * Technical background for au.com.mineauz.minigames.signs.FlagSign
 */
public class CTFFlag {
    private Location spawnLocation = null;
    private Location currentLocation = null;
    private BlockState spawnData = null;
    private BlockState originalBlock = null;
    private String[] signText = null;
    private boolean atHome = true;
    private Team team = null;
    private int respawnTime = 60;
    private int taskID = -1;
    private Minigame minigame = null;
    private int cParticleID = -1;

    public CTFFlag(Location spawn, Team team, Minigame minigame) {
        spawnLocation = spawn;
        ((Sign) spawnLocation.getBlock().getState()).setWaxed(true);
        spawnData = spawnLocation.getBlock().getState();
        signText = ((Sign) spawnLocation.getBlock().getState()).getSide(Side.FRONT).getLines();
        this.team = team;
        this.setMinigame(minigame);
        respawnTime = Minigames.getPlugin().getConfig().getInt("multiplayer.ctf.flagrespawntime");
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isAtHome() {
        return atHome;
    }

    public void setAtHome(boolean atHome) {
        this.atHome = atHome;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Location spawnFlag(Location location) {
        Location blockBelow = location.clone();
        Location newLocation = location.clone();
        blockBelow.setY(blockBelow.getBlockY() - 1);

        if (blockBelow.getBlock().getType() == Material.AIR) {
            while (blockBelow.getBlock().getType() == Material.AIR) {
                if (blockBelow.getY() > blockBelow.getWorld().getMinHeight()) {
                    blockBelow.setY(blockBelow.getY() - 1);
                } else {
                    return null;
                }
            }
        } else if (blockBelow.getBlock().getType() != Material.AIR) {
            while (blockBelow.getBlock().getType() != Material.AIR) {
                if (blockBelow.getY() < blockBelow.getWorld().getMaxHeight()) {
                    blockBelow.setY(blockBelow.getY() + 1);
                } else {
                    return null;
                }
            }
            blockBelow.setY(blockBelow.getY() - 1);
        }

        if (blockBelow.getBlock().getType() == Material.LAVA) {
            return null;
        }

        if (blockBelow.getBlock().getState() instanceof Container ||
                Tag.ALL_SIGNS.isTagged(blockBelow.getBlock().getType())) {
            blockBelow.setY(blockBelow.getY() + 1);
        }

        newLocation = blockBelow.clone();
        newLocation.setY(newLocation.getY() + 1);

        // Converting wall signs to normal signs, if necessary
        String standingSignName = spawnData.getType().toString()
                .replace("WALL_SIGN", "SIGN")
                .replace("WALL_HANGING_SIGN", "SIGN")
                .replace("HANGING_SIGN", "SIGN");
        Material standingSign = Material.getMaterial(standingSignName);

        newLocation.getBlock().setType(standingSign == null ? Material.OAK_SIGN : standingSign);
        Sign sign = (Sign) newLocation.getBlock().getState();

        originalBlock = blockBelow.getBlock().getState();
        blockBelow.getBlock().setType(Material.BEDROCK);

        atHome = false;

        for (int i = 0; i < 4; i++) {
            sign.getSide(Side.FRONT).setLine(i, signText[i]);
        }
        sign.update();
        currentLocation = newLocation.clone();

        return newLocation;
    }

    public void removeFlag() {
        if (!atHome) {
            if (currentLocation != null) {
                Location blockBelow = currentLocation.clone();
                currentLocation.getBlock().setType(Material.AIR);

                blockBelow.setY(blockBelow.getY() - 1);
                blockBelow.getBlock().setType(originalBlock.getType());
                originalBlock.update();

                currentLocation = null;
                stopTimer();
            }
        } else {
            spawnLocation.getBlock().setType(Material.AIR);
        }
    }

    public void respawnFlag() {
        removeFlag();
        spawnLocation.getBlock().setType(spawnData.getType());
        spawnData.update();
        currentLocation = null;
        atHome = true;

        Sign sign = (Sign) spawnLocation.getBlock().getState();
        sign.setWaxed(true);

        for (int i = 0; i < 4; i++) {
            sign.getSide(Side.FRONT).setLine(i, signText[i]);
        }
        sign.update();
    }

    public void stopTimer() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    public Minigame getMinigame() {
        return minigame;
    }

    public void setMinigame(Minigame minigame) {
        this.minigame = minigame;
    }

    public void startReturnTimer() {
        final CTFFlag self = this;
        taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.getPlugin(), () -> {
            String id = MinigameUtils.createLocationID(currentLocation);
            if (minigame.hasDroppedFlag(id)) {
                minigame.removeDroppedFlag(id);
                String newID = MinigameUtils.createLocationID(spawnLocation);
                minigame.addDroppedFlag(newID, self);
            }
            respawnFlag();
            //TODO: Build this again with broadcasts.
            for (MinigamePlayer pl : minigame.getPlayers()) {
                if (getTeam() != null)
                    pl.sendInfoMessage(
                            MessageManager.getMinigamesMessage("minigame.flag.returnedTeam", getTeam().getChatColor() + getTeam().getDisplayName() + ChatColor.WHITE));
                else
                    pl.sendInfoMessage(MinigameUtils.getLang("minigame.flag.returnedNeutral"));
            }
            taskID = -1;
        }, respawnTime * 20L);
    }

    public void startCarrierParticleEffect(final Player player) {
        cParticleID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.getPlugin(), () -> player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0), 15L, 15L);
    }

    public void stopCarrierParticleEffect() {
        if (cParticleID != -1) {
            Bukkit.getScheduler().cancelTask(cParticleID);
            cParticleID = -1;
        }
    }
}
