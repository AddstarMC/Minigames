package au.com.mineauz.minigames.blockRecorder;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.block.*;
import org.bukkit.block.data.Rotatable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.List;

public class RollbackScheduler implements Runnable {

    private final Iterator<MgBlockData> iterator;
    private final BukkitTask task;
    private final Minigame minigame;
    private final MinigamePlayer modifier;

    public RollbackScheduler(List<MgBlockData> blocks,  Minigame minigame, MinigamePlayer modifier) {
        iterator = blocks.iterator();
        this.minigame = minigame;
        this.modifier = modifier;
        int delay = minigame.getRegenDelay() * 20 + 1;
        task = Bukkit.getScheduler().runTaskTimer(Minigames.getPlugin(), this, delay, 1);
    }

    @Override
    public void run() {
        long time = System.nanoTime();
        while (iterator.hasNext()) {
            MgBlockData bdata = iterator.next();
            bdata.getBlockState().update(true);
            //there might be odd cases when we don't apply physics. This smells like a bucket full of strange bugs. might change in the future.
            bdata.getLocation().getBlock().setBlockData(bdata.getBukkitBlockData(), false);

            if (bdata.getItems() != null){
                if (bdata.getLocation().getBlock().getState() instanceof InventoryHolder inventoryHolder)
                    inventoryHolder.getInventory().setContents(bdata.getItems());
            }

            if (System.nanoTime() - time > Minigames.getPlugin().getConfig().getDouble("regeneration.maxDelay") * 1000000)
                return;
        }

        // When rolling back a single player's changes don't change the overall games state
        if (modifier == null) {
            HandlerList.unregisterAll(minigame.getRecorderData());
            HandlerList.bakeAll();

            minigame.setState(MinigameState.IDLE);
        }

        task.cancel();
    }

}
