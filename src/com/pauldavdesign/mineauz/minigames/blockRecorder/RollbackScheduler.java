package com.pauldavdesign.mineauz.minigames.blockRecorder;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitTask;

import com.pauldavdesign.mineauz.minigames.Minigames;

public class RollbackScheduler implements Runnable {
	
	private Iterator<BlockData> iterator;
	private Iterator<BlockData> physIterator;
	private BukkitTask task;
	
	public RollbackScheduler(List<BlockData> blocks, List<BlockData> physblocks){
		iterator = blocks.iterator();
		physIterator = physblocks.iterator();
		task = Bukkit.getScheduler().runTaskTimer(Minigames.plugin, this, 1, 1);
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
			if(bdata.getBlockState().getType() == Material.SIGN_POST || bdata.getBlockState().getType() == Material.WALL_SIGN){
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
			if(System.nanoTime() - time > Minigames.plugin.getConfig().getDouble("regeneration.maxDelay") * 1000000)
				return;
		}
		task.cancel();
	}

}
