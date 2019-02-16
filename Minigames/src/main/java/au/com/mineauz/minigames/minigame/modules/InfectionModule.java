package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfectionModule extends MinigameModule {

    private IntegerFlag infectedPercent = new IntegerFlag(18, "infectedPercent");

    //Unsaved Data
    private List<MinigamePlayer> infected = new ArrayList<>();

    public InfectionModule(Minigame mgm) {
        super(mgm);
    }

    public static InfectionModule getMinigameModule(Minigame mgm) {
        return (InfectionModule) mgm.getModule("Infection");
    }

    @Override
    public String getName() {
        return "Infection";
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
        Map<String, Flag<?>> flags = new HashMap<>();
        flags.put("infectedPercent", infectedPercent);
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
        Menu m = new Menu(6, "Infection Settings", previous.getViewer());
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);

        m.addItem(infectedPercent.getMenuItem("Infected Percent", Material.ZOMBIE_HEAD,
                MinigameUtils.stringToList("The percentage of players;chosen to start as;infected"), 1, 99));
        m.displayMenu(previous.getViewer());
        return true;
    }

    public int getInfectedPercent() {
        return infectedPercent.getFlag();
    }

    public void setInfectedPercent(int amount) {
        infectedPercent.setFlag(amount);
    }

    public void addInfectedPlayer(MinigamePlayer ply) {
        infected.add(ply);
    }

    public void removeInfectedPlayer(MinigamePlayer ply) {
        infected.remove(ply);
    }

    public boolean isInfectedPlayer(MinigamePlayer ply) {
        return infected.contains(ply);
    }

    public void clearInfectedPlayers() {
        infected.clear();
    }
}
