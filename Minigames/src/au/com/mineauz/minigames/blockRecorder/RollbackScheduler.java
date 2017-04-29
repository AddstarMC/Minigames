package au.com.mineauz.minigames.blockRecorder;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.block.FlowerPot;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;

public class RollbackScheduler implements Runnable {
	
	private Iterator<BlockData> iterator;
	private Iterator<BlockData> physIterator;
	private BukkitTask task;
	private Minigame minigame;
	private MinigamePlayer modifier;
	
	public RollbackScheduler(List<BlockData> blocks, List<BlockData> physblocks, Minigame minigame, MinigamePlayer modifier){
		iterator = blocks.iterator();
		physIterator = physblocks.iterator();
		this.minigame = minigame;
		this.modifier = modifier;
		int delay = minigame.getRegenDelay() * 20 + 1;
		task = Bukkit.getScheduler().runTaskTimer(Minigames.plugin, this, delay, 1);
	}

	@Override
	public void run() {
		long time = System.nanoTime();
		while(iterator.hasNext()){
			BlockData bdata = iterator.next();
			bdata.getBlockState().update(true);
			if(System.nanoTime() - time > Minigames.plugin.getConfig().getDouble("regeneration.maxDelay") * 1000000)
				return;
		}
		while(physIterator.hasNext()){
			BlockData bdata = physIterator.next();
			bdata.getBlockState().update(true);
			if((bdata.getBlockState().getType() == Material.SIGN_POST || bdata.getBlockState().getType() == Material.WALL_SIGN) && 
					bdata.getBlockState() instanceof Sign){
				Sign sign = (Sign) bdata.getLocation().getBlock().getState();
				Sign signOld = (Sign) bdata.getBlockState();
				sign.setLine(0, signOld.getLine(0));
				sign.setLine(1, signOld.getLine(1));
				sign.setLine(2, signOld.getLine(2));
				sign.setLine(3, signOld.getLine(3));
				sign.update();
			}
			else if(bdata.getLocation().getBlock().getState() instanceof InventoryHolder){
				InventoryHolder block = (InventoryHolder) bdata.getLocation().getBlock().getState();
				if(bdata.getItems() != null)
					block.getInventory().setContents(bdata.getItems().clone());
			}
			else if(bdata.getBlockState() instanceof FlowerPot){
				FlowerPot pot = (FlowerPot) bdata.getLocation().getBlock().getState();
				if(bdata.getSpecialData("contents") != null)
					pot.setContents((MaterialData)bdata.getSpecialData("contents"));
			}
			else if(bdata.getBlockState().getType() == Material.JUKEBOX){
				Jukebox jbox = (Jukebox) bdata.getLocation().getBlock().getState();
				Jukebox orig = (Jukebox) bdata.getBlockState();
				jbox.setPlaying(orig.getPlaying());
				jbox.update();
			}
			else if(bdata.getBlockState().getType() == Material.SKULL){
				Skull skull = (Skull) bdata.getBlockState().getBlock().getState();
				Skull orig = (Skull) bdata.getBlockState();
				skull.setOwningPlayer(orig.getOwningPlayer());
				skull.setRotation(orig.getRotation());
				skull.setSkullType(orig.getSkullType());
				skull.update();
			}
			
			if(System.nanoTime() - time > Minigames.plugin.getConfig().getDouble("regeneration.maxDelay") * 1000000)
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
