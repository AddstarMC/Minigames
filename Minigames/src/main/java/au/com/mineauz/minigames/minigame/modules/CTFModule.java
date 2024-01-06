package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CTFModule extends MinigameModule {
    private final BooleanFlag useFlagAsCapturePoint = new BooleanFlag(true, "useFlagAsCapturePoint");
    private final BooleanFlag bringFlagBackManual = new BooleanFlag(false, "bringFlagBackManual");

    public CTFModule(@NotNull Minigame mgm, String name) {
        super(mgm, name);
    }

    public static CTFModule getMinigameModule(Minigame mgm) {
        return ((CTFModule) mgm.getModule(MgModules.INFECTION.getName()));
    }

    public Boolean getUseFlagAsCapturePoint() {
        return useFlagAsCapturePoint.getFlag();
    }

    public void setUseFlagAsCapturePoint(Boolean useFlagAsCapturePoint) {
        this.useFlagAsCapturePoint.setFlag(useFlagAsCapturePoint);
    }

    public Boolean getBringFlagBackManual() {
        return bringFlagBackManual.getFlag();
    }

    public void setBringFlagBackManual(Boolean bringFlagBackManual) {
        this.bringFlagBackManual.setFlag(bringFlagBackManual);
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
        Map<String, Flag<?>> flags = new HashMap<>();
        flags.put("useFlagAsCapturePoint", useFlagAsCapturePoint);
        flags.put("bringFlagBackManual", bringFlagBackManual);
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
                List.of("Use a teams Flag as a capture point")));
        m.addItem(bringFlagBackManual.getMenuItem("Bring Flag Back Manually", Material.ENDER_EYE,
                List.of("If enabled, the flag can be brought", "back to the base manually")));
        m.displayMenu(previous.getViewer());
        return true;
    }
}

