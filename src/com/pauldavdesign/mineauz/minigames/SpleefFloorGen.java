package com.pauldavdesign.mineauz.minigames;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class SpleefFloorGen {
	public Location spoint1;
	public Location spoint2;
	
	public SpleefFloorGen(Location point1, Location point2){
		Double minX;
		Double maxX;
		Double minY;
		Double maxY;
		Double minZ;
		Double maxZ;
		
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
		
		spoint1 = point1.clone();
		spoint2 = point2.clone();
		
		spoint1.setX(minX);
		spoint1.setY(minY - 1);
		spoint1.setZ(minZ);
		
		spoint2.setX(maxX);
		spoint2.setY(maxY - 1);
		spoint2.setZ(maxZ);
	}
	
	public void regenFloor(final Material mat){
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {

			Location curblock = spoint1.clone();
			int x = curblock.getBlockX();
			int z = curblock.getBlockZ();
			int y = spoint2.getBlockY();
			
			@Override
			public void run() {
				do{
					curblock.setZ(z);
					curblock.setX(x);
					curblock.setY(y);
					for(int i = spoint1.getBlockX(); i <= spoint2.getBlockX() + 1; i++){
						for(int k = spoint1.getBlockZ(); k <= spoint2.getBlockZ() + 1; k++){
							if(curblock.getBlock().getType() == Material.AIR){
								curblock.getBlock().setType(mat);
							}
							curblock.setZ(k);
						}
						curblock.setX(i);
						curblock.setZ(z);
						try{
							Thread.sleep(5);
						}catch(InterruptedException e){
							e.printStackTrace();
						}
					}
					y -= 3;
				}while(y >= spoint1.getBlockY());
			}
		});
	}
}
