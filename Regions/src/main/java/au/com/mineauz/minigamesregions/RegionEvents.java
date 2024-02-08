package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.*;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.events.EnterRegionEvent;
import au.com.mineauz.minigamesregions.events.LeaveRegionEvent;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.triggers.MgRegTrigger;
import au.com.mineauz.minigamesregions.triggers.Trigger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

public class RegionEvents implements Listener {

    private final Minigames plugin = Minigames.getPlugin();
    private final MinigamePlayerManager pdata = plugin.getPlayerManager();

    private void executeRegionChanges(Minigame mg, MinigamePlayer mgPlayer) {
        for (Region region : RegionModule.getMinigameModule(mg).getRegions()) {
            if (region.playerInRegion(mgPlayer)) {
                region.execute(MgRegTrigger.PLAYER_REGION_MOVE_INSIDE, mgPlayer);

                if (!region.hasPlayer(mgPlayer)) {
                    region.addPlayer(mgPlayer);
                    region.execute(MgRegTrigger.PLAYER_REGION_ENTER, mgPlayer);
                    EnterRegionEvent ev = new EnterRegionEvent(mgPlayer, region);
                    Bukkit.getPluginManager().callEvent(ev);
                }
            } else {
                if (region.hasPlayer(mgPlayer)) {
                    region.removePlayer(mgPlayer);
                    region.execute(MgRegTrigger.PLAYER_REGION_LEAVE, mgPlayer);
                    LeaveRegionEvent ev = new LeaveRegionEvent(mgPlayer, region);
                    Bukkit.getPluginManager().callEvent(ev);
                }
            }
        }
    }

    private RegionModule getRegionModule(Minigame minigame) {
        return RegionModule.getMinigameModule(minigame);
    }

