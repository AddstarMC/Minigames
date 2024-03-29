package au.com.mineauz.minigames.recorder;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LeashHitch;
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
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * This class records all relevant changes the player does in the world while in a minigame
 * After the minigame ends (or the player leaves it) this changes will be reverted.
 * Shot arrows will be removed, blocks will be placed again, etc.
 */
public class BasicRecorder implements Listener {
    private final MinigamePlayerManager playerManager = Minigames.getPlugin().getPlayerManager();

    /**
     * helper methode for blockBreak event to not write code double
     * this checks for Minigame signs and cancels the event, adds the block to the block recorder and handles block drops
     *
     * @param event    the fired block break event
     * @param mgPlayer the minigame representation of a player who broke the block
     * @return true if the BlockBreakEvent should be canceled - aka if a minigame sign would get broken or if block drops are not allowed
     */
    private boolean handleBlockBreak(BlockBreakEvent event, MinigamePlayer mgPlayer) {
        Block eBlock = event.getBlock();
        Minigame mgm = mgPlayer.getMinigame();
        RecorderData recData = mgm.getRecorderData();

        //don't allow breakage of Minigame signs
        //therefore check if the Block is next to such a sign.
        //please note: this doesn't check if the said sign is even attached to the block, todo if it gets necessary a check has to be added for this.
        for (BlockFace face : BlockFace.values()) {
            if (face.isCartesian() || face == BlockFace.SELF) {
                Block other = eBlock.getRelative(face);

                if (other.getState() instanceof Sign sign &&
                        PlainTextComponentSerializer.plainText().serialize(sign.line(0)).equalsIgnoreCase("[Minigame]")) {
                    return true;
                }
            }
        }

        if (mgm.getActivatePlayerRecorder()) {
            recData.addBlock(event.getBlock(), mgPlayer);
        }

        // signs are safe. Now check for block drops
        if (!mgm.canBlocksdrop()) {
            event.getBlock().setType(Material.AIR);
            return true;
        }

        return false;
    }

