package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.config.*;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 16/08/2017.
 */
public class RespawnModule extends MinigameModule {

    private LongFlag timer = new LongFlag(0L, "respawn.timer");

    public Location getRespawnLocation() {
        return respawnLocation.getFlag();
    }

    public void setRespawnLocation(Location respawnLocation) {
        this.respawnLocation.setFlag(respawnLocation);
    }

    private SimpleLocationFlag respawnLocation = new SimpleLocationFlag(null, "respawn.location");
    private BooleanFlag canMoveRespawnWait = new BooleanFlag(true, "respawn.canMoveRespawnWait");
    private BooleanFlag canInteractRespawnWait = new BooleanFlag(true, "respawn.canInteractRespawnWait");

    public RespawnModule(Minigame mgm) {
        super(mgm);
    }

    public static RespawnModule getMinigameModule(Minigame minigame) {
        return (RespawnModule) minigame.getModule("Respawn");
    }

    @Override
    public String getName() {
        return "Respawn";
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
        Map<String, Flag<?>> map = new HashMap<String, Flag<?>>();
        addConfigFlag(timer, map);
        addConfigFlag(respawnLocation, map);
        addConfigFlag(canMoveRespawnWait, map);
        addConfigFlag(canInteractRespawnWait, map);
        return map;
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
        Menu m = new Menu(6, "Respawn", menu.getViewer());
        if (!menu.getViewer().getMinigame().getRespawn()){
            m.addItem(new MenuItem("Repawn in main menu set to false",Material.BARRIER));

        }else {
            m.addItem(new MenuItemInteger("Respawn Time", Material.WATCH, new Callback<Integer>() {

                @Override
                public void setValue(Integer value) {
                    timer.setFlag(value.longValue());
                }


                @Override
                public Integer getValue() {
                    return timer.getFlag().intValue();
                }
            }, 0, 7200));
            m.addItem(new MenuItemBoolean("Interact while in Respawn ", Material.REDSTONE_LAMP_ON, new Callback<Boolean>() {

                @Override
                public void setValue(Boolean value) {
                    canInteractRespawnWait.setFlag(value);
                }

                @Override
                public Boolean getValue() {
                    return canInteractRespawnWait.getFlag();
                }
            }));

            m.addItem(new MenuItemBoolean("Move while in Respawn ", Material.CAULDRON_ITEM, new Callback<Boolean>() {

                @Override
                public Boolean getValue() {
                    return canMoveRespawnWait.getFlag();
                }

                @Override
                public void setValue(Boolean value) {
                    canMoveRespawnWait.setFlag(value);
                }
            }));
        }
        m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, menu), m.getSize() - 9);
        menu.addItem(new MenuItemPage("Respawn Settings", Material.EYE_OF_ENDER, m));
    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        return false;
    }

    private void addConfigFlag(Flag<?> flag, Map<String, Flag<?>> flags) {
        flags.put(flag.getName(), flag);
    }
}
