package au.com.mineauz.minigames.blockRecorder;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;

public class BasicRecorder implements Listener {

    private final MinigamePlayerManager pdata = Minigames.getPlugin().getPlayerManager();

    @EventHandler(priority = EventPriority.HIGH)
    private void blockBreak(BlockBreakEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply == null) return;
        if (ply.isInMinigame()) {
            if (!ply.getMinigame().hasStarted() || ply.isLatejoining()) {
                event.setCancelled(true);
                return;
            }
            Minigame mgm = ply.getMinigame();
            RecorderData d = mgm.getBlockRecorder();
            if (((d.getWhitelistMode() && d.getWBBlocks().contains(event.getBlock().getType())) ||
                    (!d.getWhitelistMode() && !d.getWBBlocks().contains(event.getBlock().getType()))) &&
                    mgm.canBlockBreak()) {
                if (event.getBlock().getState() instanceof Sign) {
                    Sign sign = (Sign) event.getBlock().getState();
                    if (sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE + "[Minigame]")) {
                        event.setCancelled(true);
                    } else {
                        d.addBlock(event.getBlock(), ply);
                        if (!mgm.canBlocksdrop()) {
                            event.setCancelled(true);
                            event.getBlock().setType(Material.AIR);
                        }
                    }
                } else {
                    Location above = event.getBlock().getLocation().clone();
                    above.setY(above.getY() + 1);
                    d.addBlock(event.getBlock(), ply);

                    if (!mgm.canBlocksdrop()) {
                        event.setCancelled(true);
                        event.getBlock().setType(Material.AIR);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void blockPlace(BlockPlaceEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply == null) return;
        if (ply.isInMinigame() && !event.isCancelled()) {
            if (!ply.getMinigame().hasStarted() || ply.isLatejoining()) {
                event.setCancelled(true);
                return;
            }
            Minigame mgm = ply.getMinigame();
            RecorderData d = mgm.getBlockRecorder();
            if (((d.getWhitelistMode() && d.getWBBlocks().contains(event.getBlock().getType())) ||
                    (!d.getWhitelistMode() && !d.getWBBlocks().contains(event.getBlock().getType()))) &&
                    mgm.canBlockPlace()) {
                d.addBlock(event.getBlockReplacedState(), ply);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void takeItem(PlayerInteractEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply == null) return;
        if (ply.isInMinigame() && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && !ply.getMinigame().isSpectator(ply)) {
            Minigame mgm = ply.getMinigame();
            RecorderData d = mgm.getBlockRecorder();
            if (event.getClickedBlock().getState() instanceof InventoryHolder) {
                d.addBlock(event.getClickedBlock().getLocation().getBlock(), ply);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void bucketFill(PlayerBucketFillEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply == null) return;
        if (ply.isInMinigame()) {
            Minigame mgm = ply.getMinigame();
            RecorderData d = mgm.getBlockRecorder();
            if (((d.getWhitelistMode() && d.getWBBlocks().contains(event.getBlockClicked().getType())) ||
                    (!d.getWhitelistMode() && !d.getWBBlocks().contains(event.getBlockClicked().getType()))) &&
                    mgm.canBlockBreak()) {
                d.addBlock(event.getBlockClicked(), pdata.getMinigamePlayer(event.getPlayer()));
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void igniteblock(BlockIgniteEvent event) {
        MinigamePlayer ply = null;
        if (event.getPlayer() != null)
            ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply == null) return;
        if (ply.isInMinigame() &&
                (event.getCause() == IgniteCause.FIREBALL || event.getCause() == IgniteCause.FLINT_AND_STEEL)) {
            Minigame mgm = ply.getMinigame();
            RecorderData d = mgm.getBlockRecorder();
            if (((d.getWhitelistMode() && d.getWBBlocks().contains(Material.FIRE)) ||
                    (!d.getWhitelistMode() && !d.getWBBlocks().contains(Material.FIRE))) &&
                    mgm.canBlockPlace()) {
                d.addBlock(event.getBlock(), pdata.getMinigamePlayer(event.getPlayer()));
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void paintingPlace(HangingPlaceEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply.isInMinigame()) {
            Minigame mgm = ply.getMinigame();
            RecorderData d = mgm.getBlockRecorder();
            if (((d.getWhitelistMode() && d.getWBBlocks().contains(Material.PAINTING)) ||
                    (!d.getWhitelistMode() && !d.getWBBlocks().contains(Material.PAINTING))) ||
                    ((d.getWhitelistMode() && d.getWBBlocks().contains(Material.ITEM_FRAME)) ||
                            (!d.getWhitelistMode() && !d.getWBBlocks().contains(Material.ITEM_FRAME)))) {
                d.addEntity(event.getEntity(), ply, true);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void animalHurt(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Animals) {
            Animals animal = (Animals) event.getEntity();
            if (animal.getHealth() <= event.getDamage()) {
                MinigamePlayer ply = null;
                if (event.getDamager() instanceof Player) {
                    ply = pdata.getMinigamePlayer((Player) event.getDamager());
                } else if (event.getDamager() instanceof Arrow) {
                    Arrow arr = (Arrow) event.getDamager();
                    if (arr.getShooter() instanceof Player) {
                        ply = pdata.getMinigamePlayer((Player) arr.getShooter());
                    }
                }
                if (ply != null) {
                    if (ply.isInMinigame()) {
                        ply.getMinigame().getBlockRecorder().addEntity(animal, ply, false);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void paintingBreak(HangingBreakByEntityEvent event) {
        Player ply = null;
        if (event.getRemover() instanceof Player) {
            ply = (Player) event.getRemover();
        } else if (event.getRemover() instanceof Arrow) {
            if (((Arrow) event.getRemover()).getShooter() instanceof Player) {
                ply = (Player) ((Arrow) event.getRemover()).getShooter();
            }
        }
        if (ply != null) {
            if (pdata.getMinigamePlayer(ply).isInMinigame()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void arrowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player ply = (Player) event.getEntity();
            if (pdata.getMinigamePlayer(ply).isInMinigame()) {
                pdata.getMinigamePlayer(ply).getMinigame().getBlockRecorder().addEntity(event.getProjectile(), pdata.getMinigamePlayer(ply), true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void throwEnderpearl(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player ply = (Player) event.getEntity().getShooter();
            if (pdata.getMinigamePlayer(ply).isInMinigame()) {
                pdata.getMinigamePlayer(ply).getMinigame().getBlockRecorder().addEntity(event.getEntity(), pdata.getMinigamePlayer(ply), true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void bucketEmpty(PlayerBucketEmptyEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply == null) return;
        if (ply.isInMinigame()) {
            Minigame mgm = ply.getMinigame();
            RecorderData d = mgm.getBlockRecorder();
            if (((d.getWhitelistMode() && d.getWBBlocks().contains(event.getBlockClicked().getType())) ||
                    (!d.getWhitelistMode() && !d.getWBBlocks().contains(event.getBlockClicked().getType()))) &&
                    mgm.canBlockPlace()) {
                Location loc = new Location(event.getBlockClicked().getWorld(),
                        event.getBlockFace().getModX() + event.getBlockClicked().getX(),
                        event.getBlockFace().getModY() + event.getBlockClicked().getY(),
                        event.getBlockFace().getModZ() + event.getBlockClicked().getZ());
                d.addBlock(loc.getBlock(), pdata.getMinigamePlayer(event.getPlayer()));
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void vehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() != null) {
            if (event.getAttacker() instanceof Player) {
                Player ply = (Player) event.getAttacker();
                Minigame mg = pdata.getMinigamePlayer(ply).getMinigame();
                if (pdata.getMinigamePlayer(ply).isInMinigame()) {
                    if (!mg.getBlockRecorder().hasEntity(event.getVehicle())) {
                        mg.getBlockRecorder().addEntity(event.getVehicle(), pdata.getMinigamePlayer(ply), false);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void entityExplode(EntityExplodeEvent event) {
        for (Minigame mg : Minigames.getPlugin().getMinigameManager().getAllMinigames().values()) {
            if (!mg.hasPlayers() && !mg.hasStarted() && mg.getBlockRecorder().hasRegenArea() && mg.getBlockRecorder().blockInRegenArea(event.getLocation())) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
