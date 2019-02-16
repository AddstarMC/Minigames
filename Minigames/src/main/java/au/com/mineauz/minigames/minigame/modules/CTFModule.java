package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 19/01/2017.
 */
public class CTFModule extends MinigameModule {

    private BooleanFlag useFlagAsCapturePoint = new BooleanFlag(true, "useFlagAsCapturePoint");

    public CTFModule(Minigame mgm) {
        super(mgm);
    }

    public static CTFModule getMinigameModule(Minigame mgm) {
        return (CTFModule) mgm.getModule("CTF");
    }

    public Boolean getUseFlagAsCapturePoint() {
        return useFlagAsCapturePoint.getFlag();
    }

    public void setUseFlagAsCapturePoint(Boolean useFlagAsCapturePoint) {
        this.useFlagAsCapturePoint.setFlag(useFlagAsCapturePoint);
    }

    @Override
    public String getName() {
        return "CTF";
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
        Map<String, Flag<?>> flags = new HashMap<>();
        flags.put("useFlagAsCapturePoint", useFlagAsCapturePoint);
        return flags;
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

    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        Menu m = new Menu(6, "CTF Settings", previous.getViewer());
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);

        m.addItem(useFlagAsCapturePoint.getMenuItem("CTF Flag is Capture Point", Material.BLACK_BANNER,
                MinigameUtils.stringToList("Use a teams Flag as a capture point")));
        m.displayMenu(previous.getViewer());
        return true;
    }
}

