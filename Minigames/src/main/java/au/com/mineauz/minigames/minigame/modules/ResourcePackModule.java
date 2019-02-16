package au.com.mineauz.minigames.minigame.modules;

import java.util.HashMap;
import java.util.Map;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.ResourcePack;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import static au.com.mineauz.minigames.menu.MenuUtility.getBackMaterial;

/**
 * Created for the AddstarMC Project. Created by Narimm on 12/02/2019.
 */
public class ResourcePackModule extends MinigameModule {

    private BooleanFlag enabled = new BooleanFlag(false, "resourcePackEnabled");
    private StringFlag resourcePackName = new StringFlag("", "resourcePackName");
    private BooleanFlag forced = new BooleanFlag(false, "forceResourcePack");

    public ResourcePackModule(Minigame mgm) {
        super(mgm);
    }

    public static ResourcePackModule getMinigameModule(Minigame minigame) {
        return (ResourcePackModule) minigame.getModule("ResourcePack");
    }

    public boolean isEnabled() {
        return enabled.getFlag();
    }

    public void setEnabled(Boolean bool) {
        enabled.setFlag(bool);
    }

    public boolean isForced() {
        return forced.getFlag();
    }

    public void setResourcePackname(String name) {
        resourcePackName.setFlag(name);
    }

    public String getResourcePackName() {
        return resourcePackName.getFlag();
    }

    @Override
    public String getName() {
        return "ResourcePack";
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
        Map<String, Flag<?>> map = new HashMap<>();
        addConfigFlag(enabled, map);
        addConfigFlag(resourcePackName, map);
        return map;
    }

    private void addConfigFlag(Flag<?> flag, Map<String, Flag<?>> flags) {
        flags.put(flag.getName(), flag);
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
        Menu m = new Menu(3, "Teams", menu.getViewer());
        m.setPreviousPage(menu);
        m.addItem(enabled.getMenuItem("Enable Resource Pack", Material.MAP));
        MenuItem item = new MenuItemString("Resource Pack Name", Material.PAPER, new Callback<String>() {
            @Override
            public String getValue() {
                return resourcePackName.getFlag();
            }

            @Override
            public void setValue(String value) {
                resourcePackName.setFlag(value);
            }
        }) {
            @Override
            public void checkValidEntry(String entry) {
                if (entry.length() == 0) {
                    super.checkValidEntry(entry);
                    return;
                }
                ResourcePack pack = Minigames.getPlugin().getResourceManager().getResourcePack(entry);
                if (pack == null) {
                    getContainer().cancelReopenTimer();
                    getContainer().displayMenu(getContainer().getViewer());
                    getContainer().getViewer().sendMessage("No resource pack exists for the name, \"" + entry + "\".", MinigameMessageType.ERROR);
                } else {
                    super.checkValidEntry(entry);
                }
            }
        };
        m.addItem(item);
        m.addItem(forced.getMenuItem("Force Resource Pack", Material.SKELETON_SKULL));
        MenuItemPage p = new MenuItemPage("Resource Pack Options", Material.MAP, m);
        m.addItem(new MenuItemPage("Back", getBackMaterial(), menu), m.getSize() - 9);
        menu.addItem(p);
    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        return false;
    }
}
