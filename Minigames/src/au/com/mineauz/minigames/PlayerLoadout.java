package au.com.mineauz.minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.minigame.TeamColor;

public class PlayerLoadout {
	private Map<Integer, ItemStack> itemSlot = new HashMap<Integer, ItemStack>();
	private List<PotionEffect> potions = new ArrayList<PotionEffect>();
	private String loadoutName = "default";
	private boolean usePermission = false;
	private boolean fallDamage = true;
	private boolean hunger = false;
	private int level = -1;
	private boolean deleteable = true;
	private String displayname = null;
	private boolean lockInventory = false;
	private boolean lockArmour = false;
	private TeamColor team = null;
	private boolean displayInMenu = true;
	
	public PlayerLoadout(String name){
		loadoutName = name;
		for(TeamColor col : TeamColor.values()){
			if(name.toUpperCase().equals(col.toString())){
				team = col;
				break;
			}
		}
	}
	
	public void setDisplayName(String name){
		displayname = name;
	}
	
	public Callback<String> getDisplayNameCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				displayname = value;
			}

			@Override
			public String getValue() {
				return displayname;
			}
		};
	}
	
	public String getDisplayName(){
		return displayname;
	}
	
	public void setUsePermissions(boolean bool){
		usePermission = bool;
	}
	
	public boolean getUsePermissions(){
		return usePermission;
	}
	
	public Callback<Boolean> getUsePermissionsCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				usePermission = value;
			}

			@Override
			public Boolean getValue() {
				return usePermission;
			}
		};
	}
	
	public String getName(boolean useDisplay){
		if(!useDisplay || getDisplayName() == null)
			return loadoutName;
		return getDisplayName();
	}
	
	public void addItem(ItemStack item, int slot){
		itemSlot.put(slot, item);
	}
	
	public void addPotionEffect(PotionEffect effect){
		for(PotionEffect pot : potions){
			if(effect.getType().getName().equals(pot.getType().getName())){
				potions.remove(pot);
				break;
			}
		}
		potions.add(effect);
	}
	
	public void removePotionEffect(PotionEffect effect){
		if(potions.contains(effect)){
			potions.remove(effect);
		}
		else{
			for(PotionEffect pot : potions){
				if(pot.getType().getName().equals(effect.getType().getName())){
					potions.remove(pot);
					break;
				}
			}
		}
	}
	
	public List<PotionEffect> getAllPotionEffects(){
		return potions;
	}
	
	public void equiptLoadout(MinigamePlayer player){
		player.getPlayer().getInventory().clear();
		player.getPlayer().getInventory().setHelmet(null);
		player.getPlayer().getInventory().setChestplate(null);
		player.getPlayer().getInventory().setLeggings(null);
		player.getPlayer().getInventory().setBoots(null);
		for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
			player.getPlayer().removePotionEffect(potion.getType());
		}
		if(!itemSlot.isEmpty()){
			for(Integer slot : itemSlot.keySet()){
				if(slot < 100)
					player.getPlayer().getInventory().setItem(slot, getItem(slot));
				else if(slot == 100)
					player.getPlayer().getInventory().setBoots(getItem(slot));
				else if(slot == 101)
					player.getPlayer().getInventory().setLeggings(getItem(slot));
				else if(slot == 102)
					player.getPlayer().getInventory().setChestplate(getItem(slot));
				else if(slot == 103)
					player.getPlayer().getInventory().setHelmet(getItem(slot));
			}
			player.updateInventory();
		}
		
		final MinigamePlayer fplayer = player;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
			
			@Override
			public void run() {
				fplayer.getPlayer().addPotionEffects(potions);
			}
		});
		
		if(level != -1)
			player.getPlayer().setLevel(level);
	}
	
	public Set<Integer> getItems(){
		return itemSlot.keySet();
	}
	
	public ItemStack getItem(int slot){
		return itemSlot.get(slot);
	}
	
	public void clearLoadout(){
		itemSlot.clear();
	}
	
	public boolean hasFallDamage(){
		return fallDamage;
	}
	
	public void setHasFallDamage(boolean bool){
		fallDamage = bool;
	}
	
	public Callback<Boolean> getFallDamageCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fallDamage = value;
			}

			@Override
			public Boolean getValue() {
				return fallDamage;
			}
		};
	}
	
	public boolean hasHunger(){
		return hunger;
	}
	
	public void setHasHunger(boolean bool){
		hunger = bool;
	}
	
	public Callback<Boolean> getHungerCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				hunger = value;
			}

			@Override
			public Boolean getValue() {
				return hunger;
			}
		};
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public Callback<Integer> getLevelCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				if(level >= -1)
					level = value;
			}

			@Override
			public Integer getValue() {
				return level;
			}
		};
	}
	
	public boolean isDeleteable(){
		return deleteable;
	}
	
	public void setDeleteable(boolean value){
		deleteable = value;
	}
	
	public boolean isInventoryLocked(){
		return lockInventory;
	}
	
	public Callback<Boolean> getInventoryLockedCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				setInventoryLocked(value);
			}

			@Override
			public Boolean getValue() {
				return isInventoryLocked();
			}
		};
	}
	
	public void setInventoryLocked(boolean locked){
		lockInventory = locked;
	}
	
	public boolean isArmourLocked(){
		return lockArmour;
	}
	
	public Callback<Boolean> getArmourLockedCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				setArmourLocked(value);
			}

			@Override
			public Boolean getValue() {
				return isArmourLocked();
			}
		};
	}
	
	public void setArmourLocked(boolean locked){
		lockArmour = locked;
	}
	
	public TeamColor getTeamColor(){
		return team;
	}
	
	public Callback<String> getTeamColorCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				setTeamColor(TeamColor.matchColor(value.toUpperCase()));
			}

			@Override
			public String getValue() {
				if(getTeamColor() == null)
					return "None";
				return MinigameUtils.capitalize(getTeamColor().toString());
			}
		};
	}
	
	public void setTeamColor(TeamColor color){
		team = color;
	}
	
	public boolean isDisplayedInMenu(){
		return displayInMenu;
	}
	
	public Callback<Boolean> getDisplayInMenuCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				setDisplayInMenu(value);
			}

			@Override
			public Boolean getValue() {
				return isDisplayedInMenu();
			}
		};
	}
	
	public void setDisplayInMenu(boolean bool){
		displayInMenu = bool;
	}
}
