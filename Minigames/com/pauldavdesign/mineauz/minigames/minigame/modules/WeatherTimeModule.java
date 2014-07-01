package com.pauldavdesign.mineauz.minigames.minigame.modules;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.config.BooleanFlag;
import com.pauldavdesign.mineauz.minigames.config.EnumFlag;
import com.pauldavdesign.mineauz.minigames.config.Flag;
import com.pauldavdesign.mineauz.minigames.config.LongFlag;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemInteger;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemList;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;

public class WeatherTimeModule extends MinigameModule {
	
	private LongFlag time = new LongFlag(0L, "customTime.value");
	private BooleanFlag useCustomTime = new BooleanFlag(false, "customTime.enabled");
	private BooleanFlag useCustomWeather = new BooleanFlag(false, "customWeather.enabled");
	private EnumFlag<WeatherType> weather = new EnumFlag<WeatherType>(WeatherType.CLEAR, "customWeather.type");
	private int task = -1;
	
	public WeatherTimeModule(Minigame mgm){
		super(mgm);
	}

	@Override
	public String getName() {
		return "WeatherTime";
	}
	
	@Override
	public Map<String, Flag<?>> getFlags(){
		Map<String, Flag<?>> map = new HashMap<String, Flag<?>>();
		map.put(time.getName(), time);
		map.put(useCustomTime.getName(), useCustomTime);
		map.put(useCustomWeather.getName(), useCustomWeather);
		map.put(weather.getName(), weather);
		return map;
	}
	
	@Override
	public boolean useSeparateConfig(){
		return false;
	}

	@Override
	public void save(FileConfiguration config) {
	}

	@Override
	public void load(FileConfiguration config) {
	}

	@Override
	public void addMenuOptions(Menu menu) {
		Menu m = new Menu(6, "Time and Weather", menu.getViewer());
		m.addItem(new MenuItemBoolean("Use Custom Time", Material.WATCH, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				useCustomTime.setFlag(value);
			}

			@Override
			public Boolean getValue() {
				return useCustomTime.getFlag();
			}
		}));
		m.addItem(new MenuItemInteger("Time of Day", Material.WATCH, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				time.setFlag(value.longValue());
			}
			
			@Override
			public Integer getValue() {
				return time.getFlag().intValue();
			}
		}, 0, 24000));
		m.addItem(new MenuItemBoolean("Use Custom Weather", Material.WATER_BUCKET, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				useCustomWeather.setFlag(value);
			}

			@Override
			public Boolean getValue() {
				return useCustomWeather.getFlag();
			}
		}));
		m.addItem(new MenuItemList("Weather Type", Material.WATER_BUCKET, new Callback<String>() {

			@Override
			public void setValue(String value) {
				weather.setFlag(WeatherType.valueOf(value.toUpperCase()));
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(weather.getFlag().toString());
			}
		}, MinigameUtils.stringToList("Clear;Downfall")));
		
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, menu), m.getSize() - 9);
		
		menu.addItem(new MenuItemPage("Time and Weather Settings", Material.CHEST, m));
	}

	@Override
	public boolean getMenuOptions(Menu previous) {
		return false;
	}
	
	public static WeatherTimeModule getMinigameModule(Minigame minigame){
		return (WeatherTimeModule) minigame.getModule("WeatherTime");
	}
	
	public long getTime(){
		return time.getFlag();
	}
	
	public void setTime(long time){
		this.time.setFlag(time);
	}
	
	public boolean isUsingCustomTime(){
		return useCustomTime.getFlag();
	}
	
	public void setUseCustomTime(boolean bool){
		useCustomTime.setFlag(bool);
	}
	
	public void applyCustomTime(MinigamePlayer player){
		if(isUsingCustomTime()){
			player.getPlayer().setPlayerTime(time.getFlag(), false);
		}
	}
	
	public boolean isUsingCustomWeather(){
		return useCustomWeather.getFlag();
	}
	
	public void setUsingCustomWeather(boolean bool){
		useCustomWeather.setFlag(bool);
	}
	
	public WeatherType getCustomWeather(){
		return weather.getFlag();
	}
	
	public void setCustomWeather(WeatherType type){
		weather.setFlag(type);
	}
	
	public void applyCustomWeather(MinigamePlayer player){
		if(isUsingCustomWeather())
			player.getPlayer().setPlayerWeather(weather.getFlag());
	}
	
	public void startTimeLoop(){
		final Minigame fmgm = getMinigame();
		if(task == -1 && isUsingCustomTime()){
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.plugin, new Runnable() {
				
				@Override
				public void run() {
					for(MinigamePlayer player : fmgm.getPlayers()){
						player.getPlayer().setPlayerTime(time.getFlag(), false);
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
