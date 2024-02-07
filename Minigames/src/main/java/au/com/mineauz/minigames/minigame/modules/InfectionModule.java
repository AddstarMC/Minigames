package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfectionModule extends MinigameModule {
    private final IntegerFlag infectedPercent = new IntegerFlag(18, "infectedPercent");
    private final StringFlag infectedTeam = new StringFlag("red", "infectedTeam");
    private final StringFlag survivorTeam = new StringFlag("blue", "survivorTeam");

    //Unsaved Data
    private final List<MinigamePlayer> infected = new ArrayList<>();

    public InfectionModule(@NotNull Minigame mgm, @NotNull String name) {
        super(mgm, name);
    }

    public static @Nullable InfectionModule getMinigameModule(@NotNull Minigame mgm) {
        return ((InfectionModule) mgm.getModule(MgModules.INFECTION.getName()));
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
        return new Callback<>() {
            @Override
            public String getValue() {
                if (infectedTeam.getFlag() != null) {
                    if (infectedTeam.getFlag().equalsIgnoreCase(TeamColor.NONE.toString())) {
                        return infectedTeam.getFlag();
                    } else if (TeamColor.matchColor(infectedTeam.getFlag()) == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                            TeamColor.matchColor(infectedTeam.getFlag()) == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                            TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(infectedTeam.getFlag().toLowerCase())) {
                        return WordUtils.capitalize(infectedTeam.getFlag().toLowerCase().replace("_", " "));
                    } else {
                        return WordUtils.capitalize(infectedTeam.getDefaultFlag().toLowerCase().replace("_", " "));
                    }
                } else {
                    return WordUtils.capitalize(infectedTeam.getDefaultFlag().toLowerCase().replace("_", " "));
                }
            }

            @Override
            public void setValue(String value) {
                if (value.equalsIgnoreCase("None")) {
                    infectedTeam.setFlag("None");
                } else if (TeamColor.matchColor(value) == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                        TeamColor.matchColor(value) == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                        TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(value.toLowerCase())) {
                    infectedTeam.setFlag(TeamColor.matchColor(value.replace(" ", "_")).toString());
                } else {
                    infectedTeam.setFlag(null);
                }
            }
        };
    }

    public Callback<String> getSurvivorTeamCallback() {
        return new Callback<>() {
            @Override
            public String getValue() {
                if (survivorTeam.getFlag() != null) {
                    if (survivorTeam.getFlag().equalsIgnoreCase("None")) {
                        return survivorTeam.getFlag();
                    } else if (TeamColor.matchColor(survivorTeam.getFlag()) == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                            TeamColor.matchColor(survivorTeam.getFlag()) == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                            TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(survivorTeam.getFlag().toLowerCase())) {
                        return WordUtils.capitalize(survivorTeam.getFlag().toLowerCase().replace("_", " "));
                    } else {
                        return WordUtils.capitalize(survivorTeam.getDefaultFlag().toLowerCase().replace("_", " "));
                    }
                } else {
                    return WordUtils.capitalize(survivorTeam.getDefaultFlag().toLowerCase().replace("_", " "));
                }
            }

            @Override
            public void setValue(String value) {
                if (value.equalsIgnoreCase("None")) {
                    survivorTeam.setFlag("None");
                } else if (TeamColor.matchColor(value) == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                        TeamColor.matchColor(value) == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                        TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(value.toLowerCase())) {
                    survivorTeam.setFlag(TeamColor.matchColor(value.replace(" ", "_")).toString());
                } else {
                    survivorTeam.setFlag(null);
                }
            }
        };
    }

    @Override
    public void addEditMenuOptions(Menu menu) {
    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        Menu m = new Menu(6, "Infection Settings", previous.getViewer());
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);

        m.addItem(infectedPercent.getMenuItem("Infected Percent", Material.ZOMBIE_HEAD,
                List.of("The percentage of players", "chosen to start as", "infected"), 1, 99));

        TeamsModule teamsModule = TeamsModule.getMinigameModule(getMinigame());
        List<String> teams = new ArrayList<>(teamsModule.getTeamsNameMap().size() + 3);
        for (String t : teamsModule.getTeamsNameMap().keySet()) {
            if (!t.equalsIgnoreCase(infectedTeam.getDefaultFlag()) || t.equalsIgnoreCase(survivorTeam.getDefaultFlag())) {
                teams.add(WordUtils.capitalize(t.replace("_", " ")));
            } // avoid adding defaults twice
        }
        // add defaults
        teams.add(WordUtils.capitalize(infectedTeam.getDefaultFlag().toLowerCase().replace("_", " ")));
        teams.add(WordUtils.capitalize(survivorTeam.getDefaultFlag().toLowerCase().replace("_", " ")));
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

    public @NotNull TeamColor getInfectedTeam() {
        if (infectedTeam.getFlag() != null && !infectedTeam.getFlag().equalsIgnoreCase(TeamColor.NONE.toString())) {
            TeamColor team = TeamColor.matchColor(infectedTeam.getFlag());
            if (team == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                    team == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                    TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(team.toString().toLowerCase())) {
                return team;
            } else {
                return TeamColor.NONE;
            }
        } else {
            return TeamColor.NONE;
        }
    }

    public void setInfectedTeam(@NotNull TeamColor teamColor) {
        if (teamColor == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                teamColor == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                TeamsModule.getMinigameModule(getMinigame()).getTeamsNameMap().containsKey(teamColor.toString().toLowerCase())) {
            this.infectedTeam.setFlag(teamColor.toString());
        } else
            this.infectedTeam.setFlag(TeamColor.NONE.toString());
    }

    public TeamColor getDefaultInfectedTeam() {
        return TeamColor.matchColor(infectedTeam.getDefaultFlag());
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

    public boolean setSurvivorTeam(@NotNull TeamColor sTeam) {
        TeamsModule teamsModule = TeamsModule.getMinigameModule(getMinigame());

        if (sTeam == TeamColor.NONE ||
                sTeam == TeamColor.matchColor(infectedTeam.getDefaultFlag()) ||
                sTeam == TeamColor.matchColor(survivorTeam.getDefaultFlag()) ||
                (teamsModule != null && teamsModule.getTeamsNameMap().containsKey(sTeam.toString().toLowerCase()))) {
            this.survivorTeam.setFlag(sTeam.toString());

            return true;
        } else {
            this.survivorTeam.setFlag(null);
            return false;
        }
    }

    public TeamColor getDefaultSurvivorTeam() {
        return TeamColor.matchColor(survivorTeam.getDefaultFlag());
    }

    public void addInfectedPlayer(@NotNull MinigamePlayer mgPlayer) {
        infected.add(mgPlayer);
    }

    public void removeInfectedPlayer(@NotNull MinigamePlayer mgPlayer) {
        infected.remove(mgPlayer);
    }

    public boolean isInfectedPlayer(@Nullable MinigamePlayer mgPlayer) {
        return infected.contains(mgPlayer);
    }

    public void clearInfectedPlayers() {
        infected.clear();
    }
}
