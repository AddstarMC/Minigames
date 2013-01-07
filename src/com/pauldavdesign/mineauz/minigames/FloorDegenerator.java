package com.pauldavdesign.mineauz.minigames;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class FloorDegenerator extends Thread{
	private boolean running = true;
	private Location xSideNeg1;
	private Location xSidePos1;
	private Location zSideNeg1;
	private Location zSidePos1;
	private Location xSideNeg2;
	private Location xSidePos2;
	private Location zSideNeg2;
	private Location zSidePos2;
	private static Minigames plugin = Minigames.plugin;
	private int timeDelay = 30;
	private Minigame mgm = null;
	
	public FloorDegenerator(Location point1, Location point2, Minigame mgm){
		timeDelay = plugin.getConfig().getInt("multiplayer.floordegenerator.time");
		this.mgm = mgm;
		double minX;
		double maxX;
		double minY;
		double maxY;
		double minZ;
		double maxZ;
		
		Double x1 = point1.getX();
		Double x2 = point2.getX();
		Double y1 = point1.getY();
		Double y2 = point2.getY();
		Double z1 = point1.getZ();
		Double z2 = point2.getZ();
		
		if(x1 < x2){
			minX = x1;
			maxX = x2;
		}
		else{
			minX = x2;
			maxX = x1;
		}
		
		if(y1 < y2){
			minY = y1;
			maxY = y2;
		}
		else{
			minY = y2;
			maxY = y1;
		}
		
		if(z1 < z2){
			minZ = z1;
			maxZ = z2;
		}
		else{
			minZ = z2;
			maxZ = z1;
		}
		
		minY--;
		maxY--;
		
		xSideNeg1 = new Location(point1.getWorld(), minX, minY, minZ);
		xSideNeg2 = new Location(point1.getWorld(), maxX, maxY, minZ);
		zSideNeg1 = new Location(point1.getWorld(), minX, minY, minZ);
		zSideNeg2 = new Location(point1.getWorld(), minX, maxY, maxZ);
		xSidePos1 = new Location(point1.getWorld(), minX, minY, maxZ);
		xSidePos2 = new Location(point1.getWorld(), maxX, maxY, maxZ);
		zSidePos1 = new Location(point1.getWorld(), maxX, minY, minZ);
		zSidePos2 = new Location(point1.getWorld(), maxX, maxY, maxZ);
	}
	
	public void run(){
		try{
			Thread.sleep(timeDelay * 1000);
		}
		catch(InterruptedException e){
			Bukkit.getLogger().severe("Floor degenerator failed!");
			e.printStackTrace();
		}
		while(running){
			degenerateSide(xSideNeg1, xSideNeg2);
			degenerateSide(xSidePos1, xSidePos2);
			degenerateSide(zSideNeg1, zSideNeg2);
			degenerateSide(zSidePos1, zSidePos2);
			
			incrementSide();
			if(xSideNeg1.getZ() >= xSidePos1.getZ() || zSideNeg1.getX() >= zSidePos1.getX()){
				stopDegenerator();
			}
			try{
				Thread.sleep(timeDelay * 1000);
			}
			catch(InterruptedException e){
				Bukkit.getLogger().severe("Floor degenerator failed!");
				e.printStackTrace();
			}
		}
	}
	
	private void incrementSide(){
		xSideNeg1.setZ(xSideNeg1.getZ() + 1);
		xSideNeg2.setZ(xSideNeg2.getZ() + 1);
		xSidePos1.setZ(xSidePos1.getZ() - 1);
		xSidePos2.setZ(xSidePos2.getZ() - 1);
		zSideNeg1.setX(zSideNeg1.getX() + 1);
		zSideNeg2.setX(zSideNeg2.getX() + 1);
		zSidePos1.setX(zSidePos1.getX() - 1);
		zSidePos2.setX(zSidePos2.getX() - 1);
	}
	
	private void degenerateSide(Location loc1, Location loc2){
		Location curblock = loc1.clone();
		int x = curblock.getBlockX();
		int z = curblock.getBlockZ();
		int y = curblock.getBlockY();
		do{
			curblock.setZ(z);
			curblock.setX(x);
			curblock.setY(y);
			for(int i = loc1.getBlockX(); i <= loc2.getBlockX() + 1; i++){
				for(int k = loc1.getBlockZ(); k <= loc2.getBlockZ() + 1; k++){
					if(curblock.getBlock().getType() == mgm.getSpleefFloorMaterial()){
						mgm.getBlockRecorder().addBlock(curblock.getBlock(), null);
						curblock.getBlock().setType(Material.AIR);
					}
					curblock.setZ(k);
				}
				curblock.setX(i);
				curblock.setZ(z);
			}
			y++;
		}while(y <= loc2.getBlockY());
	}
	
	public void stopDegenerator(){
		running = false;
	}
}
