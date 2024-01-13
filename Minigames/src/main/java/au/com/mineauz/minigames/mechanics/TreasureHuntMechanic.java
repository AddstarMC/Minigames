package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.MinigameTimer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.MinigameTimerTickEvent;
import au.com.mineauz.minigames.events.TimerExpireEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MgModules;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;
import au.com.mineauz.minigames.minigame.reward.ItemReward;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.RewardsModule;
import au.com.mineauz.minigames.minigame.reward.scheme.StandardRewardScheme;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                if (loaded) {
                    c = old.getChunk();
                    c.setForceLoaded(true);
                }
                if (old.getBlock().getState() instanceof Chest chest) {
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
        if (thm.getMaxRadius() <= 0) {
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
        if (rpos.getBlock().getType().isAir()) {
            int minWorldHeight = rpos.getWorld().getMinHeight();
            // find first block below that is not air anymore to spawn on top of
            while (rpos.getBlock().getType().isAir() && rpos.getY() > minWorldHeight) {
                rpos.setY(rpos.getY() - 1);
            }
            rpos.setY(rpos.getY() + 1);
        } else {
            int maxWorldHeight = rpos.getWorld().getMaxHeight();
            // find first block above that is air to spawn into
            while (!rpos.getBlock().getType().isAir() && rpos.getY() < maxWorldHeight) {
                rpos.setY(rpos.getY() + 1);
            }
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> rpos.getBlock().setType(Material.CHEST), 1L);

        //Fill new container
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (rpos.getBlock().getState() instanceof Container container) {

                // TODO: Treasure hunt needs own rewards specification
                RewardsModule rewards = RewardsModule.getModule(mgm);
                if (rewards.getScheme() instanceof StandardRewardScheme) {
                    if (!((StandardRewardScheme) rewards.getScheme()).getPrimaryReward().getRewards().isEmpty()) {
                        int numItems = (int) Math.min(container.getInventory().getSize(), Math.round(Math.random() * (thm.getMaxTreasure() - thm.getMinTreasure())) + thm.getMinTreasure());

                        final ItemStack[] items = new ItemStack[27];
                        for (int i = 0; i < numItems; i++) {
                            RewardType rew = ((StandardRewardScheme) rewards.getScheme()).getPrimaryReward().getReward().get(0);
                            if (rew instanceof ItemReward irew) {
                                items[i] = irew.getRewardItem();
                            }
                        }
                        Collections.shuffle(Arrays.asList(items));
                        container.getInventory().setContents(items);
                    }
                }
            }
        }, 0L);

        thm.setTreasureLocation(rpos);
        MinigameMessageManager.debugMessage(mgm.getName(false) + " treasure chest spawned at: " + rpos);
        MinigameMessageManager.broadcast(MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_SPAWN,
                        Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(maxradius)),
                        Placeholder.unparsed(MinigamePlaceHolderKey.LOCATION.getKey(), thm.getLocation())),
                mgm, "minigame.treasure.announce");

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
    public boolean checkCanStart(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
        return true;
    }

    @Override
    public MinigameModule displaySettings(Minigame minigame) {
        return minigame.getModule(MgModules.TREASURE_HUNT.getName());
    }

    @Override
    public void startMinigame(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
        final TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(minigame);
        if (thm.getLocation() != null) {
            spawnTreasure(minigame);

            if (Bukkit.getOnlinePlayers().isEmpty())
                minigame.getMinigameTimer().stopTimer();
        } else {
            if (caller == null) {
                Minigames.getCmpnntLogger().info("Treasure Hunt \"" + minigame.getName(false) + "\" requires a location name to run!");
            } else {
                MinigameMessageManager.sendMgMessage(caller, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_TREASUREHUNT_ERROR_NOLOCATION);
            }
        }
    }

    @Override
    public void stopMinigame(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
        TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(minigame);

        minigame.getMinigameTimer().stopTimer();
        minigame.setMinigameTimer(null);
        thm.clearHints();

        if (thm.hasTreasureLocation()) {
            removeTreasure(minigame);
            if (!thm.isTreasureFound()) {
                MinigameMessageManager.broadcast(MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_REMOVED,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(true))),
                        minigame, "minigame.treasure.announce");
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
    private void timerTick(@NotNull MinigameTimerTickEvent event) {
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
                xdir = MinigameMessageManager.getUnformattedMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_WEST);
            } else {
                dfcx = block.getX() - mgm.getStartLocations().get(0).getX();
                xdir = MinigameMessageManager.getUnformattedMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_EAST);
            }
            if (mgm.getStartLocations().get(0).getZ() > block.getZ()) {
                dfcz = mgm.getStartLocations().get(0).getZ() - block.getZ();
                zdir = MinigameMessageManager.getUnformattedMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_NORTH);
            } else {
                dfcz = block.getZ() - mgm.getStartLocations().get(0).getZ();
                zdir = MinigameMessageManager.getUnformattedMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_SOUTH);
            }
            Component dir;
            MiniMessage miniMessage = MiniMessage.miniMessage();

            if (dfcz > dfcx) {
                if (dfcx > dfcz / 2) {
                    dir = miniMessage.deserialize(zdir + xdir.toLowerCase());
                } else {
                    dir = miniMessage.deserialize(zdir);
                }
            } else {
                if (dfcz > dfcx / 2) {
                    dir = miniMessage.deserialize(zdir + xdir.toLowerCase());
                } else {
                    dir = miniMessage.deserialize(xdir);
                }
            }
            Component hint1 = MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_HINT1,
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName(true)),
                    Placeholder.component(MinigamePlaceHolderKey.DIRECTION.getKey(), dir),
                    Placeholder.parsed(MinigamePlaceHolderKey.LOCATION.getKey(), thm.getLocation()));
            MinigameMessageManager.broadcast(hint1, mgm, "minigame.treasure.announce");
            thm.addHint(hint1);
        } else if (time == hintTime2) {
            block.setY(block.getY() - 1);
            Component hint2 = MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_HINT2,
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName(true)),
                    Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), block.getBlock().getType().toString().toLowerCase().replace("_", " ")));
            MinigameMessageManager.broadcast(hint2, mgm, "minigame.treasure.announce");
            thm.addHint(hint2);
            block.setY(block.getY() + 1);
        } else if (time == hintTime3) {
            int height = block.getBlockY();
            Component dir;
            int dist;
            if (height > 62) {
                dist = height - 62;
                dir = MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_ABOVE);
            } else {
                dist = 62 - height;
                dir = MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_BELOW);
            }
            Component hint3 = MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_HINT3,
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName(true)),
                    Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(dist)),
                    Placeholder.component(MinigamePlaceHolderKey.DIRECTION.getKey(), dir));
            MinigameMessageManager.broadcast(hint3, mgm, "minigame.treasure.announce");
            thm.addHint(hint3);
        } else if (time == hintTime4) {
            Component hint4 = MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_HINT4,
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName(true)),
                    Placeholder.unparsed(MinigamePlaceHolderKey.BIOME.getKey(), block.getBlock().getBiome().toString().toLowerCase().replace("_", " ")));
            MinigameMessageManager.broadcast(hint4, mgm, "minigame.treasure.announce");
            thm.addHint(hint4);
        }
    }

    @EventHandler
    private void timerExpire(@NotNull TimerExpireEvent event) {
        if (event.getMinigame().getType() != MinigameType.GLOBAL &&
                !event.getMinigame().getMechanicName().equals(getMechanic())) return;

        Minigame mgm = event.getMinigame();
        TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(mgm);

        if (thm.hasTreasureLocation()) {
            mgm.setMinigameTimer(new MinigameTimer(mgm, thm.getTreasureWaitTime()));
            Location old = thm.getTreasureLocation();
            removeTreasure(mgm);
            if (!thm.isTreasureFound()) {
                MinigameMessageManager.broadcast(MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_DESPAWN,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName(true)),
                                Placeholder.component(MinigamePlaceHolderKey.LOCATION.getKey(), MinigameMessageManager.formatBlockPostion(old))),
                        mgm, "minigame.treasure.announce");
            }
            thm.setTreasureFound(false);
        } else {
            spawnTreasure(mgm);
        }
    }

    @EventHandler
    private void interactEvent(@NotNull PlayerInteractEvent event) {
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
                                MinigameMessageManager.broadcast(MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERFOUND,
                                                Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), event.getPlayer().displayName()),
                                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(true))),
                                        minigame, "minigame.treasure.announce"); //todo Permission manager
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
