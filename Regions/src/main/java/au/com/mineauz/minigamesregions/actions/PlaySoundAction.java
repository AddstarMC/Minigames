package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.FloatFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaySoundAction extends AbstractAction {
    
    private final StringFlag sound = new StringFlag("ENTITY_PLAYER_LEVELUP", "sound");
    private final BooleanFlag priv = new BooleanFlag(true, "private");
    private final FloatFlag vol = new FloatFlag(1f, "volume");
    private final FloatFlag pit = new FloatFlag(1f, "pitch");

    @Override
    public String getName() {
        return "PLAY_SOUND";
    }

    @Override
    public String getCategory() {
        return "World Actions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        out.put("Sound", sound.getFlag());
        out.put("Volume", vol.getFlag());
        out.put("Pitch", pit.getFlag());
        out.put("Is Private", priv.getFlag());
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public void executeRegionAction(MinigamePlayer player,
            Region region) {
        debug(player,region);
        execute(player, player.getLocation());
    }

    @Override
    public void executeNodeAction(MinigamePlayer player,
            Node node) {
        debug(player,node);
        execute(player, node.getLocation());
    }
    
    private void execute(MinigamePlayer player, Location loc){
        if(player == null || !player.isInMinigame()) return;
        if(priv.getFlag()) {
            player.getPlayer().playSound(loc,
                    getSound(sound.getFlag()),
                    vol.getFlag(),
                    pit.getFlag());
        }
        else
            player.getPlayer().getWorld().playSound(loc,
                    getSound(sound.getFlag()),
                    vol.getFlag(),
                    pit.getFlag());
    }

    @Override
    public void saveArguments(FileConfiguration config,
            String path) {
        sound.saveValue(path, config);
        priv.saveValue(path, config);
        vol.saveValue(path, config);
        pit.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
            String path) {
        sound.loadValue(path, config);
        priv.loadValue(path, config);
        vol.loadValue(path, config);
        pit.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Play Sound", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        List<String> sounds = new ArrayList<>();
        for(Sound s : Sound.values())
            sounds.add(MinigameUtils.capitalize(s.toString().replace("_", " ")));
        m.addItem(new MenuItemList("Sound", Material.NOTE_BLOCK, new Callback<String>() {
            
            @Override
            public void setValue(String value) {
                if(sounds.contains(value)) {
                    sound.setFlag(value.toUpperCase().replace(" ", "_"));
                }else{
                    player.sendMessage("Sound not available", MinigameMessageType.ERROR);
                }
            }
            
            @Override
            public String getValue() {
                Sound s=getSound(sound.getFlag());              //ENSURE CONFIG doesnt contain old enums replace if they do.
                if(!s.toString().equals(sound.getFlag())){
                    sound.setFlag(s.toString());
                }
                return MinigameUtils.capitalize(sound.getFlag().replace("_", " "));
            }
        }, sounds));
        m.addItem(priv.getMenuItem("Private Playback", Material.ENDER_PEARL));
        m.addItem(new MenuItemDecimal("Volume", Material.JUKEBOX, new Callback<Double>() {

            @Override
            public void setValue(Double value) {
                vol.setFlag(value.floatValue());
            }

            @Override
            public Double getValue() {
                return vol.getFlag().doubleValue();
            }
        }, 0.1, 1d, 0.5, null));
        m.addItem(new MenuItemDecimal("Pitch", Material.ENDER_EYE, new Callback<Double>() {

            @Override
            public void setValue(Double value) {
                pit.setFlag(value.floatValue());
            }

            @Override
            public Double getValue() {
                return pit.getFlag().doubleValue();
            }
        }, 0.05, 0.1, 0d, 2d));
        m.displayMenu(player);
        return true;
    }
    private Sound getSound(String sound){
        Sound result;
        try{
             result = Sound.valueOf(sound);
        }catch (IllegalArgumentException e){
            Minigames.getPlugin().getLogger().warning("Bad Sound Config in Minigame Config : " + sound);
            result =  Sound.ENTITY_PLAYER_BURP;
        }
        return result;
    }

}
