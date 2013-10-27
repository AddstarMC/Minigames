package com.pauldavdesign.mineauz.minigames.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;

public class Menu {
	private int rows = 1;
	private ItemStack[] pageView;
	private Map<Integer, MenuItem> pageMap = new HashMap<Integer, MenuItem>();
	private String name = "Menu";
	private boolean allowModify = false;
	private Menu previousPage = null;
	private Menu nextPage = null;
	private MinigamePlayer viewer = null;
	private int reopenTimerID = -1;
	private Inventory inv = null;
	
	public Menu(int rows, String name, MinigamePlayer viewer){
		this.rows = rows;
		this.name = name;
		pageView = new ItemStack[rows*9];
		this.viewer = viewer;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean addItem(MenuItem item, int slot){
		if(!pageMap.containsKey(slot) && slot < pageView.length){
			item.setContainer(this);
			item.setSlot(slot);
			pageMap.put(slot, item);
			if(inv != null){
				inv.setItem(slot, item.getItem());
			}
			return true;
		}
		return false;
	}
	
	public void removeItem(int slot){
		if(pageMap.containsKey(slot)){
			pageMap.remove(slot);
			pageView[slot] = null;
			if(inv != null){
				inv.setItem(slot, null);
			}
		}
	}
	
	public void addItemStack(ItemStack item, int slot){
		inv.setItem(slot, item);
	}
	
	private void populateMenu(){
		for(Integer key : pageMap.keySet()){
			pageView[key] = pageMap.get(key).getItem();
		}
	}
	
	public void displayMenu(MinigamePlayer ply){
		populateMenu();
		
		inv = Bukkit.createInventory(ply.getPlayer(), rows*9, name);
		inv.setContents(pageView);
		ply.getPlayer().openInventory(inv);
		ply.setMenu(this);
	}
	
	public boolean getAllowModify(){
		return allowModify;
	}
	
	public void setAllowModify(boolean canModify){
		allowModify = canModify;
	}
	
	public MenuItem getClicked(int slot){
		return pageMap.get(slot);
	}
	
	public boolean hasMenuItem(int slot){
		if(pageMap.containsKey(slot)){
			return true;
		}
		return false;
	}
	
	public int getSize(){
		return rows * 9;
	}
	
	public void setNextPage(Menu page){
		nextPage = page;
	}
	
	public Menu getNextPage(){
		return nextPage;
	}
	
	public boolean hasNextPage(){
		if(nextPage != null)
			return true;
		return false;
	}
	
	public void setPreviousPage(Menu page){
		previousPage = page;
	}
	
	public Menu getPreviousPage(){
		return previousPage;
	}
	
	public boolean hasPreviousPage(){
		if(previousPage != null)
			return true;
		return false;
	}
	
	public MinigamePlayer getViewer(){
		return viewer;
	}
	
	public void startReopenTimer(int time){
		reopenTimerID = Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
			
			@Override
			public void run() {
				viewer.setNoClose(false);
				viewer.setManualEntry(null);
				displayMenu(viewer);
			}
		}, (long)(time * 20));
	}
	
	public void cancelReopenTimer(){
		if(reopenTimerID != -1){
			viewer.setNoClose(false);
			viewer.setManualEntry(null);
			Bukkit.getScheduler().cancelTask(reopenTimerID);
		}
	}
	
	public ItemStack[] getInventory(){
		ItemStack[] inv = new ItemStack[getSize()];
		
		for(int i = 0; i < this.inv.getContents().length; i++){
			if(!pageMap.containsKey(i)){
				inv[i] = this.inv.getContents()[i];
			}
		}
		
		return inv;
	}
	
	public Set<Integer> getSlotMap(){
		return pageMap.keySet();
	}
}
