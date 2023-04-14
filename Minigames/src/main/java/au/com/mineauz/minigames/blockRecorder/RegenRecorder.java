package au.com.mineauz.minigames.blockRecorder;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

/**
 * advanced recorder if a minigame has a regen area
 */
public class RegenRecorder implements Listener {
    private final Minigame minigame;
    private final RecorderData recorderData;

    public RegenRecorder(Minigame minigame) {
        this.minigame = minigame;
        this.recorderData = minigame.getRecorderData();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void vehicleCreate(VehicleCreateEvent event) {
        if (minigame.hasPlayers() && recorderData.isInRegenArea(event.getVehicle().getLocation())) {
            recorderData.addEntity(event.getVehicle(), null, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void vehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() == null) {
            if (minigame.hasPlayers() && recorderData.isInRegenArea(event.getVehicle().getLocation())) {
                recorderData.addEntity(event.getVehicle(), null, false);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void animalDeath(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Animals animal) {
            if (minigame.hasPlayers() && !(event.getDamager() instanceof Player)) {
                Location entityLoc = event.getEntity().getLocation();

                if (recorderData.isInRegenArea(entityLoc)) {
                    if (animal.getHealth() <= event.getDamage()) {
                        recorderData.addEntity(event.getEntity(), null, false);
                    }
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void mobSpawnEvent(CreatureSpawnEvent event) {
        if (minigame.hasPlayers() && recorderData.isInRegenArea(event.getLocation())) {
            recorderData.addEntity(event.getEntity(), null, true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void entityExplode(EntityExplodeEvent event) {
        if (minigame.hasPlayers()) {
            Location block = event.getLocation().getBlock().getLocation();
            if (recorderData.isInRegenArea(block)) {
                List<Block> blocks = new ArrayList<>(event.blockList());

                for (Block bl : blocks) {
                    if ((recorderData.getWhitelistMode() && recorderData.getWBBlocks().contains(bl.getType())) ||
                            (!recorderData.getWhitelistMode() && !recorderData.getWBBlocks().contains(bl.getType()))) {
                        recorderData.addBlock(bl, null);
                    } else {
                        event.blockList().remove(bl);
                    }
                }
            }
        //don't allow explosions while regenerating
        } else if (minigame.isRegenerating() && minigame.getRecorderData().isInRegenArea(event.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void itemDrop(ItemSpawnEvent event) {
        if (minigame.hasPlayers()) {
            Location ent = event.getLocation();
            if (recorderData.isInRegenArea(ent)) {
                recorderData.addEntity(event.getEntity(), null, true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void physicalBlock(EntityChangeBlockEvent event) {
        if (recorderData.isInRegenArea(event.getBlock().getLocation())) {
            if (minigame.isRegenerating()) {
                event.setCancelled(true);
                return;
            }
            if (event.getTo().hasGravity()) {
                if (minigame.hasPlayers() || event.getEntity().hasMetadata("FellInMinigame")) {
                    recorderData.addEntity(event.getEntity(), null, true);
                }
            } else if (event.getEntityType() == EntityType.FALLING_BLOCK && minigame.hasPlayers()) {
                event.getEntity().setMetadata("FellInMinigame", new FixedMetadataValue(Minigames.getPlugin(), true));
                recorderData.addEntity(event.getEntity(), null, true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void cartHopperPickup(InventoryPickupItemEvent event) {
        if (minigame.hasPlayers() && event.getInventory().getHolder() instanceof HopperMinecart) {
            Location loc = ((HopperMinecart) event.getInventory().getHolder()).getLocation();
            if (recorderData.isInRegenArea(loc)) {
                recorderData.addEntity((HopperMinecart) event.getInventory().getHolder(), null, false);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void cartMoveItem(InventoryMoveItemEvent event) {
        if (!minigame.hasPlayers()) return;

        Location loc;
        if (event.getInitiator().getHolder() instanceof HopperMinecart) {
            loc = ((HopperMinecart) event.getInitiator().getHolder()).getLocation().clone();
            if (recorderData.isInRegenArea(loc))
                recorderData.addEntity((Entity) event.getInitiator().getHolder(), null, false);
        }

        if (event.getDestination().getHolder() instanceof HopperMinecart) {
            loc = ((HopperMinecart) event.getDestination().getHolder()).getLocation().clone();
            if (recorderData.isInRegenArea(loc))
                recorderData.addEntity((Entity) event.getInitiator().getHolder(), null, false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void physEvent(BlockPhysicsEvent event) {
        if (minigame.isRegenerating() && recorderData.isInRegenArea(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void waterFlow(BlockFromToEvent event) {
        if (minigame.isRegenerating() && recorderData.isInRegenArea(event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void fireSpread(BlockSpreadEvent event) {
        if (minigame.isRegenerating() && recorderData.isInRegenArea(event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void interact(PlayerInteractEvent event) {
        if (minigame.isRegenerating() && recorderData.isInRegenArea(event.getClickedBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
}
