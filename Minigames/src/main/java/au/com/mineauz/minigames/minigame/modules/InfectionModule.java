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
    private StringFlag infectedTeam = new StringFlag("red", "infectedTeam");
    private StringFlag survivorTeam = new StringFlag("blue", "survivorTeam");

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
                    if (TeamColor.matchColor(infectedTeam.getFlag()) == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                            TeamColor.matchColor(infectedTeam.getFlag()) == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                            TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(infectedTeam.getFlag().toLowerCase())) {
                        return WordUtils.capitalize(infectedTeam.getFlag().replace("_", " "));
                    } else
                        return WordUtils.capitalize(infectedTeam.getDefaultFlag().toLowerCase().replace("_", " "));
                } else
                    return WordUtils.capitalize(infectedTeam.getDefaultFlag().toLowerCase().replace("_", " "));
            }

            @Override
            public void setValue(String value) {
                if (TeamColor.matchColor(value) == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                        TeamColor.matchColor(value) == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                        TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(value.toLowerCase())) {
                    infectedTeam.setFlag(TeamColor.matchColor(value.replace(" ", "_")).toString());
                } else
                    infectedTeam.setFlag(null);
            }
        };
    }

    public Callback<String> getSurvivorTeamCallback() {
        return new Callback<String>() {
            @Override
            public String getValue() {
                if (survivorTeam.getFlag() != null) {
                    if (TeamColor.matchColor(survivorTeam.getFlag()) == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                            TeamColor.matchColor(survivorTeam.getFlag()) == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                            TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(survivorTeam.getFlag().toLowerCase())) {
                        return WordUtils.capitalize(survivorTeam.getFlag().replace("_", " "));
                    } else
                        return WordUtils.capitalize(survivorTeam.getDefaultFlag().toLowerCase().replace("_", " "));
                } else
                    return WordUtils.capitalize(survivorTeam.getDefaultFlag().toLowerCase().replace("_", " "));
            }

            @Override
            public void setValue(String value) {
                if (TeamColor.matchColor(value) == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                        TeamColor.matchColor(value) == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                        TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(value.toLowerCase())) {
                    survivorTeam.setFlag(TeamColor.matchColor(value.replace(" ", "_")).toString());
                } else
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
            if (t.equalsIgnoreCase(infectedTeam.getDefaultFlag()) || t.equalsIgnoreCase(survivorTeam.getDefaultFlag())) {
                // avoiding adding defaults twice
            } else {
                teams.add(WordUtils.capitalize(t.replace("_", " ")));
            }
        }
        // add defaults
        teams.add(infectedTeam.getDefaultFlag());
        teams.add(survivorTeam.getDefaultFlag());
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
            TeamColor team = TeamColor.matchColor(infectedTeam.getFlag());
            if (team == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                    team == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                    TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(team.toString().toLowerCase())) {
                return team;
            } else
                return null;
        } else
            return null;
    }

    public void setInfectedTeam(TeamColor iTeam) {
        if (iTeam != null) {
            if (iTeam == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                    iTeam == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                    TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(iTeam.toString().toLowerCase())) {
                this.infectedTeam.setFlag(iTeam.toString());
            } else
                this.infectedTeam.setFlag(null);
        } else
            this.infectedTeam.setFlag(null);
    }

    public TeamColor getSurvivorTeam() {
        if (survivorTeam.getFlag() != null) {
            TeamColor team = TeamColor.matchColor(survivorTeam.getFlag());
            if (team == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                    team == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                    TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(team.toString().toLowerCase())) {
                return team;
            } else
                return null;
        } else
            return null;
    }

    public void setSurvivorTeam(TeamColor sTeam) {
        if (sTeam != null) {
            if (sTeam == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                    sTeam == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                    TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(sTeam.toString().toLowerCase()))
                this.survivorTeam.setFlag(sTeam.toString());
            else
                this.survivorTeam.setFlag(null);
        } else
            this.survivorTeam.setFlag(null);
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
