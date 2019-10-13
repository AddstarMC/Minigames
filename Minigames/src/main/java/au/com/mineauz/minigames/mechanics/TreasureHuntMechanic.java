package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameTimer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.events.MinigameTimerTickEvent;
import au.com.mineauz.minigames.events.TimerExpireEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;
import au.com.mineauz.minigames.minigame.reward.ItemReward;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.RewardsModule;
import au.com.mineauz.minigames.minigame.reward.scheme.StandardRewardScheme;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TreasureHuntMechanic extends GameMechanicBase {

    public static void removeTreasure(Minigame minigame) {
        TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(minigame);
        thm.clearHints();
        if (thm.hasTreasureLocation()) {
            Location old = thm.getTreasureLocation();
            if (old.getWorld() != null && !old.getWorld().isChunkLoaded(old.getChunk().getX(), old.getChunk().getZ())) {
              boolean loaded = old.getChunk().load();
              Chunk c = null;
              if(loaded) {
                  c = old.getChunk();
                  c.setForceLoaded(true);
                }
                if (old.getBlock().getState() instanceof Chest) {
                    Chest chest = (Chest) old.getBlock().getState();
                    chest.getInventory().clear();
                    old.getBlock().setType(Material.AIR);
                }
                if (loaded) {
                  c.setForceLoaded(false);
                  c.unload();
                }
              thm.setTreasureLocation(null);
            }
        }
    }
    public static void spawnTreasure(final Minigame mgm) {
        final TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(mgm);

        if (thm.hasTreasureLocation())
            removeTreasure(mgm);
        if (!thm.getCurrentHints().isEmpty())
            thm.clearHints();
        thm.setTreasureFound(false);

        Location tcpos = mgm.getStartLocations().get(0).clone();
        final Location rpos = tcpos;
        double rx;
        double ry;
        double rz;
        final int maxradius;
        if (thm.getMaxRadius() == 0) {
            maxradius = 1000;
        } else {
            maxradius = thm.getMaxRadius();
        }
        final int maxheight = thm.getMaxHeight();

        Random rand = new Random();
        int rrad = rand.nextInt(maxradius);
        double randCir = 2 * Math.PI * rand.nextInt(360) / 360;
        rx = tcpos.getX() - 0.5 + Math.round(rrad * Math.cos(randCir));
        rz = tcpos.getZ() - 0.5 + Math.round(rrad * Math.sin(randCir));

        ry = tcpos.getY() + rand.nextInt(maxheight);

        rpos.setX(rx);
        rpos.setY(ry);
        rpos.setZ(rz);

        //Add a new Chest
        //TODO: Improve so no invalid spawns (Not over void, Strict containment)
        switch (rpos.getBlock().getType()){
            case AIR:
            case CAVE_AIR:
            case VOID_AIR:
                while (rpos.getBlock().getType() == Material.AIR && rpos.getY() > 1) {
                    rpos.setY(rpos.getY() - 1);
                }
                rpos.setY(rpos.getY() + 1);

                Bukkit.getScheduler().runTaskLater(plugin, () -> rpos.getBlock().setType(Material.CHEST),1L);
                break;

                default:
                    while (rpos.getBlock().getType() != Material.AIR && rpos.getY() < 255) {
                        rpos.setY(rpos.getY() + 1);
                    }
                    Bukkit.getScheduler().runTaskLater(plugin, () -> rpos.getBlock().setType(Material.CHEST),1L);
                    break;
        }
        //Fill new chest
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (rpos.getBlock().getState() instanceof Chest) {
                final Chest chest = (Chest) rpos.getBlock().getState();

                // TODO: Treasure hunt needs own rewards specification
                RewardsModule rewards = RewardsModule.getModule(mgm);
                if (rewards.getScheme() instanceof StandardRewardScheme) {
                    if (!((StandardRewardScheme) rewards.getScheme()).getPrimaryReward().getRewards().isEmpty()) {
                        int numitems = (int) Math.round(Math.random() * (thm.getMaxTreasure() - thm.getMinTreasure())) + thm.getMinTreasure();

                        final ItemStack[] items = new ItemStack[27];
                        for (int i = 0; i < numitems; i++) {
                            RewardType rew = ((StandardRewardScheme) rewards.getScheme()).getPrimaryReward().getReward().get(0);
                            if (rew instanceof ItemReward) {
                                ItemReward irew = (ItemReward) rew;
                                items[i] = irew.getRewardItem();
                            }
                        }
                        Collections.shuffle(Arrays.asList(items));
                        chest.getInventory().setContents(items);
                    }
                }
            }
        },0L);

        thm.setTreasureLocation(rpos);
        plugin.getLogger().info(MinigameUtils.formStr("minigame.treasurehunt.consSpawn", mgm.getName(false), rpos.getBlockX() + ", " + rpos.getBlockY() + ", " + rpos.getBlockZ()));
        MinigameUtils.broadcast(MinigameUtils.formStr("minigame.treasurehunt.plySpawn", maxradius, thm.getLocation()), mgm, "minigame.treasure.announce");

        mgm.setMinigameTimer(new MinigameTimer(mgm, mgm.getTimer()));
    }

    @Override
    public String getMechanic() {
        return "treasure_hunt";
    }

    @Override
    public EnumSet<MinigameType> validTypes() {
        return EnumSet.of(MinigameType.GLOBAL);
    }

    @Override
    public boolean checkCanStart(Minigame minigame, MinigamePlayer caller) {
        return true;
    }

    @Override
    public MinigameModule displaySettings(Minigame minigame) {
        return minigame.getModule("TreasureHunt");
    }

    @Override
    public void startMinigame(Minigame minigame, MinigamePlayer caller) {
        final TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(minigame);
        if (thm.getLocation() != null) {
            spawnTreasure(minigame);

            if (Bukkit.getOnlinePlayers().size() == 0)
                minigame.getMinigameTimer().stopTimer();
        } else {
            if (caller == null)
                Bukkit.getLogger().info("Treasure Hunt requires a location name to run!");
            else
                caller.sendMessage("Treasure Hunt requires a location name to run!", MinigameMessageType.ERROR);
        }
    }

    @Override
    public void stopMinigame(Minigame minigame, MinigamePlayer caller) {
        TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(minigame);

        minigame.getMinigameTimer().stopTimer();
        minigame.setMinigameTimer(null);
        thm.clearHints();

        if (thm.hasTreasureLocation()) {
            removeTreasure(minigame);
            if (!thm.isTreasureFound()) {
                MinigameUtils.broadcast(MinigameUtils.formStr("minigame.treasurehunt.plyRemoved", minigame.getName(true))
                        , minigame, "minigame.treasure.announce");
            }
        }
    }

    @Override
    public void onJoinMinigame(Minigame minigame, MinigamePlayer player) {
    }

    @Override
    public void quitMinigame(Minigame minigame, MinigamePlayer player,
                             boolean forced) {
    }

    @Override
    public void endMinigame(Minigame minigame, List<MinigamePlayer> winners,
                            List<MinigamePlayer> losers) {
    }

    @EventHandler
    private void timerTick(MinigameTimerTickEvent event) {
        if (event.getMinigame().getType() != MinigameType.GLOBAL &&
                !event.getMinigame().getMechanicName().equals(getMechanic())) return;

        Minigame mgm = event.getMinigame();
        TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(mgm);
        if (!thm.hasTreasureLocation() || thm.isTreasureFound()) return;

        int time = event.getTimeLeft();
        int hintTime1 = event.getMinigame().getTimer() - 1;
        int hintTime2 = (int) (event.getMinigame().getTimer() * 0.75);
        int hintTime3 = (int) (event.getMinigame().getTimer() * 0.50);
        int hintTime4 = (int) (event.getMinigame().getTimer() * 0.25);
        Location block = thm.getTreasureLocation();

        if (time == hintTime1) {
            double dfcx;
            double dfcz;
            String xdir;
            String zdir;

            if (mgm.getStartLocations().get(0).getX() > block.getX()) {
                dfcx = mgm.getStartLocations().get(0).getX() - block.getX();
                xdir = MinigameUtils.getLang("minigame.treasurehunt.hint1.west");
            } else {
                dfcx = block.getX() - mgm.getStartLocations().get(0).getX();
                xdir = MinigameUtils.getLang("minigame.treasurehunt.hint1.east");
            }
            if (mgm.getStartLocations().get(0).getZ() > block.getZ()) {
                dfcz = mgm.getStartLocations().get(0).getZ() - block.getZ();
                zdir = MinigameUtils.getLang("minigame.treasurehunt.hint1.north");
            } else {
                dfcz = block.getZ() - mgm.getStartLocations().get(0).getZ();
                zdir = MinigameUtils.getLang("minigame.treasurehunt.hint1.south");
            }
            String dir;

            if (dfcz > dfcx) {
                if (dfcx > dfcz / 2) {
                    dir = zdir + xdir.toLowerCase();
                } else {
                    dir = zdir;
                }
            } else {
                if (dfcz > dfcx / 2) {
                    dir = zdir + xdir.toLowerCase();
                } else {
                    dir = xdir;
                }
            }
            String hint = MinigameUtils.formStr("minigame.treasurehunt.hint1.hint", mgm.getName(true), dir, thm.getLocation());
            MinigameUtils.broadcast(hint, mgm, "minigame.treasure.announce");
            thm.addHint(ChatColor.GRAY + hint);
        } else if (time == hintTime2) {
            block.setY(block.getY() - 1);
            String hint = MinigameUtils.formStr("minigame.treasurehunt.hint2", mgm.getName(true), block.getBlock().getType().toString().toLowerCase().replace("_", " "));
            MinigameUtils.broadcast(hint, mgm, "minigame.treasure.announce");
            thm.addHint(ChatColor.GRAY + hint);
            block.setY(block.getY() + 1);
        } else if (time == hintTime3) {
            int height = block.getBlockY();
            String dir;
            int dist;
            if (height > 62) {
                dist = height - 62;
                dir = MinigameUtils.getLang("minigame.treasurehunt.hint3.above");
            } else {
                dist = 62 - height;
                dir = MinigameUtils.getLang("minigame.treasurehunt.hint3.below");
            }
            String hint = MinigameUtils.formStr("minigame.treasurehunt.hint3.hint", mgm.getName(true), dist, dir);
            MinigameUtils.broadcast(hint, mgm, "minigame.treasure.announce");
            thm.addHint(ChatColor.GRAY + hint);
        } else if (time == hintTime4) {
            String hint = MinigameUtils.formStr("minigame.treasurehunt.hint4", mgm.getName(true), block.getBlock().getBiome().toString().toLowerCase().replace("_", " "));
            MinigameUtils.broadcast(hint, mgm, "minigame.treasure.announce");
            thm.addHint(ChatColor.GRAY + hint);
        }
    }

    @EventHandler
    private void timerExpire(TimerExpireEvent event) {
        if (event.getMinigame().getType() != MinigameType.GLOBAL &&
                !event.getMinigame().getMechanicName().equals(getMechanic())) return;

        Minigame mgm = event.getMinigame();
        TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(mgm);

        if (thm.hasTreasureLocation()) {
            mgm.setMinigameTimer(new MinigameTimer(mgm, thm.getTreasureWaitTime()));
            Location old = thm.getTreasureLocation();
            removeTreasure(mgm);
            if (!thm.isTreasureFound()) {
                MinigameUtils.broadcast(MinigameUtils.formStr("minigame.treasurehunt.plyDespawn", mgm.getName(true)) + "\n" +
                        ChatColor.GRAY + MinigameUtils.formStr("minigame.treasurehunt.plyDespawnCoords",
                        old.getBlockX(), old.getBlockY(), old.getBlockZ()), mgm, "minigame.treasure.announce");
            }
            thm.setTreasureFound(false);
        } else {
            spawnTreasure(mgm);
        }
    }

    @EventHandler
    private void interactEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block cblock = event.getClickedBlock();
            boolean cancelled = (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY);
            if (cblock != null && cblock.getState() instanceof Chest && !cancelled) {
                for (Minigame minigame : mdata.getAllMinigames().values()) {
                    if (minigame.getType() == MinigameType.GLOBAL &&
                            minigame.getMechanicName().equalsIgnoreCase(getMechanic()) &&
                            minigame.getMinigameTimer() != null) {
                        TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(minigame);
                        if (!thm.isTreasureFound() && thm.hasTreasureLocation()) {
                            int x1 = thm.getTreasureLocation().getBlockX();
                            int x2 = cblock.getLocation().getBlockX();
                            int y1 = thm.getTreasureLocation().getBlockY();
                            int y2 = cblock.getLocation().getBlockY();
                            int z1 = thm.getTreasureLocation().getBlockZ();
                            int z2 = cblock.getLocation().getBlockZ();
                            if (x2 == x1 && y2 == y1 && z2 == z1) {
                                MinigameUtils.broadcast(MinigameUtils.formStr("minigame.treasurehunt.plyFound",
                                        event.getPlayer().getDisplayName(),
                                        minigame.getName(true)), minigame,
                                        "minigame.treasure.announce");
                                event.setCancelled(true);
                                Chest chest = (Chest) cblock.getState();
                                event.getPlayer().openInventory(chest.getInventory());

                                thm.setTreasureFound(true);
                                minigame.getMinigameTimer().setTimeLeft(300);
                            }
                        }
                    }
                }
            }
        }
    }

}
