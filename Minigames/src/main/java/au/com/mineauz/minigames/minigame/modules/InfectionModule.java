package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.minigame.Minigame;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfectionModule extends MinigameModule {

    private IntegerFlag infectedPercent = new IntegerFlag(18, "infectedPercent");
    private StringFlag infectedTeam = new StringFlag(null, "infectedTeam");
    private StringFlag survivorTeam = new StringFlag(null, "survivorTeam");

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
        flags.put(infectedPercent.getName(), infectedPercent);
        flags.put(infectedTeam.getName(), infectedTeam);
        flags.put(survivorTeam.getName(), survivorTeam);
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

    public Callback<String> getInfectedTeamCallback() {
        return new Callback<String>() {
            @Override
            public String getValue() {
                if (infectedTeam.getFlag() != null) {
                    if (!TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(infectedTeam.getFlag())) {
                        return "None";
                    }

                    return WordUtils.capitalize(infectedTeam.getFlag().replace("_", " "));
                }
                return "None";
            }

            @Override
            public void setValue(String value) {
                if (!value.equals("None"))
                    infectedTeam.setFlag(TeamColor.matchColor(value.replace(" ", "_")).toString());
                else
                    infectedTeam.setFlag(null);
            }
        };
    }

    public Callback<String> getSurvivorTeamCallback() {
        return new Callback<String>() {
            @Override
            public String getValue() {
                if (survivorTeam.getFlag() != null) {
                    if (!TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(survivorTeam.getFlag())) {
                        return "None";
                    }

                    return WordUtils.capitalize(survivorTeam.getFlag().replace("_", " "));
                }
                return "None";
            }

            @Override
            public void setValue(String value) {
                if (!value.equals("None"))
                    survivorTeam.setFlag(TeamColor.matchColor(value.replace(" ", "_")).toString());
                else
                    survivorTeam.setFlag(null);
            }
        };
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

        List<String> teams = new ArrayList<>(TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().size() + 1);
        for (String t : TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().keySet()) {
            teams.add(WordUtils.capitalize(t.replace("_", " ")));
        }
        teams.add("None");

        m.addItem(new MenuItemList("Infected Team", Material.PAPER, getInfectedTeamCallback(), teams));
        m.addItem(new MenuItemList("Survivor Team", Material.PAPER, getSurvivorTeamCallback(), teams));
        m.displayMenu(previous.getViewer());
        return true;
    }

    public int getInfectedPercent() {
        return infectedPercent.getFlag();
    }

    public void setInfectedPercent(int amount) {
        infectedPercent.setFlag(amount);
    }

    public TeamColor getInfectedTeam() {
        if (infectedTeam.getFlag() != null) {
            if (!TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(infectedTeam.getFlag())) {
                return null;
            } else {
                return TeamColor.matchColor(infectedTeam.getFlag());
            }
        }
        return null;
    }

    public void setInfectedTeam(TeamColor infectedTeam) {
        this.infectedTeam.setFlag(infectedTeam.toString());
    }

    public TeamColor getSurvivorTeam() {
        if (survivorTeam.getFlag() != null) {
            TeamColor team = TeamColor.matchColor(survivorTeam.getFlag());
            if (!TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(survivorTeam.getFlag())) {
                return null;
            } else {
                return TeamColor.matchColor(survivorTeam.getFlag());
            }
        }
        return null;
    }

    public void setSurvivorTeam(TeamColor survivorTeam) {
        this.survivorTeam.setFlag(survivorTeam.toString());
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
