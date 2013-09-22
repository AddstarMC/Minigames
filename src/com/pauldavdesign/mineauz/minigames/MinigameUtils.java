package com.pauldavdesign.mineauz.minigames;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
//import net.minecraft.server.v1_6_R2.EntityPlayer;
//
//import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

public class MinigameUtils {
	private static FileConfiguration lang = Minigames.plugin.getLang();
	
	/**
	 * Returns the item stack from a number or name.
	 * @param item - The items name or ID.
	 * @param quantity - The number of said item
	 * @return The ItemStack referred to in the parameter.
	 */
	public static ItemStack stringToItemStack(String item, int quantity){
		String itemName = "";
		int itemInt = 0;
		short itemData = 0;
		String[] split = null;
		
		if(item.matches("[0-9]+(:[0-9]+)?")){
			if(item.contains(":")){
				split = item.split(":");
				itemInt = Integer.parseInt(split[0]);
				itemData = Short.parseShort(split[1]);
			}
			else{
				itemInt = Integer.parseInt(item);
			}
		}
		else{
			if(item.contains(":")){
				split = item.split(":");
				itemName = split[0].toUpperCase();
				if(split[1].matches("[0-9]+")){
					itemData = Short.parseShort(split[1]);
				}
			}
			else{
				itemName = item.toUpperCase();
			}
		}
		
		ItemStack it = null;
		
		if(Material.getMaterial(itemName) != null){
			it = new ItemStack(Material.getMaterial(itemName), quantity, itemData);
		}
		else if(Material.getMaterial(itemInt) != null){
			it = new ItemStack(Material.getMaterial(itemInt), quantity, itemData);
		}
		return it;
	}
	
	/**
	 * Gets the name of an ItemStack
	 * @param item - The ItemStack to get the name of
	 * @return The name of the item
	 */
	public static String getItemStackName(ItemStack item){
		return item.getType().toString().toLowerCase().replace("_", " ");
	}
	
	/**
	 * Checks for an update at a web address where the version and changelog is stored.
	 * @param webAddress - The address where the update info is stored.
	 * @param version - The version the plugin is currently on.
	 * @return A list containing the version and changes if there is an update available, null if not.
	 */
	public static List<String> checkForUpdate(String webAddress, String version){
		URL update;
		try {
			update = new URL(webAddress);
			BufferedReader read = new BufferedReader(new InputStreamReader(update.openStream()));
			
			List<String> list = new ArrayList<String>();
			while(read.ready()){
				list.add(read.readLine());
			}
			
			if(!list.isEmpty()){
				if(!version.equals(list.get(0))){
					return list;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Converts seconds into weeks, days, hours, minutes and seconds to be neatly
	 * displayed. Only shows the values that aren't 0.
	 * @param time - The time in seconds to be converted
	 * @return A message with a neat time
	 */
	public static String convertTime(int time){
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		int rtime = time;
		String msg = "";
		
		if(time > 604800){
			weeks = rtime / 604800;
			rtime = rtime - weeks * 604800;
			days = rtime / 86400;
			rtime = rtime - days * 86400;
			hours = rtime / 3600;
			rtime = rtime - hours * 3600;
			minutes = rtime / 60;
			rtime = rtime - minutes * 60;
			seconds = rtime;
		}
		else if(time > 86400){
			days = rtime / 86400;
			rtime = rtime - days * 86400;
			hours = rtime / 3600;
			rtime = rtime - hours * 3600;
			minutes = rtime / 60;
			rtime = rtime - minutes * 60;
			seconds = rtime;
		}
		else if(time > 3600){
			hours = rtime / 3600;
			rtime = rtime - hours * 3600;
			minutes = rtime / 60;
			seconds = rtime - minutes * 60;
		}
		else if(time > 60){
			minutes = time / 60;
			seconds = rtime - minutes * 60;
		}
		else{
			seconds = time;
		}
		
		if(weeks != 0){
			msg = String.format(lang.getString("time.weeks"), weeks);
		}
		if(days != 0){
			if(!msg.equals("")){
				if(seconds != 0 || hours != 0 || minutes != 0){
					msg += ", ";
				}
				else{
					msg += " " + lang.getString("time.and") + " ";
				}
			}
			msg += String.format(lang.getString("time.days"), days);
		}
		if(hours != 0){
			if(!msg.equals("")){
				if(seconds != 0 || minutes != 0){
					msg += ", ";
				}
				else{
					msg += " " + lang.getString("time.and") + " ";
				}
			}
			msg += String.format(lang.getString("time.hours"), hours);
		}
		if(minutes != 0){
			if(!msg.equals("")){
				if(seconds != 0){
					msg += ", ";
				}
				else{
					msg += " " + lang.getString("time.and") + " ";
				}
			}
			msg += String.format(lang.getString("time.minutes"), minutes);
		}
		if(seconds != 0){
			if(!msg.equals("")){
				msg += " " + lang.getString("time.and") + " ";
			}
			msg += String.format(lang.getString("time.seconds"), seconds);
		}
		
		return msg;
	}
	
	/**
	 * Creates a string ID to compare locations.
	 * @param location - The location to give an ID to.
	 * @return The ID
	 */
	public static String createLocationID(Location location){
		return location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + location.getWorld().getName();
	}
	
	public static String listToString(List<String> list){
		String slist = "";
		boolean switchColour = false;
		for(String entry : list){
			if(switchColour){
				slist += ChatColor.WHITE + entry;
				if(!entry.equalsIgnoreCase(list.get(list.size() - 1))){
					slist += ChatColor.WHITE + ", ";
				}
				switchColour = false;
			}
			else{
				slist += ChatColor.GRAY + entry;
				if(!entry.equalsIgnoreCase(list.get(list.size() - 1))){
					slist += ChatColor.WHITE + ", ";
				}
				switchColour = true;
			}
		}
		return slist;
	}
	
	/**
	 * Formats a string from the language file.
	 * @param format - The location in the YAML of the string to format.
	 * @param text - What to replace the formatted variables with.
	 * @return The formatted string
	 */
	public static String formStr(String format, Object... text){
		FileConfiguration lang = Minigames.plugin.getLang();
		return String.format(lang.getString(format), text);
	}
	
//	public static void removePlayerArrows(MinigamePlayer player){
//		try{
//			Class.forName("net.minecraft.server.v1_5_R3.EntityPlayer");
//			EntityPlayer eply = ((CraftPlayer) player.getPlayer()).getHandle();
//			eply.re(0);
//		}catch(ClassNotFoundException e){}
//	}
}
