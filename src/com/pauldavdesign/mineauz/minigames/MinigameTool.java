package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemToolMode;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class MinigameTool {
	private ItemStack tool;
	private Minigame minigame = null;
	private MinigameToolMode mode = null;
	private String team = null;
	
	public MinigameTool(ItemStack tool){
		this.tool = tool;
		ItemMeta meta = tool.getItemMeta();
		if(meta.getLore() != null){
			String mg = ChatColor.stripColor(meta.getLore().get(0)).replace("Minigame: ", "");
			if(Minigames.plugin.mdata.hasMinigame(mg))
				minigame = Minigames.plugin.mdata.getMinigame(mg);
			
			String md = ChatColor.stripColor(meta.getLore().get(1)).replace("Mode: ", "");
			if(MinigameToolMode.getByName(md) != null)
				mode = MinigameToolMode.getByName(md);
			
			if(mode == MinigameToolMode.START && meta.getLore().size() == 3){
				team = ChatColor.stripColor(meta.getLore().get(2).replace("Team: ", ""));
			}
		}
		else{
			meta.setDisplayName(ChatColor.GREEN + "Minigame Tool");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.AQUA + "Minigame: " + ChatColor.WHITE + "None");
			lore.add(ChatColor.AQUA + "Mode: " + ChatColor.WHITE + "None");
			meta.setLore(lore);
			tool.setItemMeta(meta);
		}
	}
	
	public ItemStack getTool(){
		return tool;
	}
	
	public void setMinigame(Minigame minigame){
		ItemMeta meta = tool.getItemMeta();
		List<String> lore = meta.getLore();
		lore.set(0, ChatColor.AQUA + "Minigame: " + ChatColor.WHITE + minigame.getName());
		meta.setLore(lore);
		tool.setItemMeta(meta);
		this.minigame = minigame;
	}
	
	public void setMode(MinigameToolMode mode){
		ItemMeta meta = tool.getItemMeta();
		List<String> lore = meta.getLore();
		lore.set(1, ChatColor.AQUA + "Mode: " + ChatColor.WHITE + mode.getMode());
		meta.setLore(lore);
		tool.setItemMeta(meta);
		this.mode = mode;
	}
	
	public MinigameToolMode getMode(){
		return mode;
	}
	
	public Minigame getMinigame(){
		return minigame;
	}
	
	public void setTeam(String team){
		ItemMeta meta = tool.getItemMeta();
		List<String> lore = meta.getLore();
		if(team != null && team.matches("r(ed)?")){
			if(lore.size() == 3)
				lore.set(2, ChatColor.AQUA + "Team: " + ChatColor.RED + "Red");
			else
				lore.add(ChatColor.AQUA + "Team: " + ChatColor.RED + "Red");
		}
		else if(team != null && team.matches("b(lue)?")){
			if(lore.size() == 3)
				lore.set(2, ChatColor.AQUA + "Team: " + ChatColor.BLUE + "Blue");
			else
				lore.add(ChatColor.AQUA + "Team: " + ChatColor.BLUE + "Blue");
		}
		else{
			if(lore.size() == 3)
				lore.remove(2);
		}
			
		meta.setLore(lore);
		tool.setItemMeta(meta);
		this.team = team;
	}
	
	public String getTeam(){
		return team;
	}
	
	public void openMenu(MinigamePlayer player){
		Menu men = new Menu(2, "Set Tool Mode", player);
		List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(new MenuItemToolMode("Set Start Points", Material.SKULL_ITEM, MinigameToolMode.START));
		items.add(new MenuItemToolMode("Set Quit Point", Material.ENDER_PEARL, MinigameToolMode.QUIT));
		items.add(new MenuItemToolMode("Set End Point", Material.EYE_OF_ENDER, MinigameToolMode.END));
		items.add(new MenuItemToolMode("Set Lobby Point", Material.WOOD_DOOR, MinigameToolMode.LOBBY));
		items.add(new MenuItemToolMode("Set Regeneration Area", Material.GRASS, MinigameToolMode.REGEN_AREA));
		items.add(new MenuItemToolMode("Set Degeneration Area", Material.SAND, MinigameToolMode.DEGEN_AREA));
		items.add(new MenuItemToolMode("Set Restore Block", Material.TNT, MinigameToolMode.RESTORE_BLOCK));
		men.addItems(items);
		men.displayMenu(player);
	}
}
