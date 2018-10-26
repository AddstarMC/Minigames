package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.LongFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WeatherTimeModule extends MinigameModule {
	
	private LongFlag time = new LongFlag(0L, "customTime.value");
	private BooleanFlag useCustomTime = new BooleanFlag(false, "customTime.enabled");
	private BooleanFlag useCustomWeather = new BooleanFlag(false, "customWeather.enabled");
    private EnumFlag<WeatherType> weather = new EnumFlag<>(WeatherType.CLEAR, "customWeather.type");
	private int task = -1;
	
	public WeatherTimeModule(Minigame mgm){
		super(mgm);
	}

	@Override
	public String getName() {
		return "WeatherTime";
	}

    /**
     * @param minigame The game
     * @return WeatherTimeModule
     */
    @Nullable
    public static WeatherTimeModule getMinigameModule(Minigame minigame) {
        return (WeatherTimeModule) minigame.getModule("WeatherTime");
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
	public void addEditMenuOptions(Menu menu) {
		Menu m = new Menu(6, "Time and Weather", menu.getViewer());
		m.addItem(new MenuItemBoolean("Use Custom Time", Material.CLOCK, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				useCustomTime.setFlag(value);
			}

			@Override
			public Boolean getValue() {
				return useCustomTime.getFlag();
			}
		}));
		m.addItem(new MenuItemInteger("Time of Day", Material.CLOCK, new Callback<Integer>() {
			
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
		
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH, menu), m.getSize() - 9);
		
		menu.addItem(new MenuItemPage("Time and Weather Settings", Material.CHEST, m));
	}

	@Override
	public boolean displayMechanicSettings(Menu previous) {
		return false;
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
        Map<String, Flag<?>> map = new HashMap<>();
        map.put(time.getName(), time);
        map.put(useCustomTime.getName(), useCustomTime);
        map.put(useCustomWeather.getName(), useCustomWeather);
        map.put(weather.getName(), weather);
        return map;
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
            task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.getPlugin(), new Runnable() {
				
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
