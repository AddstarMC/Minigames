package au.com.mineauz.minigames.menu;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;

public class MenuListener implements Listener {
	
	private Minigames plugin = Minigames.plugin;
	
	@EventHandler(ignoreCancelled = true)
	private void clickMenu(InventoryClickEvent event){
		MinigamePlayer ply = plugin.pdata.getMinigamePlayer((Player)event.getWhoClicked());
		if(ply.isInMenu()){
			if(event.getRawSlot() < ply.getMenu().getSize()){
				if(!ply.getMenu().getAllowModify() || ply.getMenu().hasMenuItem(event.getRawSlot()))
					event.setCancelled(true);
				
				MenuItem item = ply.getMenu().getClicked(event.getRawSlot());
				if(item != null){
					ItemStack disItem = null;
					if(event.getClick() == ClickType.LEFT){
						if(event.getCursor().getType() != Material.AIR)
							disItem = item.onClickWithItem(ply, event.getCursor());
						else
							disItem = item.onClick(ply);
					}
					else if(event.getClick() == ClickType.RIGHT)
						disItem = item.onRightClick(ply);
					else if(event.getClick() == ClickType.SHIFT_LEFT)
						disItem = item.onShiftClick(ply);
					else if(event.getClick() == ClickType.SHIFT_RIGHT)
						disItem = item.onShiftRightClick(ply);
					else if(event.getClick() == ClickType.DOUBLE_CLICK)
						disItem = item.onDoubleClick(ply);
					
					if(item != null)
						event.setCurrentItem(disItem);
				}
			}
		}
		else if(ply.isInMinigame()){
			if((ply.getLoadout().isArmourLocked() && event.getSlot() >= 36 && event.getSlot() <= 39) || 
					(ply.getLoadout().isInventoryLocked() && event.getSlot() >= 0 && event.getSlot() <= 35))
				event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void dragMenu(InventoryDragEvent event){
		MinigamePlayer ply = plugin.pdata.getMinigamePlayer((Player)event.getWhoClicked());
		if(ply.isInMenu()){
			if(!ply.getMenu().getAllowModify()){
				for(int slot : event.getRawSlots()){
					if(slot < ply.getMenu().getSize()){
						event.setCancelled(true);
						break;
					}
				}
			}
			else{
				Set<Integer> slots = new HashSet<Integer>();
				slots.addAll(event.getRawSlots());
				
				for(int slot : slots){
					if(ply.getMenu().hasMenuItem(slot)){
						event.getRawSlots().remove(slot);
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void closeMenu(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player))
			return;
		
		MinigamePlayer ply = plugin.pdata.getMinigamePlayer((Player)event.getPlayer());
		if(ply == null) return;
		
		Menu menu = ply.getMenu();
		if (menu != null && !ply.getNoClose()) {
			menu.onCloseMenu(ply);
		}
	}
	
	@EventHandler
	private void manualItemEntry(AsyncPlayerChatEvent event){
		MinigamePlayer ply = plugin.pdata.getMinigamePlayer(event.getPlayer());
		if(ply.isInMenu() && ply.getNoClose() && ply.getManualEntry() != null){
			event.setCancelled(true);
			ply.getManualEntry().completeManualEntry(ply, event.getMessage());
		}
		
	}
}
