package au.com.mineauz.minigames;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class MinigameSave {
    private FileConfiguration minigameSave = null;
    private File minigameSaveFile = null;
    String minigame = null;
    private static Minigames plugin = Minigames.getPlugin();
    private String name;
    
    public MinigameSave(String name){
        this.name = name;
        reloadFile();
        saveConfig();
    }
    
    public MinigameSave(String minigame, String name){
        this.minigame = minigame;
        this.name = name;
        reloadFile();
        saveConfig();
    }
    
    public void reloadFile(){
        if(minigame != null){
            if(minigameSaveFile == null){
                minigameSaveFile = new File(plugin.getDataFolder() + "/minigames/" + minigame + "/", name + ".yml");
            }
            minigameSave = YamlConfiguration.loadConfiguration(minigameSaveFile);
        }
        else{
            if(minigameSaveFile == null){
                minigameSaveFile = new File(plugin.getDataFolder() + "/", name + ".yml");
            }
            minigameSave = YamlConfiguration.loadConfiguration(minigameSaveFile);
        }
    }
    
    public FileConfiguration getConfig(){
        if(minigameSave == null){
            reloadFile();
        }
        return minigameSave;
    }
    
    public void saveConfig(){
        if(minigameSave == null || minigameSaveFile == null){
            return;
        }
        try{
            minigameSave.save(minigameSaveFile);
        }
        catch(IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + minigame + " config file!");
        }
    }
    
    public void deleteFile(){
        if(minigameSave == null){
            reloadFile();
        }
        File delfile = new File(minigameSaveFile.getPath());
        delfile.delete();
        minigameSaveFile = null;
    }
}
