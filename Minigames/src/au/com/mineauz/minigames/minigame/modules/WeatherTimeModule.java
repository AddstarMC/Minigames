package au.com.mineauz.minigames.minigame.modules;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WeatherType;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemEnum;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.EnumProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;

public class WeatherTimeModule extends MinigameModule {
	
	private final ConfigPropertyContainer properties;
	private final IntegerProperty time = new IntegerProperty(0, "customTime.value");
	private final BooleanProperty useCustomTime = new BooleanProperty(false, "customTime.enabled");
	private final BooleanProperty useCustomWeather = new BooleanProperty(false, "customWeather.enabled");
	private final EnumProperty<WeatherType> weather = new EnumProperty<WeatherType>(WeatherType.CLEAR, "customWeather.type");
	private int task = -1;
	
	public WeatherTimeModule(Minigame mgm){
		super(mgm);
		
		properties = new ConfigPropertyContainer();
		properties.addProperty(time);
		properties.addProperty(useCustomTime);
		properties.addProperty(useCustomWeather);
		properties.addProperty(weather);
	}

	@Override
	public String getName() {
		return "WeatherTime";
	}
	
	@Override
	public ConfigPropertyContainer getProperties() {
		return properties;
	}
	
	@Override
	public boolean useSeparateConfig(){
		return false;
	}

	@Override
	public void addEditMenuOptions(Menu menu) {
		Menu m = new Menu(6, "Time and Weather");
		m.addItem(new MenuItemBoolean("Use Custom Time", Material.WATCH, useCustomTime));
		m.addItem(new MenuItemInteger("Time of Day", Material.WATCH, time, 0, 24000));
		m.addItem(new MenuItemBoolean("Use Custom Weather", Material.WATER_BUCKET, useCustomWeather));
		m.addItem(new MenuItemEnum<WeatherType>("Weather Type", Material.WATER_BUCKET, weather, WeatherType.class));
		
		menu.addItem(new MenuItemSubMenu("Time and Weather Settings", Material.CHEST, m));
	}

	@Deprecated
	public static WeatherTimeModule getMinigameModule(Minigame minigame){
		return (WeatherTimeModule) minigame.getModule(WeatherTimeModule.class);
	}
	
	public int getTime() {
		return time.getValue();
	}
	
	public void setTime(int time) {
		this.time.setValue(time);
	}
	
	public boolean isUsingCustomTime(){
		return useCustomTime.getValue();
	}
	
	public void setUseCustomTime(boolean bool){
		useCustomTime.setValue(bool);
	}
	
	public void applyCustomTime(MinigamePlayer player){
		if(isUsingCustomTime()){
			player.getPlayer().setPlayerTime(time.getValue(), false);
		}
	}
	
	public boolean isUsingCustomWeather(){
		return useCustomWeather.getValue();
	}
	
	public void setUsingCustomWeather(boolean bool){
		useCustomWeather.setValue(bool);
	}
	
	public WeatherType getCustomWeather(){
		return weather.getValue();
	}
	
	public void setCustomWeather(WeatherType type){
		weather.setValue(type);
	}
	
	public void applyCustomWeather(MinigamePlayer player){
		if(isUsingCustomWeather())
			player.getPlayer().setPlayerWeather(weather.getValue());
	}
	
	public void startTimeLoop(){
		final Minigame fmgm = getMinigame();
		if(task == -1 && isUsingCustomTime()){
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.plugin, new Runnable() {
				
				@Override
				public void run() {
					for(MinigamePlayer player : fmgm.getPlayers()){
						player.getPlayer().setPlayerTime(time.getValue(), false);
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
	
	@Override
	public void applySettings(MinigamePlayer player) {
		applyCustomTime(player);
		applyCustomWeather(player);
		
		if (task == -1) {
			startTimeLoop();
		}
	}
}
