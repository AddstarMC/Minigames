package com.pauldavdesign.mineauz.minigames.minigame.modules;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemInteger;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemList;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;

public class WeatherTimeModule implements MinigameModule {
	
	private long time = 0;
	private boolean useCustomTime = false;
	private boolean useCustomWeather = false;
	private WeatherType weather = WeatherType.CLEAR;
	private int task = -1;

	@Override
	public String getName() {
		return "WeatherTime";
	}
	
	@Override
	public boolean useSeparateConfig(){
		return false;
	}

	@Override
	public void save(Minigame minigame, FileConfiguration config) {
		if(useCustomTime){
			config.set(minigame + ".customTime.enabled", true);
			if(time != 0)
				config.set(minigame + ".customTime.value", time);
		}
		if(useCustomWeather){
			config.set(minigame + ".customWeather.enabled", true);
			if(weather != WeatherType.CLEAR)
				config.set(minigame + ".customWeather.type", weather.toString());
		}
	}

	@Override
	public void load(Minigame minigame, FileConfiguration config) {
		if(config.contains(minigame + ".customTime.enabled")){
			useCustomTime = config.getBoolean(minigame + ".customTime.enabled");
			if(config.contains(minigame + ".customTime.value"))
				time = config.getLong(minigame + ".customTime.value");
		}
		if(config.contains(minigame + ".customWeather.enabled")){
			useCustomWeather = config.getBoolean(minigame + ".customWeather.enabled");
			if(config.contains(minigame + ".customWeather.type"))
				weather = WeatherType.valueOf(config.getString(minigame + ".customWeather.type"));
		}
	}

	@Override
	public void addMenuOptions(Menu menu) {
		Menu m = new Menu(6, "Time and Weather", menu.getViewer());
		m.addItem(new MenuItemBoolean("Use Custom Time", Material.WATCH, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				useCustomTime = value;
			}

			@Override
			public Boolean getValue() {
				return useCustomTime;
			}
		}));
		m.addItem(new MenuItemInteger("Time of Day", Material.WATCH, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				time = value;
			}
			
			@Override
			public Integer getValue() {
				return (int)time;
			}
		}, 0, 24000));
		m.addItem(new MenuItemBoolean("Use Custom Weather", Material.WATER_BUCKET, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				useCustomWeather = value;
			}

			@Override
			public Boolean getValue() {
				return useCustomWeather;
			}
		}));
		m.addItem(new MenuItemList("Weather Type", Material.WATER_BUCKET, new Callback<String>() {

			@Override
			public void setValue(String value) {
				weather = WeatherType.valueOf(value.toUpperCase());
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(weather.toString());
			}
		}, MinigameUtils.stringToList("Clear;Downfall")));
		
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, menu), m.getSize() - 9);
		
		menu.addItem(new MenuItemPage("Time and Weather Settings", Material.CHEST, m));
	}
	
	public static WeatherTimeModule getMinigameModule(Minigame minigame){
		return (WeatherTimeModule) minigame.getModule("WeatherTime");
	}
	
	public long getTime(){
		return time;
	}
	
	public void setTime(long time){
		this.time = time;
	}
	
	public boolean isUsingCustomTime(){
		return useCustomTime;
	}
	
	public void setUseCustomTime(boolean bool){
		useCustomTime = bool;
	}
	
	public void applyCustomTime(MinigamePlayer player){
		if(isUsingCustomTime()){
			player.getPlayer().setPlayerTime(time, false);
		}
	}
	
	public boolean isUsingCustomWeather(){
		return useCustomWeather;
	}
	
	public void setUsingCustomWeather(boolean bool){
		useCustomWeather = bool;
	}
	
	public WeatherType getCustomWeather(){
		return weather;
	}
	
	public void setCustomWeather(WeatherType type){
		weather = type;
	}
	
	public void applyCustomWeather(MinigamePlayer player){
		if(isUsingCustomWeather())
			player.getPlayer().setPlayerWeather(weather);
	}
	
	public void startTimeLoop(Minigame mgm){
		final Minigame fmgm = mgm;
		if(task == -1 && isUsingCustomTime()){
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.plugin, new Runnable() {
				
				@Override
				public void run() {
					for(MinigamePlayer player : fmgm.getPlayers()){
						player.getPlayer().setPlayerTime(time, false);
					}
				}
			}, 20 * 5, 20 * 5);
		}
	}
	
	public void stopTimeLoop(){
		if(task == -1) return;
		Bukkit.getScheduler().cancelTask(task);
		task = -1;
	}
}
