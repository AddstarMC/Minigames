package au.com.mineauz.minigames.managers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.MinigameSave;
import au.com.mineauz.minigames.objects.ResourcePack;


/**
 * Created for the AddstarMC Project. Created by Narimm on 12/02/2019.
 */
public class ResourcePackManager {

    final static Path resourceDir = Paths.get(Minigames.getPlugin().getDataFolder().toString(), "resources");
    private boolean enabled = true;
    private Map<String, ResourcePack> resources = new HashMap<>();
    private MinigameSave config;

    public ResourcePackManager() {
        if (!Files.notExists(resourceDir))
            try {
                Path path = Files.createDirectories(resourceDir);
                if (Files.notExists(path)) {
                    Minigames.log().severe("Cannot create a resource directory to house resources " +
                            "- they will be unavailable");
                    enabled = false;
                } else {
                    if (Files.exists(path))
                        enabled = true;
                    else {
                        enabled = false;
                        Minigames.log().severe("Cannot create a resource directory to house resources " +
                                "- they will be unavailable.");
                    }
                }

            } catch (IOException e) {
                Minigames.log().severe("Cannot create a resource directory to house resources " +
                        "- they will be unavailable: Message" + e.getMessage());
                enabled = false;
            }
    }

    public static Path getResourceDir() {
        return resourceDir;
    }

    private boolean loadEmptyPack() {
        try {
            URL u = new URL("https://github.com/AddstarMC/Minigames/raw/master/Minigames/src/main/resources/resourcepack/emptyResourcePack.zip");
            ResourcePack empty = new ResourcePack("empty", u);
            addResourcePack(empty);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public ResourcePack getResourcePack(String name) {
        if (!enabled) return null;
        ResourcePack pack = resources.get(name);
        if (pack != null && pack.isValid()) return pack;
        else return null;
    }

    public ResourcePack addResourcePack(ResourcePack pack) {
        if (!enabled) return null;
        return resources.put(pack.getName(), pack);
    }

    public void removeResourcePack(ResourcePack pack) {
        if (!enabled) return;
        resources.remove(pack.getName());
        saveResources();
    }

    public boolean initialize(final MinigameSave c) {
        this.config = c;
        boolean emptyPresent = false;
        final List<ResourcePack> resources = new ArrayList<>();
        final Object objects = this.config.getConfig().get("resources");
        if (objects instanceof List) {
            final List obj = (List) objects;
            for (final Object object : obj) {
                if (object instanceof ResourcePack) {
                    resources.add((ResourcePack) object);
                }
            }
        }
        for (final ResourcePack pack : resources) {
            if (pack.getName().equals("empty")) {
                emptyPresent = true;
                enabled = true;
            }
            addResourcePack(pack);
        }
        if (!emptyPresent) {
            if (!loadEmptyPack()) {
                Minigames.log().warning("Minigames Resource Manager could not create the empty reset pack");
                enabled = false;
                return false;
            }
            enabled = true;
        }
        enabled = true;
        return true;
    }

    public void saveResources() {
        List<ResourcePack> resourceList = new ArrayList<>(resources.values());
        config.getConfig().set("resources", resourceList);
        config.saveConfig();
    }

    public Set<String> getResourceNames() {
        return resources.keySet();
    }


}
