package au.com.mineauz.minigames.managers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.object.ResourcePack;
import org.bukkit.configuration.ConfigurationSection;


/**
 * Created for the AddstarMC Project. Created by Narimm on 12/02/2019.
 */
public class ResourcePackManager {

    private boolean enabled = true;

    public static Path getResourceDir() {
        return resourceDir;
    }

    final static Path resourceDir = Paths.get(Minigames.getPlugin().getDataFolder().toPath()+"resources");

    private Map<String, ResourcePack> resources = new HashMap<>();

    public ResourcePackManager() {
        if(!Files.notExists(resourceDir))
            try {
                Path path = Files.createDirectories(resourceDir);
                if(Files.notExists(path)){
                    Minigames.log().severe("Cannot create a resource directory to house resources " +
                            "- they will be unavailable");
                    enabled = false;
                } else {
                    enabled = true;
                }

            }catch (IOException e){
                Minigames.log().severe("Cannot create a resource directory to house resources " +
                        "- they will be unavailable: Message" + e.getMessage() );
                enabled = false;
            }
    }

    public ResourcePack getResourcePack(String name){
        ResourcePack pack = resources.get(name);
        if(pack != null && pack.isValid()) return pack;
        else return null;
    }
    
    public ResourcePack addResourcePack(ResourcePack pack){
        return resources.put(pack.getName(),pack);
    };
    
    public boolean initialize(ConfigurationSection config){
        Set<String> keys =  config.getKeys(false);
        for(String key:keys){
            ConfigurationSection section = config.getConfigurationSection(key);
            String  url  = section.getString("url");
            try {
                URL u = new URL(url);
                File local = new File(resourceDir.toFile(),key+".resourcepack");
                ResourcePack pack = new ResourcePack(key,u,local);
                addResourcePack(pack);
            }catch (MalformedURLException e){
                Minigames.log().warning("Minigames Resource:" + key + " could not load see following error");
                Minigames.log().warning(e.getMessage());
                continue;
            }
        }
        return true;
    }
}
