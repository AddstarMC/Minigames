package au.com.mineauz.minigames.blockRecorder;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import org.bukkit.Bukkit;
import org.bukkit.block.*;
import org.bukkit.block.data.Rotatable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.List;

public class RollbackScheduler implements Runnable {

    private final Iterator<MgBlockData> iterator;
    private final Iterator<MgBlockData> physIterator;
    private final BukkitTask task;
    private final Minigame minigame;
    private final MinigamePlayer modifier;

    public RollbackScheduler(List<MgBlockData> blocks, List<MgBlockData> physblocks, Minigame minigame, MinigamePlayer modifier) {
        iterator = blocks.iterator();
        physIterator = physblocks.iterator();
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
            if (System.nanoTime() - time > Minigames.getPlugin().getConfig().getDouble("regeneration.maxDelay") * 1000000)
                return;
        }
        while (physIterator.hasNext()) {
            MgBlockData bdata = physIterator.next();
            bdata.getBlockState().update(true);
            switch (bdata.getBlockState().getType()) {
                case OAK_SIGN:
                case OAK_WALL_SIGN:
                    if (bdata.getBlockState() instanceof Sign) {
                        Sign sign = (Sign) bdata.getLocation().getBlock().getState();
                        Sign signOld = (Sign) bdata.getBlockState();
                        sign.setLine(0, signOld.getLine(0));
                        sign.setLine(1, signOld.getLine(1));
                        sign.setLine(2, signOld.getLine(2));
                        sign.setLine(3, signOld.getLine(3));
                        sign.update();
                    }
                    break;
                case SKELETON_SKULL:
                case WITHER_SKELETON_SKULL:
                case CREEPER_HEAD:
                case PLAYER_HEAD:
                case PLAYER_WALL_HEAD:
                case SKELETON_WALL_SKULL:
                case CREEPER_WALL_HEAD:
                case WITHER_SKELETON_WALL_SKULL:
                    if (bdata.getBlockState() instanceof Skull) {
                        Skull skull = (Skull) bdata.getBlockState().getBlock().getState();
                        Rotatable skullData = (Rotatable) skull.getBlockData();
                        Skull orig = (Skull) bdata.getBlockState();
                        Rotatable origData = (Rotatable) orig.getBlockData();
                        if (orig.getOwningPlayer() != null) skull.setOwningPlayer(orig.getOwningPlayer());
                        skullData.setRotation(origData.getRotation());
                        skull.update();
                    }
                    break;
                case JUKEBOX:
                    Jukebox jbox = (Jukebox) bdata.getLocation().getBlock().getState();
                    Jukebox orig = (Jukebox) bdata.getBlockState();
                    jbox.setPlaying(orig.getPlaying());
                    jbox.update();
                    break;
                case FLOWER_POT:
                case POTTED_ACACIA_SAPLING:
                case POTTED_ALLIUM:
                case POTTED_AZURE_BLUET:
                case POTTED_BIRCH_SAPLING:
            }
            if (bdata.getLocation().getBlock().getState() instanceof InventoryHolder) {
                InventoryHolder block = (InventoryHolder) bdata.getLocation().getBlock().getState();
                if (bdata.getItems() != null)
                    block.getInventory().setContents(bdata.getItems().clone());
            }


            if (System.nanoTime() - time > Minigames.getPlugin().getConfig().getDouble("regeneration.maxDelay") * 1000000)
                return;
        }

        // When rolling back a single player's changes dont change the overall games state
        if (modifier == null) {
            HandlerList.unregisterAll(minigame.getBlockRecorder());
            HandlerList.bakeAll();

            minigame.setState(MinigameState.IDLE);
        }

        task.cancel();
    }

}
