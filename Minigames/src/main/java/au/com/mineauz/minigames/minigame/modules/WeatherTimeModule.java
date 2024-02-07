package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.TimeFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WeatherTimeModule extends MinigameModule {
    private final TimeFlag time = new TimeFlag(0L, "customTime.value");
    private final BooleanFlag useCustomTime = new BooleanFlag(false, "customTime.enabled");
    private final BooleanFlag useCustomWeather = new BooleanFlag(false, "customWeather.enabled");
    private final EnumFlag<WeatherType> weather = new EnumFlag<>(WeatherType.CLEAR, "customWeather.type");
    private int task = -1;

    public WeatherTimeModule(@NotNull Minigame mgm, @NotNull String name) {
        super(mgm, name);
    }

    public static WeatherTimeModule getMinigameModule(Minigame minigame) {
        return (WeatherTimeModule) minigame.getModule(MgModules.WEATHER_TIME.getName());
    }

    @Override
    public boolean useSeparateConfig() {
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

        m.addItem(useCustomTime.getMenuItem("Use Custom Time", Material.CLOCK));
        m.addItem(time.getMenuItem("Time of Day", Material.CLOCK, 0L, 24000L));
        m.addItem(useCustomWeather.getMenuItem("Use Custom Weather", Material.WATER_BUCKET));
        m.addItem(weather.getMenuItem("Weather Type", Material.WATER_BUCKET));
        m.addItem(new MenuItemBack(menu), m.getSize() - 9);

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

    public long getTime() {
        return time.getFlag();
    }

    public void setTime(long time) {
        this.time.setFlag(time);
    }

    public boolean isUsingCustomTime() {
        return useCustomTime.getFlag();
    }

    public void setUseCustomTime(boolean bool) {
        useCustomTime.setFlag(bool);
    }

    public void applyCustomTime(MinigamePlayer player) {
        if (isUsingCustomTime()) {
            player.getPlayer().setPlayerTime(time.getFlag(), false);
        }
    }

    public boolean isUsingCustomWeather() {
        return useCustomWeather.getFlag();
    }

    public void setUsingCustomWeather(boolean bool) {
        useCustomWeather.setFlag(bool);
    }

    public WeatherType getCustomWeather() {
        return weather.getFlag();
    }

    public void setCustomWeather(WeatherType type) {
        weather.setFlag(type);
    }

    public void applyCustomWeather(MinigamePlayer player) {
        if (isUsingCustomWeather())
            player.getPlayer().setPlayerWeather(weather.getFlag());
    }

    public void startTimeLoop() {
        final Minigame fmgm = getMinigame();
        if (task == -1 && isUsingCustomTime()) {
            task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.getPlugin(), () -> {
                for (MinigamePlayer player : fmgm.getPlayers()) {
                    player.getPlayer().setPlayerTime(time.getFlag(), false);
                }
            }, 20 * 5, 20 * 5);
        }
    }

    public void stopTimeLoop() {
        if (task == -1) return;
        Bukkit.getScheduler().cancelTask(task);
        task = -1;
    }
}