    /**
     * record blocks broken by players (and stop breaking of blocks the player is not allowed to)
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void blockBreak(BlockBreakEvent event) {
        MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame()) {
            // don't allow players in minigame to break anything before they are in game
            if (!mgPlayer.getMinigame().hasStarted() || mgPlayer.isLatejoining()) {
                event.setCancelled(true);
                return;
            }

            Minigame mgm = mgPlayer.getMinigame();
            RecorderData recData = mgm.getRecorderData();

            if (mgm.canBlockBreak()) {
                if (recData.getWhitelistMode()) {
                    //white list --> blocks that are allowed to be broken
                    if (recData.getWBBlocks().contains(event.getBlock().getType())) {
                        if (handleBlockBreak(event, mgPlayer)) {
                            event.setCancelled(true);
                        }
                    } else { //whitelist mode and not on whitelist
                        event.setCancelled(true);
                    }
                    //black list --> blocks that are not allowed to be broken
                } else if (!recData.getWBBlocks().contains(event.getBlock().getType())) {
                    if (handleBlockBreak(event, mgPlayer)) {
                        event.setCancelled(true);
                    }
                } else { //blacklist mode and on blacklist
                    event.setCancelled(true);
                }
            } else { //block breaking is turned off
                event.setCancelled(true);
            }
        }
    }

    /**
     * record placed blocks (and stop placing ones the players are not allowed to)
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void blockPlace(BlockPlaceEvent event) {
        MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame()) {
            // don't allow players in minigame to place anything before they are in game
            if (!mgPlayer.getMinigame().hasStarted() || mgPlayer.isLatejoining()) {
                event.setCancelled(true);
                return;
            }

            Minigame mgm = mgPlayer.getMinigame();
            RecorderData recData = mgm.getRecorderData();

            if (mgm.canBlockPlace()) {
                if (recData.getWhitelistMode()) {
                    //white list --> blocks that are allowed to be placed
                    if (recData.getWBBlocks().contains(event.getBlock().getType())) {
                        if (mgm.getActivatePlayerRecorder()) {
                            recData.addBlock(event.getBlockReplacedState(), mgPlayer);
                        }
                    } else {
                        event.setCancelled(true);
                    }
                    //black list --> blocks that are not allowed to be placed
                } else if (recData.getWBBlocks().contains(event.getBlock().getType())) {
                    event.setCancelled(true);
                } else if (mgm.getActivatePlayerRecorder()) {
                    recData.addBlock(event.getBlockReplacedState(), mgPlayer);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * record inventories changed by players
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void changeAnotherInventory(PlayerInteractEvent event) {
        MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame() && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && !mgPlayer.getMinigame().isSpectator(mgPlayer)) {

            Minigame mgm = mgPlayer.getMinigame();
            RecorderData recData = mgm.getRecorderData();

            if (event.getClickedBlock().getState() instanceof InventoryHolder && mgm.getActivatePlayerRecorder()) {
                recData.addBlock(event.getClickedBlock().getLocation().getBlock(), mgPlayer);
            }
        }
    }

    /**
     * record fire lighted by players
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void igniteBlock(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(event.getPlayer());

            if (mgPlayer.isInMinigame() &&
                    (event.getCause() == IgniteCause.FIREBALL || event.getCause() == IgniteCause.FLINT_AND_STEEL)) {
                Minigame mgm = mgPlayer.getMinigame();
                RecorderData recData = mgm.getRecorderData();

                if (mgm.canBlockPlace()) {
                    if (recData.getWhitelistMode()) {
                        //white list --> blocks that are allowed to be broken
                        if (recData.getWBBlocks().contains(event.getBlock().getType())
                                && mgm.getActivatePlayerRecorder()) {
                            recData.addBlock(event.getBlock(), playerManager.getMinigamePlayer(event.getPlayer()));
                        }
                        //black list --> blocks that are not allowed to be broken
                    } else if (!recData.getWBBlocks().contains(event.getBlock().getType())
                            && mgm.getActivatePlayerRecorder()) {
                        recData.addBlock(event.getBlock(), playerManager.getMinigamePlayer(event.getPlayer()));
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * record hanging entities like item frames placed by players
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void hangingPlace(HangingPlaceEvent event) {
        if (event.getPlayer() != null) {
            MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(event.getPlayer());
            if (mgPlayer.isInMinigame()) {
                Minigame mgm = mgPlayer.getMinigame();
                RecorderData recData = mgm.getRecorderData();

                Material usedMaterial;
                if (event.getItemStack() == null) {
                    //using a leash on a fence still uses the deprecated version of this event, without an item stack
                    if (event.getEntity() instanceof LeashHitch) {
                        usedMaterial = Material.LEAD;
                    } else {
                        //we have no idea what Material was used
                        return;
                    }
                } else {
                    usedMaterial = event.getItemStack().getType();
                }

                if (recData.getWhitelistMode()) {
                    //white list --> blocks that are allowed to be broken
                    if (recData.getWBBlocks().contains(usedMaterial) && mgm.getActivatePlayerRecorder()) {
                        recData.addEntity(event.getEntity(), mgPlayer, true);
                        return;
                    }
                    //black list --> blocks that are not allowed to be broken
                } else if (!recData.getWBBlocks().contains(usedMaterial) && mgm.getActivatePlayerRecorder()) {
                    recData.addEntity(event.getEntity(), mgPlayer, true);
                    return;
                }

                event.setCancelled(true);
            }
        }
    }

    /**
     * record animals killed by players
     * Note: entities like monsters or armor stands getting recorded only if regen regions are getting used
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void animalHurt(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Animals animal) {
            //did the player kill the animal?
            if (animal.getHealth() <= event.getDamage()) {
                MinigamePlayer mgPlayer = null;

                //try to get the player directly or as shooter of an arrow
                if (event.getDamager() instanceof Player player) {
                    mgPlayer = playerManager.getMinigamePlayer(player);
                } else if (event.getDamager() instanceof Arrow arrow &&
                        arrow.getShooter() instanceof Player player) {
                    mgPlayer = playerManager.getMinigamePlayer(player);
                }

                if (mgPlayer != null) {
                    if (mgPlayer.isInMinigame() && mgPlayer.getMinigame().getActivatePlayerRecorder()) {
                        mgPlayer.getMinigame().getRecorderData().addEntity(animal, mgPlayer, false);
                    }
                }
            }
        }
    }

    /**
     * record hanging entities getting removed by a player
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void hangingBreak(HangingBreakByEntityEvent event) {
        MinigamePlayer mgPlayer = null;

        if (event.getRemover() instanceof Player player) {
            mgPlayer = playerManager.getMinigamePlayer(player);
        } else if (event.getRemover() instanceof Arrow arrow &&
                arrow.getShooter() instanceof Player player) {
            mgPlayer = playerManager.getMinigamePlayer(player);

        }
        if (mgPlayer != null) {
            if (mgPlayer.isInMinigame() && mgPlayer.getMinigame().getActivatePlayerRecorder()) {
                mgPlayer.getMinigame().getRecorderData().addEntity(event.getEntity(), mgPlayer, false);
            }
        }
    }

    /**
     * record arrows shot by players
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void arrowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(player);

            if (mgPlayer.isInMinigame() && mgPlayer.getMinigame().getActivatePlayerRecorder()) {
                mgPlayer.getMinigame().getRecorderData().addEntity(event.getProjectile(), mgPlayer, true);
            }
        }
    }

    /**
     * record ender perl entities (important if somewhere caught in bubble columns)
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void throwEnderPearl(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
            MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(player);

            if (mgPlayer.isInMinigame() && mgPlayer.getMinigame().getActivatePlayerRecorder()) {
                mgPlayer.getMinigame().getRecorderData().addEntity(event.getEntity(), mgPlayer, true);
            }
        }
    }

    /**
     * record liquid drained by players via bucket fill
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void bucketFill(PlayerBucketFillEvent event) {
        MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame()) {
            Minigame mgm = mgPlayer.getMinigame();
            RecorderData recData = mgm.getRecorderData();

            if (mgm.canBlockBreak()) {
                if (recData.getWhitelistMode()) {
                    //white list --> blocks that are allowed to be broken
                    if (recData.getWBBlocks().contains(event.getBlock().getType()) && mgm.getActivatePlayerRecorder()) {
                        recData.addBlock(event.getBlockClicked(), playerManager.getMinigamePlayer(event.getPlayer()));
                    }
                    //black list --> blocks that are not allowed to be broken
                } else if (!recData.getWBBlocks().contains(event.getBlock().getType())
                        && mgm.getActivatePlayerRecorder()) {
                    recData.addBlock(event.getBlockClicked(), playerManager.getMinigamePlayer(event.getPlayer()));
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * record liquid placed by players via bukkit empty
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void bucketEmpty(PlayerBucketEmptyEvent event) {
        MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame()) {
            Minigame mgm = mgPlayer.getMinigame();
            RecorderData recData = mgm.getRecorderData();

            if (mgm.canBlockPlace()) {
                if (recData.getWhitelistMode()) {
                    //white list --> blocks that are allowed to be broken
                    if (recData.getWBBlocks().contains(event.getBlock().getType()) && mgm.getActivatePlayerRecorder()) {
                        Location loc = new Location(event.getBlockClicked().getWorld(),
                                event.getBlockFace().getModX() + event.getBlockClicked().getX(),
                                event.getBlockFace().getModY() + event.getBlockClicked().getY(),
                                event.getBlockFace().getModZ() + event.getBlockClicked().getZ());
                        recData.addBlock(loc.getBlock(), playerManager.getMinigamePlayer(event.getPlayer()));
                    }
                    //black list --> blocks that are not allowed to be broken
                } else if (!recData.getWBBlocks().contains(event.getBlock().getType())
                        && mgm.getActivatePlayerRecorder()) {
                    Location loc = new Location(event.getBlockClicked().getWorld(),
                            event.getBlockFace().getModX() + event.getBlockClicked().getX(),
                            event.getBlockFace().getModY() + event.getBlockClicked().getY(),
                            event.getBlockFace().getModZ() + event.getBlockClicked().getZ());
                    recData.addBlock(loc.getBlock(), playerManager.getMinigamePlayer(event.getPlayer()));
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * record vehicles broken by players
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void vehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() != null) {
            if (event.getAttacker() instanceof Player player) {
                MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(player);
                Minigame mg = mgPlayer.getMinigame();

                if (mgPlayer.isInMinigame() && mg.getActivatePlayerRecorder()) {
                    mg.getRecorderData().addEntity(event.getVehicle(), mgPlayer, false);
                }
            }
        }
    }
}