    @EventHandler(ignoreCancelled = true)
    private void playerMove(PlayerMoveEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame()) {
            Minigame mg = mgPlayer.getMinigame();
            executeRegionChanges(mg, mgPlayer);
        }
    }

    @EventHandler
    private void playerSpawn(PlayerRespawnEvent event) {
        final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame()) {
            final Minigame mg = mgPlayer.getMinigame();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (!mgPlayer.isInMinigame()) {
                    return;
                }

                executeRegionChanges(mg, mgPlayer);

                for (Node node : RegionModule.getMinigameModule(mgPlayer.getMinigame()).getNodes()) {
                    node.execute(MgRegTrigger.PLAYER_RESPAWN, mgPlayer);
                }
                for (Region region : RegionModule.getMinigameModule(mgPlayer.getMinigame()).getRegions()) {
                    if (region.hasPlayer(mgPlayer))
                        region.execute(MgRegTrigger.PLAYER_RESPAWN, mgPlayer);
                }
            });
        }
    }

    @EventHandler
    private void playerDeath(PlayerDeathEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getEntity());
        boolean pvp = false;
        MinigamePlayer killer;

        if (mgPlayer.isInMinigame()) {
            killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
            if (killer != null && killer.isInMinigame()) {
                pvp = true;
            }

            for (Node node : RegionModule.getMinigameModule(mgPlayer.getMinigame()).getNodes()) {
                node.execute(MgRegTrigger.PLAYER_DEATH_GENERAL, mgPlayer);
                if (pvp) {
                    node.execute(MgRegTrigger.PLAYER_KILLS_PLAYER, killer);
                    node.execute(MgRegTrigger.PLAYER_DEATH_PVP, mgPlayer);
                }
            }
            for (Region region : RegionModule.getMinigameModule(mgPlayer.getMinigame()).getRegions()) {
                if (region.hasPlayer(mgPlayer))
                    region.execute(MgRegTrigger.PLAYER_DEATH_GENERAL, mgPlayer);
                if (pvp) {
                    region.execute(MgRegTrigger.PLAYER_KILLS_PLAYER, killer);
                    region.execute(MgRegTrigger.PLAYER_DEATH_PVP, mgPlayer);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerJoin(JoinMinigameEvent event) {
        final MinigamePlayer mgPlayer = event.getMinigamePlayer();
        if (mgPlayer == null) return;
        final Minigame mg = event.getMinigame();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            executeRegionChanges(mg, mgPlayer);

            for (Node node : RegionModule.getMinigameModule(mg).getNodes()) {
                node.execute(MgRegTrigger.PLAYER_GAME_JOIN, mgPlayer);
            }
            for (Region region : RegionModule.getMinigameModule(mg).getRegions()) {
                if (region.hasPlayer(mgPlayer))
                    region.execute(MgRegTrigger.PLAYER_GAME_JOIN, mgPlayer);
            }
        });
        if (event.getMinigame().getPlayers().isEmpty()) {
            for (Region region : RegionModule.getMinigameModule(event.getMinigame()).getRegions()) {
                for (RegionExecutor ex : region.getExecutors()) {
                    if (ex.getTrigger() == MgRegTrigger.TIME_TICK) {
                        region.startTickTask();
                    }
                    if (ex.getTrigger() == MgRegTrigger.TIME_GAMETICK) {
                        region.startGameTickTask();
                    }
                }
            }
        }
    }

    @EventHandler
    private void minigameStart(StartMinigameEvent event) {
        for (Node node : RegionModule.getMinigameModule(event.getMinigame()).getNodes()) {
            for (MinigamePlayer player : event.getPlayers()) {
                node.execute(MgRegTrigger.GAME_START, player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerQuit(QuitMinigameEvent event) {
        if (RegionModule.getMinigameModule(event.getMinigame()) == null) {
            MinigameMessageManager.debugMessage(event.getMinigame() + " called region event with no RegionModule loaded... was this intended?");
            return;
        }
        MinigamePlayer mgPlayer = event.getMinigamePlayer();
        if (mgPlayer == null) return;
        Minigame mg = mgPlayer.getMinigame();
        for (Region r : RegionModule.getMinigameModule(mg).getRegions()) {
            if (r.hasPlayer(mgPlayer))
                r.removePlayer(mgPlayer);
        }
        for (Node node : RegionModule.getMinigameModule(event.getMinigame()).getNodes()) {
            node.execute(MgRegTrigger.PLAYER_GAME_QUIT, event.getMinigamePlayer());
            if (event.getMinigame().getPlayers().size() > 1) {
                for (NodeExecutor exec : node.getExecutors())
                    exec.removeTrigger(event.getMinigamePlayer());
            } else {
                for (NodeExecutor exec : node.getExecutors())
                    exec.clearTriggers();
                node.setEnabled(true);
            }
        }
        for (Region region : RegionModule.getMinigameModule(event.getMinigame()).getRegions()) {
            if (region.playerInRegion(mgPlayer))
                region.execute(MgRegTrigger.PLAYER_GAME_QUIT, event.getMinigamePlayer());
            if (event.getMinigame().getPlayers().size() > 1) {
                for (RegionExecutor exec : region.getExecutors())
                    exec.removeTrigger(event.getMinigamePlayer());
            } else {
                for (RegionExecutor exec : region.getExecutors()) {
                    exec.clearTriggers();
                }
                region.removeTickTask();
                region.removeGameTickTask();
                region.setEnabled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playersEndPhase(EndPhaseMinigameEvent event) {
        if (RegionModule.getMinigameModule(event.getMinigame()) == null) {
            MinigameMessageManager.debugMessage(event.getMinigame() + " called region event with no RegionModule loaded... was this intended?");
            return;
        }

        for (MinigamePlayer mgPlayer : event.getWinners()) {
            Minigame mg = mgPlayer.getMinigame();
            for (Region r : RegionModule.getMinigameModule(mg).getRegions()) {
                if (r.hasPlayer(mgPlayer)) {
                    r.removePlayer(mgPlayer);
                }
            }
        }

        for (Node node : RegionModule.getMinigameModule(event.getMinigame()).getNodes()) {
            node.execute(MgRegTrigger.GAME_ENDPHASE, null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playersEnded(EndedMinigameEvent event) {
        if (RegionModule.getMinigameModule(event.getMinigame()) == null) {
            MinigameMessageManager.debugMessage(event.getMinigame() + " called region event with no RegionModule loaded... was this intended?");
            return;
        }
        for (Node node : RegionModule.getMinigameModule(event.getMinigame()).getNodes()) {
            node.execute(MgRegTrigger.GAME_ENDED, null);

            for (NodeExecutor exec : node.getExecutors()) {
                exec.clearTriggers();
            }
        }
        for (Region region : RegionModule.getMinigameModule(event.getMinigame()).getRegions()) {
            for (RegionExecutor exec : region.getExecutors()) {
                exec.clearTriggers();
            }
        }
    }

    @EventHandler()
    private void interactNode(PlayerInteractEvent event) {
        final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (!mgPlayer.isInMinigame()) {
            return;
        }

        if (!event.isCancelled()) {
            if (event.getAction() == Action.PHYSICAL) {
                if (Tag.PRESSURE_PLATES.isTagged(event.getClickedBlock().getType())) {
                    trigger(mgPlayer, event.getClickedBlock(), MgRegTrigger.PLAYER_BLOCK_INTERACT);
                }
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (Tag.BUTTONS.isTagged(event.getClickedBlock().getType()) ||
                        event.getClickedBlock().getType() == Material.LEVER) {
                    trigger(mgPlayer, event.getClickedBlock(), MgRegTrigger.PLAYER_BLOCK_INTERACT);
                }
            }
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            trigger(mgPlayer, event.getClickedBlock(), MgRegTrigger.PLAYER_BLOCK_CLICK_LEFT);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            trigger(mgPlayer, event.getClickedBlock(), MgRegTrigger.PLAYER_BLOCK_CLICK_RIGHT);
        }
    }

    private void trigger(final MinigamePlayer player, final Block block, final Trigger trigger) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!player.isInMinigame()) {
                return;
            }

            for (Node node : RegionModule.getMinigameModule(player.getMinigame()).getNodes()) {
                if (node.getLocation().getBlock().equals(block)) {
                    node.execute(trigger, player);
                }
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    private void blockBreak(BlockBreakEvent event) {
        final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame()) {
            final Location loc2 = event.getBlock().getLocation();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (!mgPlayer.isInMinigame()) {
                    return;
                }

                for (Node node : RegionModule.getMinigameModule(mgPlayer.getMinigame()).getNodes()) {
                    if (node.getLocation().getWorld() == loc2.getWorld()) {
                        Location loc1 = node.getLocation();
                        if (loc1.getBlockX() == loc2.getBlockX() &&
                                loc1.getBlockY() == loc2.getBlockY() &&
                                loc1.getBlockZ() == loc2.getBlockZ()) {
                            node.execute(MgRegTrigger.PLAYER_BLOCK_BREAK, mgPlayer);
                        }
                    }
                }

                for (Region region : RegionModule.getMinigameModule(mgPlayer.getMinigame()).getRegions()) {
                    if (region.locationInRegion(loc2)) {
                        region.execute(MgRegTrigger.PLAYER_BLOCK_BREAK, mgPlayer);
                    }
                }
            });
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void blockPlace(BlockPlaceEvent event) {
        final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame()) {
            final Location loc2 = event.getBlock().getLocation();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (!mgPlayer.isInMinigame()) {
                    return;
                }

                for (Node node : RegionModule.getMinigameModule(mgPlayer.getMinigame()).getNodes()) {
                    if (node.getLocation().getWorld() == loc2.getWorld()) {
                        Location loc1 = node.getLocation();
                        if (loc1.getBlockX() == loc2.getBlockX() &&
                                loc1.getBlockY() == loc2.getBlockY() &&
                                loc1.getBlockZ() == loc2.getBlockZ()) {
                            node.execute(MgRegTrigger.PLAYER_BLOCK_PLACE, mgPlayer);
                        }
                    }
                }

                for (Region region : RegionModule.getMinigameModule(mgPlayer.getMinigame()).getRegions()) {
                    if (region.locationInRegion(loc2)) {
                        region.execute(MgRegTrigger.PLAYER_BLOCK_PLACE, mgPlayer);
                    }
                }
            });
        }
    }

    @EventHandler
    private void minigameTimerTick(MinigameTimerTickEvent event) {
        for (Node node : getRegionModule(event.getMinigame()).getNodes()) {
            for (MinigamePlayer player : event.getMinigame().getPlayers()) {
                node.execute(MgRegTrigger.TIME_MINIGAMETIMER, player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void itemPickupEvent(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(((Player) event.getEntity()));
        if (mgPlayer.isInMinigame()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, executeScriptObjects(mgPlayer, MgRegTrigger.PLAYER_ITEM_PICKUP));
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void itemPickupEvent(PlayerDropItemEvent event) {
        final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMinigame()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, executeScriptObjects(mgPlayer, MgRegTrigger.PLAYER_ITEM_DROP));
        }
    }

    private Runnable executeScriptObjects(@NotNull MinigamePlayer mgPlayer, @NotNull Trigger trig) {
        return () -> {
            if (!mgPlayer.isInMinigame()) {
                return;
            }

            for (Node node : getRegionModule(mgPlayer.getMinigame()).getNodes()) {
                node.execute(trig, mgPlayer);
            }

            for (Region region : getRegionModule(mgPlayer.getMinigame()).getRegions()) {
                if (region.hasPlayer(mgPlayer)) {
                    region.execute(trig, mgPlayer);
                }
            }
        };
    }


    @EventHandler(priority = EventPriority.LOWEST)
    private void playerDisconnect(PlayerQuitEvent event) {
        Main.getPlugin().getDisplayManager().hideAll(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void playerXpChange(PlayerExpChangeEvent event) {
        final MinigamePlayer player = pdata.getMinigamePlayer(event.getPlayer());
        if (!player.isInMinigame()) {
            return;
        }

        executeTrigger(MgRegTrigger.PLAYER_XP_CHANGE, player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void playerFoodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(player);
            if (!mgPlayer.isInMinigame()) {
                return;
            }

            executeTrigger(MgRegTrigger.PLAYER_FOOD_CHANGE, mgPlayer);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void playerDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(player);
            if (!mgPlayer.isInMinigame()) {
                return;
            }

            executeTrigger(MgRegTrigger.PLAYER_DAMAGED, mgPlayer);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void playerDropFlag(DropFlagEvent ev) {
        executeTrigger(MgRegTrigger.PLAYER_CTFFLAG_DROP, ev.getPlayer());
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void playerGetFlag(TakeCTFFlagEvent event) {
        executeTrigger(MgRegTrigger.PLAYER_CTFFLAG_TAKE, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void entityGlide(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player player) {
            final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(player);
            if (!mgPlayer.isInMinigame()) {
                return;
            }
            if (event.isGliding()) {
                executeTrigger(MgRegTrigger.PLAYER_GLIDE_START, mgPlayer);
            } else {
                executeTrigger(MgRegTrigger.PLAYER_GLIDE_STOP, mgPlayer);
            }
        }
    }


    private void executeTrigger(final Trigger trigger, final MinigamePlayer player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!player.isInMinigame()) {
                return;
            }

            RegionModule module = getRegionModule(player.getMinigame());

            if (trigger.useInNodes()) {
                for (Node node : module.getNodes()) {
                    node.execute(trigger, player);
                }
            }

            if (trigger.useInRegions()) {
                for (Region region : module.getRegions()) {
                    if (region.hasPlayer(player)) {
                        region.execute(trigger, player);
                    }
                }
            }
        });
    }
}
