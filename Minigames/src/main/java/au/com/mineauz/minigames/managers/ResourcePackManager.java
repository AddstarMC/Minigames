package au.com.mineauz.minigames.managers;

import java.util.HashMap;
import java.util.Map;

import au.com.mineauz.minigames.object.ResourcePack;

/**
 * Created for the AddstarMC Project. Created by Narimm on 12/02/2019.
 */
public class ResourcePackManager {
    private Map<String, ResourcePack> resources = new HashMap<>();
    
    public ResourcePack getResourcePack(String name){
        return resources.get(name);
    }
    
    public ResourcePack addResourcePack(ResourcePack pack){
        return resources.put(pack.getName(),pack);
    };
    
    
}
