package au.com.mineauz.minigames;

import au.com.mineauz.minigames.events.RevertCheckpointEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MessageManager;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

public class Events implements Listener {
    private static final Minigames plugin = Minigames.getPlugin();
    private final MinigamePlayerManager pdata = plugin.getPlayerManager();
    private final MinigameManager mdata = plugin.getMinigameManager();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerResourcePack(PlayerResourcePackStatusEvent event) {
        final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        List<MinigamePlayer> required = plugin.getPlayerManager().getApplyingPack();
        if (ply.isInMinigame()) {
            if (required.contains(ply)) {
                ResourcePackModule module = ResourcePackModule.getMinigameModule(ply.getMinigame());
                if (!module.isEnabled()) return;
                if (!module.isForced()) return;
                switch (event.getStatus()) {
                    case ACCEPTED, SUCCESSFULLY_LOADED -> required.remove(ply);
                    case DECLINED -> {
                        Minigames.getPlugin().getPlayerManager().quitMinigame(ply, true);
                        MessageManager.sendMessage(ply, MinigameMessageType.ERROR, null, "minigames.resource.declined");
                        required.remove(ply);
                    }
                    case FAILED_DOWNLOAD -> {
                        Minigames.getPlugin().getPlayerManager().quitMinigame(ply, true);
                        MessageManager.sendMessage(ply, MinigameMessageType.ERROR, null, "minigames.resource.failed");
                        required.remove(ply);
                    }
                }
            }

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity().getPlayer());
        if (ply.isInMinigame()) {
            Minigame mgm = ply.getMinigame();
            if (!mgm.hasDeathDrops()) {
                if (mgm.keepInventory()) {
                    List<ItemStack> drops = Arrays.asList(ply.getPlayer().getInventory().getContents());
                    PlayerLoadout l = new PlayerLoadout("deathDrops");
                    for (int i = 0; i < drops.size(); i++) {
                        l.addItem(drops.get(i), i);
                    }
                    ply.setLoadout(l);
                }
                event.getDrops().clear();
            }
            String msg = event.getDeathMessage();
            event.setDeathMessage(null);
            event.setDroppedExp(0);

            ply.addDeath();
            ply.addRevert();

            pdata.partyMode(ply);

            if (ply.getPlayer().getKiller() != null) {
                MinigamePlayer killer = pdata.getMinigamePlayer(ply.getPlayer().getKiller());
                if (killer != null)
                    killer.addKill();
            }

            if (msg != null && !msg.equals("")) {
                mdata.sendMinigameMessage(mgm, msg, MinigameMessageType.ERROR);
            }
            if (mgm.getState() == MinigameState.STARTED) {
                if (mgm.getLives() > 0 && mgm.getLives() <= ply.getDeaths()) {
                    ply.sendMessage(MinigameUtils.getLang("player.quit.plyOutOfLives"), MinigameMessageType.ERROR);
                    if (!event.getDrops().isEmpty() && mgm.getPlayers().size() == 1) {
                        event.getDrops().clear();
                    }
                    pdata.quitMinigame(ply, false);
                } else if (mgm.getLives() > 0) {
                    ply.sendInfoMessage(MessageManager.getMinigamesMessage("minigame.livesLeft", mgm.getLives() - ply.getDeaths()));
                }
            } else if (mgm.getState() == MinigameState.ENDED) {
                plugin.getPlayerManager().quitMinigame(ply, true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerSpawn(PlayerRespawnEvent event) {
        final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply.isInMinigame()) {
            final WeatherTimeModule mod = WeatherTimeModule.getMinigameModule(ply.getMinigame());
            if (mod != null && mod.isUsingCustomWeather()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> ply.getPlayer().setPlayerWeather(mod.getCustomWeather()));
            }

            if (ply.getMinigame().getState() == MinigameState.ENDED) {
                plugin.getPlayerManager().quitMinigame(ply, true);
            }
        }
        if (ply.isRequiredQuit()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ply::restorePlayerData);
            event.setRespawnLocation(ply.getQuitPos());

            ply.setRequiredQuit(false);
            ply.setQuitPos(null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void playerDropItem(PlayerDropItemEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply.isInMinigame()) {
            Minigame mgm = pdata.getMinigamePlayer(event.getPlayer()).getMinigame();
            if (!mgm.hasItemDrops() ||
                    mgm.isSpectator(pdata.getMinigamePlayer(event.getPlayer()))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void itemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            MinigamePlayer ply = pdata.getMinigamePlayer(player);
            if (ply.isInMinigame()) {
                Minigame mgm = ply.getMinigame();
                if (!mgm.hasItemPickup() ||
                        mgm.isSpectator(ply)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply.isInMinigame()) {
            if (ply.getPlayer().isDead()) {
                ply.getOfflineMinigamePlayer().setLoginLocation(ply.getMinigame().getQuitPosition());
                ply.getOfflineMinigamePlayer().savePlayerData();
            }
            pdata.quitMinigame(pdata.getMinigamePlayer(event.getPlayer()), false);
        } else if (ply.isRequiredQuit()) {
            ply.getOfflineMinigamePlayer().setLoginLocation(ply.getQuitPos());
            ply.getOfflineMinigamePlayer().savePlayerData();
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
        ply.saveClaimedRewards();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConnect(PlayerJoinEvent event) {
        pdata.addMinigamePlayer(event.getPlayer());
        File pldata = new File(plugin.getDataFolder() + "/playerdata/inventories/" + event.getPlayer().getUniqueId() + ".yml");
        final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (pldata.exists()) {
            ply.setOfflineMinigamePlayer(new OfflineMinigamePlayer(event.getPlayer().getUniqueId()));
            Location floc = ply.getOfflineMinigamePlayer().getLoginLocation();
            ply.setRequiredQuit(true);
            ply.setQuitPos(floc);

            if (!ply.getPlayer().isDead() && ply.isRequiredQuit()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ply::restorePlayerData);
                ply.teleport(ply.getQuitPos());

                ply.setRequiredQuit(false);
                ply.setQuitPos(null);
            }

            plugin.getLogger().info(ply.getName() + "'s data has been restored from file.");
        }

        ply.loadClaimedRewards();

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
    public void playerInterract(PlayerInteractEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());

        if (ply.isInMinigame() && !ply.canInteract()) {
            event.setCancelled(true);
            return;
        }
        if (ply.isInMenu() && ply.getNoClose() && ply.getManualEntry() != null) {
            event.setCancelled(true);
            ply.setNoClose(false);
            if (event.getClickedBlock() != null) {
                ply.getManualEntry().checkValidEntry(event.getClickedBlock().getBlockData().getAsString());
            }
            ply.setManualEntry(null);
            return;
        }
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.DRAGON_EGG) {
            if (!ply.getMinigame().allowDragonEggTeleport()) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && !(event.useInteractedBlock() == Event.Result.DENY)) {
            Block cblock = event.getClickedBlock();
            if (cblock.getState() instanceof Sign sign && sign.getSide(Side.FRONT).getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE + "[Minigame]")) {
                // wax signs automatically
                sign.setWaxed(true);
                if (event.getPlayer().hasPermission("minigame.sign.use.details")) {
                    if ((sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Join") || sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Bet")) && !ply.isInMinigame()) {
                        Minigame mgm = mdata.getMinigame(sign.getLine(2));
                        if (mgm != null && (!mgm.getUsePermissions() || event.getPlayer().hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))) {
                            if (!mgm.isEnabled()) {
                                event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.notEnabled"));
                            } else {
                                event.getPlayer().sendMessage(ChatColor.GREEN + MinigameUtils.getLang("minigame.info.description"));
                                String status = ChatColor.AQUA + MinigameUtils.getLang("minigame.info.status.title");
                                if (!mgm.hasPlayers()) {
                                    status += " " + ChatColor.GREEN + MinigameUtils.getLang("minigame.info.status.empty");
                                } else if (mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() > 0) {
                                    status += " " + ChatColor.GREEN + MinigameUtils.getLang("minigame.info.status.waitingForPlayers");
                                } else {
                                    status += " " + ChatColor.RED + MinigameUtils.getLang("minigame.info.status.started");
                                }

                                if (mgm.getType() != MinigameType.SINGLEPLAYER) {
                                    event.getPlayer().sendMessage(status);
                                    if (mgm.canLateJoin())
                                        event.getPlayer().sendMessage(ChatColor.AQUA + MinigameUtils.getLang("minigame.info.lateJoin.msg") + " " + ChatColor.GREEN + MinigameUtils.getLang("minigame.info.lateJoin.enabled"));
                                    else
                                        event.getPlayer().sendMessage(ChatColor.AQUA + MinigameUtils.getLang("minigame.info.lateJoin.msg") + " " + ChatColor.RED + MinigameUtils.getLang("minigame.info.lateJoin.disabled"));
                                }

                                if (mgm.getMinigameTimer() != null) {
                                    event.getPlayer().sendMessage(ChatColor.AQUA + "Time left: " + MinigameUtils.convertTime(mgm.getMinigameTimer().getTimeLeft()));
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
                                    event.getPlayer().sendMessage(ChatColor.AQUA + MinigameUtils.getLang("minigame.info.score") + sc);
                                }

                                String playerCount = ChatColor.AQUA + MinigameUtils.getLang("minigame.info.playerCount") + " " + ChatColor.GRAY;
                                String players = ChatColor.AQUA + MinigameUtils.getLang("minigame.info.players.msg") + " ";

                                if (mgm.hasPlayers()) {
                                    playerCount += mgm.getPlayers().size();
                                    if (mgm.getType() != MinigameType.SINGLEPLAYER) {
                                        playerCount += "/" + mgm.getMaxPlayers();
                                    }

                                    List<String> plyList = new ArrayList<>();
                                    for (MinigamePlayer pl : mgm.getPlayers()) {
                                        plyList.add(pl.getName());
                                    }
                                    players += MinigameUtils.listToString(plyList);
                                } else {
                                    playerCount += "0";

                                    if (mgm.getType() != MinigameType.SINGLEPLAYER) {
                                        playerCount += "/" + mgm.getMaxPlayers();
                                    }

                                    players += ChatColor.GRAY + MinigameUtils.getLang("minigame.info.players.none");
                                }

                                event.getPlayer().sendMessage(playerCount);
                                event.getPlayer().sendMessage(players);
                            }
                        } else if (mgm == null) {
                            event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noMinigame"));
                        } else if (mgm.getUsePermissions()) {
                            event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MessageManager.getMinigamesMessage("minigame.error.noPermission", "minigame.join." + mgm.getName(false).toLowerCase()));
                        }
                    }
                }
            }
        }

        ItemStack item = event.getItem();
        //nullcheck in isMinigameTool()
        if (MinigameUtils.isMinigameTool(item) && ply.getPlayer().hasPermission("minigame.tool")) {
            MinigameTool tool = new MinigameTool(item);
            event.setCancelled(true);

            if (event.getPlayer().isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                tool.openMenu(ply);
                event.setCancelled(true);
            } else if (event.getClickedBlock() != null && (Tag.ALL_SIGNS.isTagged(event.getClickedBlock().getType()))) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if (ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[Minigame]") && ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase("Join")) {
                    Minigame minigame = mdata.getMinigame(sign.getLine(2));
                    tool.setMinigame(minigame);
                    ply.sendInfoMessage("Tools Minigame has been set to " + minigame);
                    event.setCancelled(true);
                }
            } else {
                checkTool(tool, ply);
                if (tool.getMode() != null && tool.getMinigame() != null) {
                    Minigame mg = tool.getMinigame();
                    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        tool.getMode().onRightClick(ply, mg, TeamsModule.getMinigameModule(mg).getTeam(tool.getTeam()), event);
                    } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        tool.getMode().onLeftClick(ply, mg, TeamsModule.getMinigameModule(mg).getTeam(tool.getTeam()), event);
                    }
                }
            }
        }

