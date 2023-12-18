package au.com.mineauz.minigames;

import au.com.mineauz.minigames.events.RevertCheckpointEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.GameOverModule;
import au.com.mineauz.minigames.minigame.modules.ResourcePackModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.minigame.modules.WeatherTimeModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.OfflineMinigamePlayer;
import au.com.mineauz.minigames.tool.MinigameTool;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Events implements Listener {
    private static final Minigames plugin = Minigames.getPlugin();
    private final MinigamePlayerManager pdata = plugin.getPlayerManager();
    private final MinigameManager mdata = plugin.getMinigameManager();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerResourcePack(@NotNull PlayerResourcePackStatusEvent event) { //todo 1.20.3 + add ressource pack not set (redo with multible Ressoucepacks in mind.)
        final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        List<MinigamePlayer> required = plugin.getPlayerManager().getApplyingPack();
        if (mgPlayer.isInMinigame()) {
            if (required.contains(mgPlayer)) {
                ResourcePackModule module = ResourcePackModule.getMinigameModule(mgPlayer.getMinigame());
                if (!module.isEnabled()) return;
                if (!module.isForced()) return;
                switch (event.getStatus()) {
                    case ACCEPTED, SUCCESSFULLY_LOADED -> required.remove(mgPlayer);
                    case DECLINED -> {
                        Minigames.getPlugin().getPlayerManager().quitMinigame(mgPlayer, true);
                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_RESOURCE_DECLINED);
                        required.remove(mgPlayer);
                    }
                    case FAILED_DOWNLOAD -> {
                        Minigames.getPlugin().getPlayerManager().quitMinigame(mgPlayer, true);
                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_RESOURCE_FAILED);
                        required.remove(mgPlayer);
                    }
                }
            }

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerDeath(PlayerDeathEvent event) {
        final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getEntity().getPlayer());
        if (mgPlayer.isInMinigame()) {
            Minigame mgm = mgPlayer.getMinigame();
            if (!mgm.hasDeathDrops()) {
                if (mgm.keepInventory()) {
                    List<ItemStack> drops = Arrays.asList(mgPlayer.getPlayer().getInventory().getContents());
                    PlayerLoadout l = new PlayerLoadout("deathDrops");
                    for (int i = 0; i < drops.size(); i++) {
                        l.addItem(drops.get(i), i);
                    }
                    mgPlayer.setLoadout(l);
                }
                event.getDrops().clear();
            }

            Component msg = event.deathMessage();
            event.deathMessage(Component.empty());
            event.setDroppedExp(0);

            mgPlayer.addDeath();
            mgPlayer.addRevert();

            pdata.partyMode(mgPlayer);

            if (mgPlayer.getPlayer().getKiller() != null) {
                MinigamePlayer killer = pdata.getMinigamePlayer(mgPlayer.getPlayer().getKiller());
                if (killer != null)
                    killer.addKill();
            }

            if (msg != null && !PlainTextComponentSerializer.plainText().serialize(msg).isEmpty()) { //components really need a better way to check if they are empty
                mdata.sendMinigameMessage(mgm, msg, MinigameMessageType.ERROR);
            }
            if (mgm.getState() == MinigameState.STARTED) {
                if (mgm.getLives() > 0 && mgm.getLives() <= mgPlayer.getDeaths()) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.PLAYER_QUIT_OUTOFLIVES);
                    if (!event.getDrops().isEmpty() && mgm.getPlayers().size() == 1) {
                        event.getDrops().clear();
                    }
                    pdata.quitMinigame(mgPlayer, false);
                } else if (mgm.getLives() > 0) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_LIVESLEFT,
                            Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(mgm.getLives() - mgPlayer.getDeaths())));
                }
            } else if (mgm.getState() == MinigameState.ENDED) {
                plugin.getPlayerManager().quitMinigame(mgPlayer, true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerSpawn(@NotNull PlayerRespawnEvent event) {
        final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMinigame()) {
            final WeatherTimeModule mod = WeatherTimeModule.getMinigameModule(mgPlayer.getMinigame());
            if (mod != null && mod.isUsingCustomWeather()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> mgPlayer.getPlayer().setPlayerWeather(mod.getCustomWeather()));
            }

            if (mgPlayer.getMinigame().getState() == MinigameState.ENDED) {
                plugin.getPlayerManager().quitMinigame(mgPlayer, true);
            }
        }
        if (mgPlayer.isRequiredQuit()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, mgPlayer::restorePlayerData);
            event.setRespawnLocation(mgPlayer.getQuitPos());

            mgPlayer.setRequiredQuit(false);
            mgPlayer.setQuitPos(null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerDropItem(@NotNull PlayerDropItemEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMinigame()) {
            Minigame mgm = pdata.getMinigamePlayer(event.getPlayer()).getMinigame();
            if (!mgm.hasItemDrops() ||
                    mgm.isSpectator(pdata.getMinigamePlayer(event.getPlayer()))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void itemPickup(@NotNull EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            MinigamePlayer mgPlayer = pdata.getMinigamePlayer(player);
            if (mgPlayer.isInMinigame()) {
                Minigame mgm = mgPlayer.getMinigame();
                if (!mgm.hasItemPickup() ||
                        mgm.isSpectator(mgPlayer)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(@NotNull PlayerQuitEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMinigame()) {
            if (mgPlayer.getPlayer().isDead()) {
                mgPlayer.getOfflineMinigamePlayer().setLoginLocation(mgPlayer.getMinigame().getQuitLocation());
                mgPlayer.getOfflineMinigamePlayer().savePlayerData();
            }
            pdata.quitMinigame(pdata.getMinigamePlayer(event.getPlayer()), false);
        } else if (mgPlayer.isRequiredQuit()) {
            mgPlayer.getOfflineMinigamePlayer().setLoginLocation(mgPlayer.getQuitPos());
            mgPlayer.getOfflineMinigamePlayer().savePlayerData();
        }

        pdata.removeMinigamePlayer(event.getPlayer());
        plugin.display.removeAll(event.getPlayer());

        if (Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            for (String mgm : mdata.getAllMinigames().keySet()) {
                if (mdata.getMinigame(mgm).getType() == MinigameType.GLOBAL) {
                    if (mdata.getMinigame(mgm).getMinigameTimer() != null)
                        mdata.getMinigame(mgm).getMinigameTimer().stopTimer();
                }
            }
        }
        mgPlayer.saveClaimedRewards();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerConnect(@NotNull PlayerJoinEvent event) {
        pdata.addMinigamePlayer(event.getPlayer());
        File pldata = new File(plugin.getDataFolder() + "/playerdata/inventories/" + event.getPlayer().getUniqueId() + ".yml");
        final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (pldata.exists()) {
            mgPlayer.setOfflineMinigamePlayer(new OfflineMinigamePlayer(event.getPlayer().getUniqueId()));
            Location floc = mgPlayer.getOfflineMinigamePlayer().getLoginLocation();
            mgPlayer.setRequiredQuit(true);
            mgPlayer.setQuitPos(floc);

            if (!mgPlayer.getPlayer().isDead() && mgPlayer.isRequiredQuit()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, mgPlayer::restorePlayerData);
                mgPlayer.teleport(mgPlayer.getQuitPos());

                mgPlayer.setRequiredQuit(false);
                mgPlayer.setQuitPos(null);
            }

            plugin.getLogger().info(mgPlayer.getName() + "'s data has been restored from file.");
        }

        mgPlayer.loadClaimedRewards();

        if (Bukkit.getServer().getOnlinePlayers().size() == 1) {
            for (Minigame mgm : mdata.getAllMinigames().values()) {
                if (mgm != null && mgm.getType() == MinigameType.GLOBAL) {
                    if (mgm.getMinigameTimer() != null) mgm.getMinigameTimer().startTimer();
                }
            }
            //for(String mgm : mdata.getAllMinigames().keySet()){
            //    if(mdata.getMinigame(mgm).getType() == MinigameType.GLOBAL){
//                    if(minigameManager.getMinigame(mgm).getThTimer() != null){
//                        minigameManager.getMinigame(mgm).getThTimer().pauseTimer(false);
//                    }
            //        if(mdata.getMinigame(mgm).getMinigameTimer() != null)
            //            mdata.getMinigame(mgm).getMinigameTimer().startTimer();
            //    }
            //       }
        }
    }

    @EventHandler
    public void playerInteract(@NotNull PlayerInteractEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame() && !mgPlayer.canInteract()) {
            event.setCancelled(true);
            return;
        }
        if (mgPlayer.isInMenu() && mgPlayer.getNoClose() && mgPlayer.getManualEntry() != null) {
            event.setCancelled(true);
            ply.setNoClose(false);
            if (event.getClickedBlock() != null) {
                mgPlayer.setNoClose(false);
                mgPlayer.getManualEntry().checkValidEntry(event.getClickedBlock().getBlockData().getAsString());
                mgPlayer.setManualEntry(null);
            }
            return;
        }
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.DRAGON_EGG) {
            if (!mgPlayer.getMinigame().allowDragonEggTeleport()) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && !(event.useInteractedBlock() == Event.Result.DENY)) {
            Block cblock = event.getClickedBlock();
            if (cblock.getState() instanceof Sign sign && sign.getSide(Side.FRONT).getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE + "[Minigame]")) {
                // wax signs automatically
                sign.setWaxed(true);
                sign.update();
                if (event.getPlayer().hasPermission("minigame.sign.use.details")) {
                    if ((sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Join") || sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Bet")) && !mgPlayer.isInMinigame()) {
                        Minigame mgm = mdata.getMinigame(sign.getLine(2));

                        if (mgm != null && (!mgm.getUsePermissions() || event.getPlayer().hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))) {
                            if (!mgm.isEnabled()) {
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_ERROR_NOTENABLED);
                            } else {
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.NONE, MinigameLangKey.MINIGAME_INFO_HEADER);

                                if (mgm.getType() != MinigameType.SINGLEPLAYER) {

                                    Component status;
                                    if (!mgm.hasPlayers()) {
                                        status = MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_INFO_STATUS_EMPTY);
                                    } else if (mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() > 0) {
                                        status = MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_INFO_STATUS_WAITINGFORPLAYERS);
                                    } else {
                                        status = MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_INFO_STATUS_STARTED);
                                    }
                                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.NONE, MinigameLangKey.MINIGAME_INFO_STATUS_TITLE,
                                            Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), status));

                                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.NONE, MinigameLangKey.MINIGAME_INFO_LATEJOIN_MSG,
                                            Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), MinigameMessageManager.getMgMessage(
                                                    mgm.canLateJoin() ?
                                                            MinigameLangKey.MINIGAME_INFO_LATEJOIN_ENABLED :
                                                            MinigameLangKey.MINIGAME_INFO_LATEJOIN_DISABLED)));
                                }

                                if (mgm.getMinigameTimer() != null) {
                                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.NONE, MinigameLangKey.TIME_TIMELEFT,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(mgm.getMinigameTimer().getTimeLeft())));
                                }

                                if (mgm.isTeamGame()) {
                                    StringBuilder sc = new StringBuilder();
                                    int c = 0;
                                    for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                                        c++;
                                        sc.append(t.getColor().getColor().toString()).append(" ").append(t.getScore()).append(ChatColor.WHITE);
                                        if (c != TeamsModule.getMinigameModule(mgm).getTeams().size()) {
                                            sc.append(" : ");
                                        }
                                    }
                                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.NONE, MinigameLangKey.MINIGAME_INFO_SCORE,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), sc.toString()));
                                }

                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.NONE, MinigameLangKey.MINIGAME_INFO_PLAYERCOUNT,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(mgm.getPlayers().size())),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(
                                                mgm.getType() == MinigameType.SINGLEPLAYER ?
                                                        0 :
                                                        mgm.getMaxPlayers())));

                                Component players;
                                if (mgm.hasPlayers()) {
                                    players = Component.text(mgm.getPlayers().stream().map(MinigamePlayer::getName).collect(Collectors.joining(", ")));
                                } else {
                                    players = MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_INFO_PLAYERS_NONE);
                                }
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.NONE, MinigameLangKey.MINIGAME_INFO_PLAYERS_TITLE,
                                        Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), players));
                            }
                        } else if (mgm == null) {
                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                                    Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), sign.getSide(Side.FRONT).line(2)));
                        } else if (mgm.getUsePermissions()) {
                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.PERMISSION.getKey(), "minigame.join." + mgm.getName(false).toLowerCase()));
                        }
                    }
                }
            }
        }

        ItemStack item = event.getItem();
        //nullcheck in isMinigameTool()
        if (MinigameUtils.isMinigameTool(item) && mgPlayer.getPlayer().hasPermission("minigame.tool")) {
            MinigameTool tool = new MinigameTool(item);
            event.setCancelled(true);

            if (event.getPlayer().isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                tool.openMenu(mgPlayer);
                event.setCancelled(true);
            } else if (event.getClickedBlock() != null && (Tag.ALL_SIGNS.isTagged(event.getClickedBlock().getType()))) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if (ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[Minigame]") && ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase("Join")) {
                    Minigame minigame = mdata.getMinigame(sign.getLine(2));
                    tool.setMinigame(minigame);
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_SELECTED_MINIGAME,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(true)));
                    event.setCancelled(true);
                }
            } else {
                checkTool(tool, mgPlayer);
                if (tool.getMode() != null && tool.getMinigame() != null) {
                    Minigame mg = tool.getMinigame();
                    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        tool.getMode().onRightClick(mgPlayer, mg, TeamsModule.getMinigameModule(mg).getTeam(tool.getTeam()), event);
                    } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        tool.getMode().onLeftClick(mgPlayer, mg, TeamsModule.getMinigameModule(mg).getTeam(tool.getTeam()), event);
                    }
                }
            }
        }

        //Spectator disables:
        if (mgPlayer.isInMinigame() && pdata.getMinigamePlayer(event.getPlayer()).getMinigame().isSpectator(pdata.getMinigamePlayer(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onTeleportAway(@NotNull PlayerTeleportEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame() && (event.getCause() == TeleportCause.COMMAND || event.getCause() == TeleportCause.PLUGIN || (!mgPlayer.getMinigame().isAllowedEnderpearls() && event.getCause() == TeleportCause.ENDER_PEARL))) {
            if (!mgPlayer.getAllowTeleport()) {
                Location from = event.getFrom();
                Location to = event.getTo();
                if (from.getWorld() != to.getWorld() || from.distance(to) > 2) {
                    event.setCancelled(true);
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTELEPORT);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onGMChange(@NotNull PlayerGameModeChangeEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMinigame() && !mgPlayer.getAllowGamemodeChange()) {
            event.setCancelled(true);
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOGAMEMODE);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onFlyToggle(@NotNull PlayerToggleFlightEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMinigame() && (!mgPlayer.getMinigame().isSpectator(mgPlayer) || !mgPlayer.getMinigame().canSpectateFly()) &&
                !mgPlayer.canFly()) {
            event.setCancelled(true);
            pdata.quitMinigame(mgPlayer, true);
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOFLY);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerRevert(@NotNull RevertCheckpointEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (event.getMinigamePlayer().isInMinigame() &&
                event.getMinigamePlayer().getMinigame().getType() == MinigameType.MULTIPLAYER &&
                !event.getMinigamePlayer().getMinigame().isAllowedMPCheckpoints() &&
                !event.getMinigamePlayer().isLatejoining()) {
            event.setCancelled(true);
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOREVERT,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), event.getMinigamePlayer().getMinigame().getType().getName()));
        } else if (!event.getMinigamePlayer().getMinigame().hasStarted()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void commandExecute(@NotNull PlayerCommandPreprocessEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMinigame()) {
            for (String comd : pdata.getDeniedCommands()) {
                if (event.getMessage().contains(comd)) {
                    event.setCancelled(true);
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOCOMMAND);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void entityDamageEntity(@NotNull EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Snowball sb) {
                MinigamePlayer mgPlayer = pdata.getMinigamePlayer((Player) event.getEntity());
                if (mgPlayer.isInMinigame() && mgPlayer.getMinigame().hasPaintBallMode()) {
                    if (sb.getShooter() instanceof Player player) {
                        MinigamePlayer shooter = pdata.getMinigamePlayer(player);
                        Minigame mgm = mgPlayer.getMinigame();

                        if (shooter == null) {
                            return;
                        }

                        if (shooter.isInMinigame() && shooter.getMinigame().equals(ply.getMinigame())) {
                            if (!shooter.canPvP()) {
                                event.setCancelled(true);
                                return;
                            }

                            Team plyTeam = mgPlayer.getTeam();
                            Team atcTeam = shooter.getTeam();
                            if (!mgm.isTeamGame() || plyTeam != atcTeam) {
                                int damage = mgm.getPaintBallDamage();
                                event.setDamage(damage);
                            }
                        }
                    }
                }
            } else if (event.getDamager() instanceof Player) {
                MinigamePlayer ply = pdata.getMinigamePlayer((Player) event.getDamager());
                if (ply.isInMinigame() && !ply.canPvP())
                    event.setCancelled(true);
                else if (ply.isInMinigame() && ply.getMinigame().getState() == MinigameState.ENDED &&
                        GameOverModule.getMinigameModule(ply.getMinigame()).isHumiliationMode() &&
                        GameOverModule.getMinigameModule(ply.getMinigame()).getLosers().contains(ply)) {
                    event.setCancelled(true);
                }
            } else if (event.getDamager() instanceof Arrow arrow) {
                if (arrow.getShooter() instanceof Player ply) {
                    MinigamePlayer mgpl = pdata.getMinigamePlayer(ply);

                    if (mgpl.isInMinigame() && !mgpl.canPvP())
                        event.setCancelled(true);
                }
            }
        }
        if (event.getDamager() instanceof Player player) {
            MinigamePlayer mgPlayer = pdata.getMinigamePlayer(player);
            ItemStack item = player.getEquipment().getItemInMainHand();
            if (MinigameUtils.isMinigameTool(item) && player.hasPermission("minigame.tool")) {
                if (mgPlayer.isInMinigame()) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.TOOL_ERROR_INMINIGAME);
                    return;
                }
                MinigameTool tool = new MinigameTool(item);
                if (player.isSneaking()) {
                    tool.openMenu(mgPlayer);
                    event.setCancelled(true);
                }

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerRightClickEntity(@NotNull PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(player);
        ItemStack item = player.getEquipment().getItemInMainHand();
        if (MinigameUtils.isMinigameTool(item) && player.hasPermission("minigame.tool")) {
            if (mgPlayer.isInMinigame()) {
                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.TOOL_ERROR_INMINIGAME);
                return;
            }
            MinigameTool tool = new MinigameTool(item);
            if (player.isSneaking()) {
                tool.openMenu(mgPlayer);
                event.setCancelled(true);
            }
        }
    }

    private void checkTool(@NotNull MinigameTool tool, @NotNull MinigamePlayer mgPlayer) {
        if (tool.getMinigame() == null) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_ERROR_NOMINIGAME);
        }
        if (tool.getMode() == null) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_ERROR_NOMODE);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerShoot(@NotNull ProjectileLaunchEvent event) {
        if (event.getEntityType() == EntityType.SNOWBALL) {
            Snowball snowball = (Snowball) event.getEntity();

            if (snowball.getShooter() instanceof Player player) {
                MinigamePlayer mgPlayer = pdata.getMinigamePlayer(player);

                if (mgPlayer.isInMinigame() && mgPlayer.getMinigame().hasUnlimitedAmmo()) {
                    //wait for the inventory to update
                    Bukkit.getScheduler().runTaskLater(Minigames.getPlugin(), () -> {
                        ItemStack itemInMainHand = mgPlayer.getPlayer().getInventory().getItemInMainHand();

                        if (itemInMainHand.getType() == Material.SNOWBALL) {
                            itemInMainHand.setAmount(16);
                            mgPlayer.getPlayer().updateInventory();
                        } else {
                            mgPlayer.getPlayer().getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));
                        }
                    }, 1L);
                }
            }

        } else if (event.getEntityType() == EntityType.EGG) {
            Egg egg = (Egg) event.getEntity();
            if (egg.getShooter() != null && egg.getShooter() instanceof Player player) {
                MinigamePlayer ply = pdata.getMinigamePlayer(player);

                if (ply.isInMinigame() && ply.getMinigame().hasUnlimitedAmmo()) {
                    //wait for the inventory to update
                    Bukkit.getScheduler().runTaskLater(Minigames.getPlugin(), () -> {
                        ItemStack itemInMainHand = ply.getPlayer().getInventory().getItemInMainHand();

                        if (itemInMainHand.getType() == Material.EGG) {
                            itemInMainHand.setAmount(16);
                            ply.getPlayer().updateInventory();
                        } else {
                            ply.getPlayer().getInventory().addItem(new ItemStack(Material.EGG, 1));
                        }
                    }, 1L);
                }
            }
        } //todo unlimited arrows
    }

    @EventHandler(ignoreCancelled = true)
    private void playerHurt(@NotNull EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            MinigamePlayer mgPlayer = pdata.getMinigamePlayer(player);

            if (mgPlayer.isInMinigame()) {
                Minigame mgm = mgPlayer.getMinigame();
                if (mgm.isSpectator(mgPlayer)) {
                    event.setCancelled(true);
                } else if ((!mgPlayer.getMinigame().hasStarted() && mgPlayer.getMinigame().getState() != MinigameState.ENDED) || mgPlayer.isLatejoining()) {
                    event.setCancelled(true);
                } else if (mgPlayer.isInvincible()) {
                    event.setCancelled(true);
                } else if (event.getCause() == DamageCause.FALL &&
                        mgPlayer.getLoadout() != null && !mgPlayer.getLoadout().hasFallDamage()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void spectatorAttack(@NotNull EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            MinigamePlayer mgPlayer = pdata.getMinigamePlayer(player);
            if (mgPlayer.isInMinigame() && mgPlayer.getMinigame().isSpectator(mgPlayer)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void clickMenu(@NotNull InventoryClickEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer((Player) event.getWhoClicked());
        if (mgPlayer.isInMenu()) {
            if (event.getRawSlot() < mgPlayer.getMenu().getSize()) {
                if (!mgPlayer.getMenu().getAllowModify() || mgPlayer.getMenu().hasMenuItem(event.getRawSlot())) {
                    event.setCancelled(true);
                }

                MenuItem item = mgPlayer.getMenu().getClicked(event.getRawSlot());
                if (item != null) {
                    ItemStack disItem = null;
                    switch (event.getClick()) {
                        case LEFT -> {
                            if (event.getCursor().getType() != Material.AIR)
                                disItem = item.onClickWithItem(event.getCursor());
                            else
                                disItem = item.onClick();
                        }
                        case RIGHT -> disItem = item.onRightClick();
                        case SHIFT_LEFT -> disItem = item.onShiftClick();
                        case SHIFT_RIGHT -> disItem = item.onShiftRightClick();
                        case DOUBLE_CLICK -> disItem = item.onDoubleClick();
                    }

                    event.setCurrentItem(disItem);
                }
                /*
                 * Cancel special cases, where event.getRawSlot() is not in the Menu inventory,
                 *  but the event modifies it anyway
                 */
            } else if (!mgPlayer.getMenu().getAllowModify()) {
                Inventory topInv = event.getView().getTopInventory();
                switch (event.getAction()) {
                    case NOTHING, DROP_ALL_CURSOR, DROP_ONE_CURSOR, UNKNOWN -> {
                    } // nothing
                    case PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE, DROP_ALL_SLOT, DROP_ONE_SLOT, HOTBAR_MOVE_AND_READD, // may take
                            PLACE_ALL, PLACE_SOME, PLACE_ONE, /*may place*/
                            SWAP_WITH_CURSOR, HOTBAR_SWAP /*may give and take*/ -> {
                        if (event.getClickedInventory() == topInv) {
                            event.setCancelled(true);
                        }
                    }
                    case COLLECT_TO_CURSOR -> { // may take complex
                        if (topInv.contains(event.getCursor().getType())) {
                            event.setCancelled(true);
                        }
                    }
                    case MOVE_TO_OTHER_INVENTORY -> {
                        event.setCancelled(true);
                    } // definitely one or the other
                }
            }

        } else if (mgPlayer.isInMinigame()) {
            if (!mgPlayer.getLoadout().allowOffHand() && event.getSlot() == 40) {
                event.setCancelled(true);
            } else if ((mgPlayer.getLoadout().isArmourLocked() && event.getSlot() >= 36 && event.getSlot() <= 39) ||
                    (mgPlayer.getLoadout().isInventoryLocked() && event.getSlot() >= 0 && event.getSlot() <= 35)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onOffhandSwap(@NotNull PlayerSwapHandItemsEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMenu()) {
            event.setCancelled(true);
        } else if (mgPlayer.isInMinigame()) {
            if (!mgPlayer.getLoadout().allowOffHand()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void dragMenu(@NotNull InventoryDragEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer((Player) event.getWhoClicked());
        if (mgPlayer.isInMenu()) {
            if (!mgPlayer.getMenu().getAllowModify()) {
                for (int slot : event.getRawSlots()) {
                    if (slot < mgPlayer.getMenu().getSize()) {
                        event.setCancelled(true);
                        break;
                    }
                }
            } else {
                Set<Integer> slots = new HashSet<>(event.getRawSlots());

                for (int slot : slots) {
                    if (mgPlayer.getMenu().hasMenuItem(slot)) {
                        event.getRawSlots().remove(slot);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void closeMenu(@NotNull InventoryCloseEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer((Player) event.getPlayer());

        if (mgPlayer.isInMenu() && !mgPlayer.getNoClose()) {
            mgPlayer.setMenu(null);
        }
    }

    @EventHandler
    private void manualItemEntry(@NotNull AsyncPlayerChatEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMenu() && mgPlayer.getNoClose() && mgPlayer.getManualEntry() != null) {
            event.setCancelled(true);
            mgPlayer.setNoClose(false);
            mgPlayer.getManualEntry().checkValidEntry(event.getMessage());
            mgPlayer.setManualEntry(null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerHungry(@NotNull FoodLevelChangeEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer((Player) event.getEntity());

        if (mgPlayer.isInMinigame() && mgPlayer.getLoadout() != null &&
                !mgPlayer.getLoadout().hasHunger()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerMove(@NotNull PlayerMoveEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame()) {
            if (mgPlayer.isFrozen()) {
                if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                        event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                    mgPlayer.teleport(new Location(event.getFrom().getWorld(), event.getFrom().getBlockX() + 0.5,
                            event.getTo().getBlockY(), event.getFrom().getBlockZ() + 0.5,
                            event.getPlayer().getLocation().getYaw(), event.getPlayer().getLocation().getPitch()));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void breakScoreboard(@NotNull BlockBreakEvent event) {
        Block block = event.getBlock();
        if (Tag.WALL_SIGNS.isTagged(block.getType())) {
            if (block.hasMetadata("MGScoreboardSign")) {
                Minigame minigame = (Minigame) block.getMetadata("Minigame").get(0).value();
                minigame.getScoreboardData().removeDisplay(block);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void potionAffectsPlayer(@NotNull PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player)) return;
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer((Player) event.getPotion().getShooter());
        if (!mgPlayer.isInMinigame()) return;
        if (mgPlayer.getMinigame().friendlyFireSplashPotions()) return;
        List<Player> list = event.getAffectedEntities().stream()
                .filter(p -> p instanceof Player)
                .filter(p -> pdata.getMinigamePlayer((Player) p).isInMinigame())
                .filter(p -> pdata.getMinigamePlayer((Player) p).getMinigame() == mgPlayer.getMinigame())
                .map(p -> (Player) p)
                .toList();
        if (list.isEmpty()) return;
        List<PotionEffectType> effects = event.getPotion().getEffects().stream().map(PotionEffect::getType).toList();
        list.stream().filter(Predicate.not(p -> isEffectApplicable(effects, mgPlayer, pdata.getMinigamePlayer(p)))).forEach(p -> event.setIntensity(p, 0.0));
    }

    @EventHandler(ignoreCancelled = true)
    private void effectAreaAffectsPlayer(@NotNull AreaEffectCloudApplyEvent event) {
        if (!(event.getEntity().getSource() instanceof Player player)) return;
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(player);

        if (!mgPlayer.isInMinigame()) return;
        if (mgPlayer.getMinigame().friendlyFireLingeringPotions()) return;
        List<Player> list = event.getAffectedEntities().stream()
                .filter(p -> p instanceof Player)
                .filter(p -> pdata.getMinigamePlayer((Player) p).isInMinigame())
                .filter(p -> pdata.getMinigamePlayer((Player) p).getMinigame() == mgPlayer.getMinigame())
                .map(p -> (Player) p)
                .toList();
        if (list.isEmpty()) return;
        List<PotionEffectType> effects = List.of(event.getEntity().getBasePotionData().getType().getEffectType());
        event.getAffectedEntities().removeAll(list.stream().filter(Predicate.not(p -> isEffectApplicable(effects, mgPlayer, pdata.getMinigamePlayer(p)))).toList());
    }

    private boolean isEffectApplicable(@NotNull Collection<@NotNull PotionEffectType> effectTypes, @NotNull MinigamePlayer mgPlayerEffecting, @NotNull MinigamePlayer mgPlayerReceiving) {
        if (!mgPlayerEffecting.getMinigame().isTeamGame()) {
            if (mgPlayerEffecting == mgPlayerReceiving) {
                return !MinigameTag.NEGATIVE_POTION.allTagged(effectTypes);
            }
            return !MinigameTag.POSITIVE_POTION.allTagged(effectTypes);
        }
        if (mgPlayerEffecting.getTeam() == mgPlayerReceiving.getTeam()) {
            return !MinigameTag.NEGATIVE_POTION.allTagged(effectTypes);
        }
        return !MinigameTag.POSITIVE_POTION.allTagged(effectTypes);
    }
}
