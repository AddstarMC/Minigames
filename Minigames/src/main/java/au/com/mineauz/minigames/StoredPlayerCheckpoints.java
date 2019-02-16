package au.com.mineauz.minigames;

import au.com.mineauz.minigames.config.MinigameSave;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class StoredPlayerCheckpoints {
    private String uuid;
    private Map<String, Location> checkpoints;
    private Map<String, List<String>> flags;
    private Map<String, Long> storedTime;
    private Map<String, Integer> storedDeaths;
    private Map<String, Integer> storedReverts;
    private Location globalCheckpoint;

    public StoredPlayerCheckpoints(String uuid) {
        this.uuid = uuid;
        checkpoints = new HashMap<>();
        flags = new HashMap<>();
        storedTime = new HashMap<>();
        storedDeaths = new HashMap<>();
        storedReverts = new HashMap<>();
    }

    public void addCheckpoint(String minigame, Location checkpoint) {
        checkpoints.put(minigame, checkpoint);
    }

    public void removeCheckpoint(String minigame) {
        checkpoints.remove(minigame);
    }

    public boolean hasCheckpoint(String minigame) {
        return checkpoints.containsKey(minigame);
    }

    public Location getCheckpoint(String minigame) {
        return checkpoints.get(minigame);
    }

    public boolean hasGlobalCheckpoint() {
        return globalCheckpoint != null;
    }

    public Location getGlobalCheckpoint() {
        return globalCheckpoint;
    }

    public void setGlobalCheckpoint(Location checkpoint) {
        globalCheckpoint = checkpoint;
    }

    public boolean hasNoCheckpoints() {
        return checkpoints.isEmpty();
    }

    public boolean hasFlags(String minigame) {
        return flags.containsKey(minigame);
    }

    public void addFlags(String minigame, List<String> flagList) {
        flags.put(minigame, new ArrayList<>(flagList));
    }

    public List<String> getFlags(String minigame) {
        return flags.get(minigame);
    }

    public void removeFlags(String minigame) {
        flags.remove(minigame);
    }

    public void addTime(String minigame, long time) {
        storedTime.put(minigame, time);
    }

    public Long getTime(String minigame) {
        return storedTime.get(minigame);
    }

    public boolean hasTime(String minigame) {
        return storedTime.containsKey(minigame);
    }

    public void removeTime(String minigame) {
        storedTime.remove(minigame);
    }

    public void addDeaths(String minigame, int deaths) {
        storedDeaths.put(minigame, deaths);
    }

    public Integer getDeaths(String minigame) {
        return storedDeaths.get(minigame);
    }

    public boolean hasDeaths(String minigame) {
        return storedDeaths.containsKey(minigame);
    }

    public void removeDeaths(String minigame) {
        storedDeaths.remove(minigame);
    }

    public void addReverts(String minigame, int reverts) {
        storedReverts.put(minigame, reverts);
    }

    public Integer getReverts(String minigame) {
        return storedReverts.get(minigame);
    }

    public boolean hasReverts(String minigame) {
        return storedReverts.containsKey(minigame);
    }

    public void removeReverts(String minigame) {
        storedReverts.remove(minigame);
    }

    public void saveCheckpoints() {
        MinigameSave save = new MinigameSave("playerdata/checkpoints/" + uuid);
        save.deleteFile();
        if (hasNoCheckpoints()) return;

        save = new MinigameSave("playerdata/checkpoints/" + uuid);
        for (String mgm : checkpoints.keySet()) {
            MinigameUtils.debugMessage("Attempting to save checkpoint for " + mgm + "...");
            try {
                save.getConfig().set(mgm, null);
                save.getConfig().set(mgm + ".x", checkpoints.get(mgm).getX());
                save.getConfig().set(mgm + ".y", checkpoints.get(mgm).getY());
                save.getConfig().set(mgm + ".z", checkpoints.get(mgm).getZ());
                save.getConfig().set(mgm + ".yaw", checkpoints.get(mgm).getYaw());
                save.getConfig().set(mgm + ".pitch", checkpoints.get(mgm).getPitch());
                save.getConfig().set(mgm + ".world", checkpoints.get(mgm).getWorld().getName());

                if (flags.containsKey(mgm))
                    save.getConfig().set(mgm + ".flags", getFlags(mgm));

                if (storedTime.containsKey(mgm))
                    save.getConfig().set(mgm + ".time", getTime(mgm));

                if (storedDeaths.containsKey(mgm))
                    save.getConfig().set(mgm + ".deaths", getDeaths(mgm));

                if (storedReverts.containsKey(mgm))
                    save.getConfig().set(mgm + ".reverts", getReverts(mgm));
            } catch (Exception e) {
                // When an error is detected, remove the stored erroneous checkpoint
                Minigames.getPlugin().getLogger().warning("Unable to save checkpoint for " + mgm + "! It has been been removed.");
                e.printStackTrace();

                // Remove the checkpoint from memory so it doesn't cause an error again
                save.getConfig().set(mgm, null);
                checkpoints.remove(mgm);
                flags.remove(mgm);
                storedTime.remove(mgm);
                storedDeaths.remove(mgm);
                storedReverts.remove(mgm);
            }
        }

        if (hasGlobalCheckpoint()) {
            try {
                save.getConfig().set("globalcheckpoint.x", globalCheckpoint.getX());
                save.getConfig().set("globalcheckpoint.y", globalCheckpoint.getY());
                save.getConfig().set("globalcheckpoint.z", globalCheckpoint.getZ());
                save.getConfig().set("globalcheckpoint.yaw", globalCheckpoint.getYaw());
                save.getConfig().set("globalcheckpoint.pitch", globalCheckpoint.getPitch());
                save.getConfig().set("globalcheckpoint.world", globalCheckpoint.getWorld().getName());
            } catch (Exception e) {
                // When an error is detected, remove the global checkpoint
                save.getConfig().set("globalcheckpoint", null);
                Minigames.getPlugin().getLogger().warning("Unable to save global checkpoint!");
                e.printStackTrace();
            }
        }
        save.saveConfig();
    }

    public void loadCheckpoints() {
        MinigameSave save = new MinigameSave("playerdata/checkpoints/" + uuid);
        Set<String> mgms = save.getConfig().getKeys(false);
        for (String mgm : mgms) {
            if (!mgm.equals("globalcheckpoint")) {
                MinigameUtils.debugMessage("Attempting to load checkpoint for " + mgm + "...");
                try {
                    Double locx = (Double) save.getConfig().get(mgm + ".x");
                    Double locy = (Double) save.getConfig().get(mgm + ".y");
                    Double locz = (Double) save.getConfig().get(mgm + ".z");
                    Float yaw = new Float(save.getConfig().get(mgm + ".yaw").toString());
                    Float pitch = new Float(save.getConfig().get(mgm + ".pitch").toString());
                    String world = (String) save.getConfig().get(mgm + ".world");

                    World w = Minigames.getPlugin().getServer().getWorld(world);
                    if (w == null) {
                        Minigames.getPlugin().getLogger().warning("WARNING: Invalid world \"" + world + "\" found in checkpoint for " + mgm + "! Checkpoint has been removed.");
                        continue;
                    }

                    Location loc = new Location(w, locx, locy, locz, yaw, pitch);
                    checkpoints.put(mgm, loc);
                } catch (ClassCastException e) {
                    MinigameUtils.debugMessage("Checkpoint could not be loaded ... " + mgm + " xyz not double");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (save.getConfig().contains(mgm + ".flags")) {
                    flags.put(mgm, save.getConfig().getStringList(mgm + ".flags"));
                }

                if (save.getConfig().contains(mgm + ".time")) {
                    storedTime.put(mgm, save.getConfig().getLong(mgm + ".time"));
                }

                if (save.getConfig().contains(mgm + ".deaths")) {
                    storedDeaths.put(mgm, save.getConfig().getInt(mgm + ".deaths"));
                }

                if (save.getConfig().contains(mgm + ".reverts")) {
                    storedReverts.put(mgm, save.getConfig().getInt(mgm + ".reverts"));
                }
            }
        }

        if (save.getConfig().contains("globalcheckpoint")) {
            double x = save.getConfig().getDouble("globalcheckpoint.x");
            double y = save.getConfig().getDouble("globalcheckpoint.y");
            double z = save.getConfig().getDouble("globalcheckpoint.z");
            Float yaw = new Float(save.getConfig().get("globalcheckpoint.yaw").toString());
            Float pitch = new Float(save.getConfig().get("globalcheckpoint.pitch").toString());
            String world = save.getConfig().getString("globalcheckpoint.world");

            World w = Minigames.getPlugin().getServer().getWorld(world);
            if (w == null) {
                Minigames.getPlugin().getLogger().warning("WARNING: Invalid world \"" + world + "\" found in global checkpoint! Checkpoint has been removed.");
            } else {
                globalCheckpoint = new Location(Minigames.getPlugin().getServer().getWorld(world), x, y, z, yaw, pitch);
            }
        }
    }
}