        //Spectator disables:
        if (ply.isInMinigame() && pdata.getMinigamePlayer(event.getPlayer()).getMinigame().isSpectator(pdata.getMinigamePlayer(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleportAway(PlayerTeleportEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());

        if (ply.isInMinigame() && (event.getCause() == TeleportCause.COMMAND || event.getCause() == TeleportCause.PLUGIN || (!ply.getMinigame().isAllowedEnderpearls() && event.getCause() == TeleportCause.ENDER_PEARL))) {
            if (!ply.getAllowTeleport()) {
                Location from = event.getFrom();
                Location to = event.getTo();
                if (from.getWorld() != to.getWorld() || from.distance(to) > 2) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noTeleport"));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onGMChange(PlayerGameModeChangeEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply.isInMinigame() && !ply.getAllowGamemodeChange()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noGamemode"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFlyToggle(PlayerToggleFlightEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply.isInMinigame() && (!ply.getMinigame().isSpectator(ply) || !ply.getMinigame().canSpectateFly()) &&
                !ply.canFly()) {
            event.setCancelled(true);
            pdata.quitMinigame(ply, true);
            event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noFly"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void playerRevert(RevertCheckpointEvent event) {
        if (event.getMinigamePlayer().isInMinigame() &&
                event.getMinigamePlayer().getMinigame().getType() == MinigameType.MULTIPLAYER &&
                !event.getMinigamePlayer().getMinigame().isAllowedMPCheckpoints() &&
                !event.getMinigamePlayer().isLatejoining()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MessageManager.getMinigamesMessage("minigame.error.noRevert", event.getMinigamePlayer().getMinigame().getType().getName()));
        } else if (!event.getMinigamePlayer().getMinigame().hasStarted()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void commandExecute(PlayerCommandPreprocessEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply.isInMinigame()) {
            for (String comd : pdata.getDeniedCommands()) {
                if (event.getMessage().contains(comd)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noCommand"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void entityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Snowball sb) {
                MinigamePlayer ply = pdata.getMinigamePlayer((Player) event.getEntity());
                if (ply.isInMinigame() && ply.getMinigame().hasPaintBallMode()) {
                    if (sb.getShooter() instanceof Player player) {
                        MinigamePlayer shooter = pdata.getMinigamePlayer(player);
                        Minigame mgm = ply.getMinigame();

                        if (shooter == null)
                            return;

                        if (shooter.isInMinigame() && shooter.getMinigame().equals(ply.getMinigame())) {
                            if (!shooter.canPvP()) {
                                event.setCancelled(true);
                                return;
                            }

                            Team plyTeam = ply.getTeam();
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
            MinigamePlayer ply = pdata.getMinigamePlayer(player);
            ItemStack item = player.getEquipment().getItemInMainHand();
            if (MinigameUtils.isMinigameTool(item) && player.hasPermission("minigame.tool")) {
                if (ply.isInMinigame()) {
                    ply.sendInfoMessage("Quit the game to use the tool at this time");
                    return;
                }
                MinigameTool tool = new MinigameTool(item);
                if (player.isSneaking()) {
                    tool.openMenu(ply);
                    event.setCancelled(true);
                } else {
                    checkTool(tool, ply);
                    if (tool.getMinigame() != null && tool.getMode() != null) {
                        Minigame mg = tool.getMinigame();
                        tool.getMode().onEntityLeftClick(ply, mg, TeamsModule.getMinigameModule(mg).getTeam(tool.getTeam()), event);
                    }
                }

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerRightClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        MinigamePlayer ply = pdata.getMinigamePlayer(player);
        ItemStack item = player.getEquipment().getItemInMainHand();
        if (MinigameUtils.isMinigameTool(item) && player.hasPermission("minigame.tool")) {
            if (ply.isInMinigame()) {
                ply.sendInfoMessage("Quit the game to use the tool at this time");
                return;
            }
            MinigameTool tool = new MinigameTool(item);
            if (player.isSneaking()) {
                tool.openMenu(ply);
                event.setCancelled(true);
            } else {
                checkTool(tool, ply);
                if (tool.getMinigame() != null && tool.getMode() != null) {
                    Minigame mg = tool.getMinigame();
                    tool.getMode().onEntityRightClick(ply, mg, TeamsModule.getMinigameModule(mg).getTeam(tool.getTeam()), event);
                }
            }
        }
    }

    private void checkTool(MinigameTool tool, MinigamePlayer ply) {
        if (tool.getMinigame() == null) {
            ply.sendInfoMessage("Please select a minigame. Click on the join sign, or /mg tool minigame <minigame>");
        }
        if (tool.getMode() == null) {
            ply.sendInfoMessage("Please select a tool mode. Shift + Right click");
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerShoot(ProjectileLaunchEvent event) {
        if (event.getEntityType() == EntityType.SNOWBALL) {
            Snowball snowball = (Snowball) event.getEntity();

            if (snowball.getShooter() instanceof Player player) {
                MinigamePlayer ply = pdata.getMinigamePlayer(player);

                if (ply.isInMinigame() && ply.getMinigame().hasUnlimitedAmmo()) {
                    //wait for the inventory to update
                    Bukkit.getScheduler().runTaskLater(Minigames.getPlugin(), () -> {
                        ItemStack itemInMainHand = ply.getPlayer().getInventory().getItemInMainHand();

                        if (itemInMainHand.getType() == Material.SNOWBALL) {
                            itemInMainHand.setAmount(16);
                            ply.getPlayer().updateInventory();
                        } else {
                            ply.getPlayer().getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));
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
    private void playerHurt(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            MinigamePlayer ply = pdata.getMinigamePlayer(player);

            if (ply.isInMinigame()) {
                Minigame mgm = ply.getMinigame();
                if (mgm.isSpectator(ply)) {
                    event.setCancelled(true);
                } else if ((!ply.getMinigame().hasStarted() && ply.getMinigame().getState() != MinigameState.ENDED) || ply.isLatejoining()) {
                    event.setCancelled(true);
                } else if (ply.isInvincible()) {
                    event.setCancelled(true);
                } else if (event.getCause() == DamageCause.FALL &&
                        ply.getLoadout() != null && !ply.getLoadout().hasFallDamage()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void spectatorAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            MinigamePlayer ply = pdata.getMinigamePlayer(player);
            if (ply.isInMinigame() && ply.getMinigame().isSpectator(ply)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void clickMenu(InventoryClickEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer((Player) event.getWhoClicked());
        if (ply.isInMenu()) {
            if (event.getRawSlot() < ply.getMenu().getSize()) {
                if (!ply.getMenu().getAllowModify() || ply.getMenu().hasMenuItem(event.getRawSlot()))
                    event.setCancelled(true);

                MenuItem item = ply.getMenu().getClicked(event.getRawSlot());
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
            }
        } else if (ply.isInMinigame()) {
            if (!ply.getLoadout().allowOffHand() && event.getSlot() == 40) {
                event.setCancelled(true);
            } else if ((ply.getLoadout().isArmourLocked() && event.getSlot() >= 36 && event.getSlot() <= 39) ||
                    (ply.getLoadout().isInventoryLocked() && event.getSlot() >= 0 && event.getSlot() <= 35)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onOffhandSwap(PlayerSwapHandItemsEvent event) {
        MinigamePlayer player = pdata.getMinigamePlayer(event.getPlayer());
        if (player.isInMenu()) {
            event.setCancelled(true);
        } else if (player.isInMinigame()) {
            if (!player.getLoadout().allowOffHand()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void dragMenu(InventoryDragEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer((Player) event.getWhoClicked());
        if (ply.isInMenu()) {
            if (!ply.getMenu().getAllowModify()) {
                for (int slot : event.getRawSlots()) {
                    if (slot < ply.getMenu().getSize()) {
                        event.setCancelled(true);
                        break;
                    }
                }
            } else {
                Set<Integer> slots = new HashSet<>(event.getRawSlots());

                for (int slot : slots) {
                    if (ply.getMenu().hasMenuItem(slot)) {
                        event.getRawSlots().remove(slot);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void closeMenu(InventoryCloseEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer((Player) event.getPlayer());

        if (ply.isInMenu() && !ply.getNoClose()) {
            ply.setMenu(null);
        }
    }

    @EventHandler
    private void manualItemEntry(AsyncPlayerChatEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply.isInMenu() && ply.getNoClose() && ply.getManualEntry() != null) {
            event.setCancelled(true);
            ply.setNoClose(false);
            ply.getManualEntry().checkValidEntry(event.getMessage());
            ply.setManualEntry(null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerHungry(FoodLevelChangeEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer((Player) event.getEntity());

        if (ply.isInMinigame() && ply.getLoadout() != null &&
                !ply.getLoadout().hasHunger()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerMove(PlayerMoveEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());

        if (ply.isInMinigame()) {
            if (ply.isFrozen()) {
                if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                        event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                    ply.teleport(new Location(event.getFrom().getWorld(), event.getFrom().getBlockX() + 0.5,
                            event.getTo().getBlockY(), event.getFrom().getBlockZ() + 0.5,
                            event.getPlayer().getLocation().getYaw(), event.getPlayer().getLocation().getPitch()));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void breakScoreboard(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (Tag.WALL_SIGNS.isTagged(block.getType())) {
            if (block.hasMetadata("MGScoreboardSign")) {
                Minigame minigame = (Minigame) block.getMetadata("Minigame").get(0).value();
                minigame.getScoreboardData().removeDisplay(block);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void potionAffectsPlayer(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player)) return;
        MinigamePlayer ply = pdata.getMinigamePlayer((Player) event.getPotion().getShooter());
        if (!ply.isInMinigame()) return;
        if (ply.getMinigame().friendlyFireSplashPotions()) return;
        List<Player> list = event.getAffectedEntities().stream()
                .filter(p -> p instanceof Player)
                .filter(p -> pdata.getMinigamePlayer((Player) p).isInMinigame())
                .filter(p -> pdata.getMinigamePlayer((Player) p).getMinigame() == ply.getMinigame())
                .map(p -> (Player) p)
                .toList();
        if (list.isEmpty()) return;
        List<PotionEffectType> effects = event.getPotion().getEffects().stream().map(PotionEffect::getType).toList();
        list.stream().filter(Predicate.not(p -> isEffectApplicable(effects, ply, pdata.getMinigamePlayer(p)))).forEach(p -> event.setIntensity(p, 0.0));
    }

    @EventHandler(ignoreCancelled = true)
    private void effectAreaAffectsPlayer(AreaEffectCloudApplyEvent event) {
        if (!(event.getEntity().getSource() instanceof Player player)) return;
        MinigamePlayer ply = pdata.getMinigamePlayer(player);

        if (!ply.isInMinigame()) return;
        if (ply.getMinigame().friendlyFireLingeringPotions()) return;
        List<Player> list = event.getAffectedEntities().stream()
                .filter(p -> p instanceof Player)
                .filter(p -> pdata.getMinigamePlayer((Player) p).isInMinigame())
                .filter(p -> pdata.getMinigamePlayer((Player) p).getMinigame() == ply.getMinigame())
                .map(p -> (Player) p)
                .toList();
        if (list.isEmpty()) return;
        List<PotionEffectType> effects = List.of(event.getEntity().getBasePotionData().getType().getEffectType());
        event.getAffectedEntities().removeAll(list.stream().filter(Predicate.not(p -> isEffectApplicable(effects, ply, pdata.getMinigamePlayer(p)))).toList());
    }

    private boolean isEffectApplicable(Collection<PotionEffectType> effectTypes, MinigamePlayer ply, MinigamePlayer rPly) {
        if (!ply.getMinigame().isTeamGame()) {
            if (ply == rPly) {
                return !MinigameTag.NEGATIVE_POTION.isTagged(effectTypes);
            }
            return !MinigameTag.POSITIVE_POTION.isTagged(effectTypes);
        }
        if (ply.getTeam() == rPly.getTeam()) {
            return !MinigameTag.NEGATIVE_POTION.isTagged(effectTypes);
        }
        return !MinigameTag.POSITIVE_POTION.isTagged(effectTypes);
    }
}
