package au.com.mineauz.minigames.blockRecorder;

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
 */
public class BasicRecorder implements Listener {

    private final MinigamePlayerManager playerManager = Minigames.getPlugin().getPlayerManager();

    //helper methode to not write code double
    //this checks for Minigame signs and cancels the event, adds the block to the block recorder and handles block drops
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

        recData.addBlock(event.getBlock(), mgPlayer);
        if (!mgm.canBlocksdrop()) {
            event.getBlock().setType(Material.AIR);
            return true;
        }

        return false;
    }

    //replace broken blocks (and stop breaking of blocks the player is not allowed to) <-- todo move this stopping into another more fitting class
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
                    }
                    //black list --> blocks that are not allowed to be broken
                } else if (!recData.getWBBlocks().contains(event.getBlock().getType())) {
                    if (handleBlockBreak(event, mgPlayer)) {
                        event.setCancelled(true);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    //remove placed blocks (and stop placing ones the player is not allowed to) <-- todo move this stopping into another more fitting class
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
                        recData.addBlock(event.getBlockReplacedState(), mgPlayer);
                    }
                    //black list --> blocks that are not allowed to be placed
                } else if (!recData.getWBBlocks().contains(event.getBlock().getType())) {
                    recData.addBlock(event.getBlockReplacedState(), mgPlayer);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    //reset changed inventories
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void changeAnotherInventory(PlayerInteractEvent event) {
        MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame() && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && !mgPlayer.getMinigame().isSpectator(mgPlayer)) {

            Minigame mgm = mgPlayer.getMinigame();
            RecorderData recData = mgm.getRecorderData();

            if (event.getClickedBlock().getState() instanceof InventoryHolder) {
                recData.addBlock(event.getClickedBlock().getLocation().getBlock(), mgPlayer);
            }
        }
    }

    //re-add drained liquid
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void bucketFill(PlayerBucketFillEvent event) {
        MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame()) {
            Minigame mgm = mgPlayer.getMinigame();
            RecorderData recData = mgm.getRecorderData();

            if (mgm.canBlockBreak()) {
                if (recData.getWhitelistMode()) {
                    //white list --> blocks that are allowed to be broken
                    if (recData.getWBBlocks().contains(event.getBlock().getType())) {
                        recData.addBlock(event.getBlockClicked(), playerManager.getMinigamePlayer(event.getPlayer()));
                    }
                    //black list --> blocks that are not allowed to be broken
                } else if (!recData.getWBBlocks().contains(event.getBlock().getType())) {
                    recData.addBlock(event.getBlockClicked(), playerManager.getMinigamePlayer(event.getPlayer()));
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    //remove lighted fire
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
                        if (recData.getWBBlocks().contains(event.getBlock().getType())) {
                            recData.addBlock(event.getBlock(), playerManager.getMinigamePlayer(event.getPlayer()));
                        }
                        //black list --> blocks that are not allowed to be broken
                    } else if (!recData.getWBBlocks().contains(event.getBlock().getType())) {
                        recData.addBlock(event.getBlock(), playerManager.getMinigamePlayer(event.getPlayer()));
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    //remove placed hanging entities
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

                //todo we might want a flag parallel to BlockPlace/break for entities spawn / hurt
                if (recData.getWhitelistMode()) {
                    //white list --> blocks that are allowed to be broken
                    if (recData.getWBBlocks().contains(usedMaterial)) {
                        recData.addEntity(event.getEntity(), mgPlayer, true);
                        return;
                    }
                    //black list --> blocks that are not allowed to be broken
                } else if (!recData.getWBBlocks().contains(usedMaterial)) {
                    recData.addEntity(event.getEntity(), mgPlayer, true);
                    return;
                }

                event.setCancelled(true);
            }
        }
    }

    //revive killed animals
    //entities like monsters or armor stands are reset if restore regions is turned on.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void animalHurt(EntityDamageByEntityEvent event) { //todo check for spawn to not revive animals spawned while the minigame was running.
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
                    if (mgPlayer.isInMinigame()) {
                        mgPlayer.getMinigame().getRecorderData().addEntity(animal, mgPlayer, false);
                    }
                }
            }
        }
    }

    //revive hanging entities
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
            if (mgPlayer.isInMinigame()) {
                mgPlayer.getMinigame().getRecorderData().addEntity(event.getEntity(), mgPlayer, false);
            }
        }
    }

    //remove arrow entities
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void arrowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(player);

            if (mgPlayer.isInMinigame()) {
                mgPlayer.getMinigame().getRecorderData().addEntity(event.getProjectile(), mgPlayer, true);
            }
        }
    }

    //remove ender perl entities (important if some where caught in bubble columns)
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void throwEnderPearl(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
            MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(player);

            if (mgPlayer.isInMinigame()) {
                mgPlayer.getMinigame().getRecorderData().addEntity(event.getEntity(), mgPlayer, true);
            }
        }
    }

    //remove liquid source
    //todo this doesn't handle changed blocks of flowing liquids, as well as new created water sources
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void bucketEmpty(PlayerBucketEmptyEvent event) {
        MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(event.getPlayer());

        if (mgPlayer.isInMinigame()) {
            Minigame mgm = mgPlayer.getMinigame();
            RecorderData recData = mgm.getRecorderData();

            if (mgm.canBlockPlace()) {
                if (recData.getWhitelistMode()) {
                    //white list --> blocks that are allowed to be broken
                    if (recData.getWBBlocks().contains(event.getBlock().getType())) {
                        Location loc = new Location(event.getBlockClicked().getWorld(),
                                event.getBlockFace().getModX() + event.getBlockClicked().getX(),
                                event.getBlockFace().getModY() + event.getBlockClicked().getY(),
                                event.getBlockFace().getModZ() + event.getBlockClicked().getZ());
                        recData.addBlock(loc.getBlock(), playerManager.getMinigamePlayer(event.getPlayer()));
                    }
                    //black list --> blocks that are not allowed to be broken
                } else if (!recData.getWBBlocks().contains(event.getBlock().getType())) {
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

    //revive vehicles
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void vehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() != null) {
            if (event.getAttacker() instanceof Player player) {
                MinigamePlayer mgPlayer = playerManager.getMinigamePlayer(player);
                Minigame mg = mgPlayer.getMinigame();

                if (mgPlayer.isInMinigame()) {
                    mg.getRecorderData().addEntity(event.getVehicle(), mgPlayer, false);
                }
            }
        }
    }
}
