package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.objects.MinigamePlayer;
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
    private final EnumFlag<TeamColor> infectedTeam = new EnumFlag<>(TeamColor.RED, "infectedTeam");
    private final EnumFlag<TeamColor> survivorTeam = new EnumFlag<>(TeamColor.BLUE, "survivorTeam");

    //Unsaved Data
    private final List<MinigamePlayer> infected = new ArrayList<>();

    public InfectionModule(@NotNull Minigame mgm, @NotNull String name) {
        super(mgm, name);
    }

    public static @Nullable InfectionModule getMinigameModule(@NotNull Minigame mgm) {
        return ((InfectionModule) mgm.getModule(MgModules.INFECTION.getName()));
    }

    @Override
    public Map<String, Flag<?>> getConfigFlags() {
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

    public Callback<TeamColor> getInfectedTeamCallback() {
        return new Callback<>() {
            @Override
            public TeamColor getValue() {
                if (infectedTeam.getFlag() != null) {
                    if (infectedTeam.getFlag().equals(TeamColor.NONE)) {
                        return infectedTeam.getFlag();
                    } else if (infectedTeam.getFlag() == infectedTeam.getDefaultFlag() || infectedTeam.getFlag() == survivorTeam.getDefaultFlag() ||
                            TeamsModule.getMinigameModule(getMinigame()).getTeamColors().contains(infectedTeam.getFlag())) {
                        return infectedTeam.getFlag();
                    } else {
                        return infectedTeam.getDefaultFlag();
                    }
                } else {
                    return infectedTeam.getDefaultFlag();
                }
            }

            @Override
            public void setValue(TeamColor value) {
                if (value == TeamColor.NONE) {
                    infectedTeam.setFlag(value);
                } else if (value == infectedTeam.getDefaultFlag() || value == survivorTeam.getDefaultFlag() ||
                        TeamsModule.getMinigameModule(getMinigame()).getTeamColors().contains(value)) {
                    infectedTeam.setFlag(value);
                } else {
                    infectedTeam.setFlag(null);
                }
            }
        };
    }

    public Callback<TeamColor> getSurvivorTeamCallback() {
        return new Callback<>() {
            @Override
            public TeamColor getValue() {
                if (survivorTeam.getFlag() != null) {
                    if (survivorTeam.getFlag() == TeamColor.NONE) {
                        return survivorTeam.getFlag();
                    } else if (survivorTeam.getFlag() == infectedTeam.getDefaultFlag() || survivorTeam.getFlag() == survivorTeam.getDefaultFlag() ||
                            TeamsModule.getMinigameModule(getMinigame()).getTeamColors().contains(survivorTeam.getFlag())) {
                        return survivorTeam.getFlag();
                    } else {
                        return survivorTeam.getDefaultFlag();
                    }
                } else {
                    return survivorTeam.getDefaultFlag();
                }
            }

            @Override
            public void setValue(TeamColor value) {
                if (value == TeamColor.NONE) {
                    survivorTeam.setFlag(TeamColor.NONE);
                } else if (value == infectedTeam.getDefaultFlag() || value == survivorTeam.getDefaultFlag() ||
                        TeamsModule.getMinigameModule(getMinigame()).getTeamColors().contains(value)) {
                    survivorTeam.setFlag(value);
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
        Menu menu = new Menu(6, MgMenuLangKey.MENU_INFECTED_NAME, previous.getViewer());
        menu.addItem(new MenuItemBack(previous), menu.getSize() - 9);

        menu.addItem(infectedPercent.getMenuItem(Material.ZOMBIE_HEAD, MgMenuLangKey.MENU_INFECTED_PERCENT_NAME,
                MgMenuLangKey.MENU_INFECTED_PERCENT_DESCRIPTION, 1, 99));

        TeamsModule teamsModule = TeamsModule.getMinigameModule(getMinigame());
        List<TeamColor> teams = new ArrayList<>(teamsModule.getTeamColors().size() + 3);
        for (TeamColor teamColor : teamsModule.getTeamColors()) {
            if (teamColor != infectedTeam.getDefaultFlag() && teamColor != survivorTeam.getDefaultFlag()) {
                teams.add(teamColor);
            } // avoid adding defaults twice
        }
        // add defaults
        teams.add(infectedTeam.getDefaultFlag());
        teams.add(survivorTeam.getDefaultFlag());
        teams.add(TeamColor.NONE);
        menu.addItem(new MenuItemList<>(Material.PAPER, MgMenuLangKey.MENU_INFECTED_TEAM_INFECTED_NAME, getInfectedTeamCallback(), teams));
        menu.addItem(new MenuItemList<>(Material.PAPER, MgMenuLangKey.MENU_INFECTED_TEAM_SURVIVOR_NAME, getSurvivorTeamCallback(), teams));
        menu.displayMenu(previous.getViewer());
        return true;
    }

    public int getInfectedPercent() {
        return infectedPercent.getFlag();
    }

    public void setInfectedPercent(int amount) {
        infectedPercent.setFlag(amount);
    }

    public @NotNull TeamColor getInfectedTeam() {
        if (infectedTeam.getFlag() != null && infectedTeam.getFlag() != TeamColor.NONE) {
            TeamColor teamColor = infectedTeam.getFlag();
            if (teamColor == infectedTeam.getDefaultFlag() || teamColor == survivorTeam.getDefaultFlag() ||
                    TeamsModule.getMinigameModule(getMinigame()).getTeamColors().contains(teamColor)) {
                return teamColor;
            } else {
                return TeamColor.NONE;
            }
        } else {
            return TeamColor.NONE;
        }
    }

    public void setInfectedTeam(@NotNull TeamColor teamColor) {
        if (teamColor == infectedTeam.getDefaultFlag() || teamColor == survivorTeam.getDefaultFlag() ||
                TeamsModule.getMinigameModule(getMinigame()).getTeamColors().contains(teamColor)) {
            this.infectedTeam.setFlag(teamColor);
        } else
            this.infectedTeam.setFlag(TeamColor.NONE);
    }

    public TeamColor getDefaultInfectedTeam() {
        return infectedTeam.getDefaultFlag();
    }

    public @Nullable TeamColor getSurvivorTeam() {
        if (survivorTeam.getFlag() != null) {
            TeamColor teamColor = survivorTeam.getFlag();
            if (teamColor == infectedTeam.getDefaultFlag() || teamColor == survivorTeam.getDefaultFlag() ||
                    TeamsModule.getMinigameModule(getMinigame()).getTeamColors().contains(teamColor)) {
                return teamColor;
            } else
                return null;
        } else
            return null;
    }

    public boolean setSurvivorTeam(@NotNull TeamColor survivorTeamColor) {
        TeamsModule teamsModule = TeamsModule.getMinigameModule(getMinigame());

        if (survivorTeamColor == TeamColor.NONE ||
                survivorTeamColor == infectedTeam.getDefaultFlag() || survivorTeamColor == survivorTeam.getDefaultFlag() ||
                (teamsModule != null && teamsModule.getTeamColors().contains(survivorTeamColor))) {
            this.survivorTeam.setFlag(survivorTeamColor);

            return true;
        } else {
            this.survivorTeam.setFlag(null);
            return false;
        }
    }

    public @NotNull TeamColor getDefaultSurvivorTeam() {
        return survivorTeam.getDefaultFlag();
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
