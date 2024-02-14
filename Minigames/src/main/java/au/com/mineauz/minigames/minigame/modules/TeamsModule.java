package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.TeamSetFlag;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamsModule extends MinigameModule {
    private final Map<TeamColor, Team> teams = new HashMap<>();
    private final TeamSetFlag teamsFlag;
    private EnumFlag<TeamColor> defaultWinner = new EnumFlag<>(TeamColor.NONE, "defaultwinner");

    public TeamsModule(@NotNull Minigame mgm, @NotNull String name) {
        super(mgm, name);
        teamsFlag = new TeamSetFlag(null, "teams", getMinigame());
        teamsFlag.setFlag(teams);
    }

    public static @Nullable TeamsModule getMinigameModule(@NotNull Minigame mgm) {
        return ((TeamsModule) mgm.getModule(MgModules.TEAMS.getName()));
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Flag<?>> getConfigFlags() {
        Map<String, Flag<?>> flags = new HashMap<>();
        flags.put(teamsFlag.getName(), teamsFlag);
        flags.put(defaultWinner.getName(), defaultWinner);
        return flags;
    }

    @Override
    public boolean useSeparateConfig() {
        return false;
    }

    @Override
    public void save(@NotNull FileConfiguration config) {
    }

    @Override
    public void load(@NotNull FileConfiguration config) {
        if (config.contains(getMinigame() + ".startposred") || config.contains(getMinigame() + ".startposblue")) {
            Minigames.getPlugin().getLogger().warning(config.getCurrentPath() + " contains unsupported configurations: " + getMinigame() + ".startpos*");
        }
    }

    public @Nullable Team getTeam(@NotNull TeamColor color) {
        return teams.get(color);
    }

    public @NotNull List<@NotNull Team> getTeams() {
        return new ArrayList<>(teams.values());
    }

    public @NotNull Map<@NotNull String, @NotNull Team> getTeamsNameMap() {
        ImmutableMap.Builder<String, Team> builder = ImmutableMap.builder();

        for (Team team : teams.values()) {
            builder.put(team.getColor().toString().toLowerCase(), team);
        }

        return builder.build();
    }

    /**
     * Adds or returns the existing team of TeamColor
     *
     * @param color {@link TeamColor}
     * @return {@link Team}
     */
    public @NotNull Team addTeam(@NotNull TeamColor color) {
        return addTeam(color, (String) null);
    }

    /**
     * Adds a new team with the color unless one already exists in which case this returns the
     * existing team
     *
     * @param color {@link TeamColor}
     * @param name  Team name
     * @return Team
     */
    public @NotNull Team addTeam(@NotNull TeamColor color, @Nullable String name) {
        if (!hasTeam(color)) {
            teams.put(color, new Team(color, getMinigame()));
            String teamNameString = color.toString().toLowerCase();
            org.bukkit.scoreboard.@NotNull Team bukkitTeam = getMinigame().getScoreboardManager().registerNewTeam(teamNameString);
            bukkitTeam.setAllowFriendlyFire(false);
            bukkitTeam.setCanSeeFriendlyInvisibles(true);
            bukkitTeam.color(color.getColor());
            if (name != null && !name.isEmpty()) {
                teams.get(color).setDisplayName(name);
                bukkitTeam.setDisplayName(name);
            }
        }
        return teams.get(color);
    }

    /**
     * Adds a team with the new color -and removes any other team with that color name from the scoreboard
     *
     * @param color {@link TeamColor}  the TeamColor to set
     * @param team  The new Team
     */
    public void addTeam(@NotNull TeamColor color, @NotNull Team team) {
        teams.put(color, team);
        String sbTeam = color.toString().toLowerCase();
        Scoreboard scoreboard = getMinigame().getScoreboardManager();
        org.bukkit.scoreboard.Team bukkitTeam = scoreboard.getTeam(sbTeam);
        if (bukkitTeam != null) {
            bukkitTeam.unregister();
        }
        bukkitTeam = getMinigame().getScoreboardManager().registerNewTeam(sbTeam);
        bukkitTeam.setAllowFriendlyFire(false);
        bukkitTeam.setCanSeeFriendlyInvisibles(true);
        bukkitTeam.setDisplayName(team.getDisplayName());
        bukkitTeam.color(color.getColor());
    }

    /**
     * True of {@link TeamColor} exists as a team
     *
     * @param color {@link TeamColor}
     * @return boolean
     */
    public boolean hasTeam(@NotNull TeamColor color) {
        return teams.containsKey(color);
    }

    /**
     * Removes a team from the module and the scoreboard
     *
     * @param color {@link TeamColor}
     */
    public void removeTeam(@NotNull TeamColor color) {
        if (hasTeam(color)) {
            teams.remove(color);
            org.bukkit.scoreboard.Team bukkitTeam =
                    getMinigame().getScoreboardManager().getTeam(color.toString().toLowerCase());
            if (bukkitTeam != null) bukkitTeam.unregister();
        }
    }

    public boolean hasTeamStartLocations() {
        for (Team t : teams.values()) {
            if (!t.hasStartLocations())
                return false;
        }
        return true;
    }

    public Callback<TeamColor> getDefaultWinnerCallback() {
        return new Callback<>() {

            @Override
            public TeamColor getValue() {
                if (defaultWinner.getFlag() != null) {
                    if (!teams.containsKey(defaultWinner.getFlag())) {
                        return TeamColor.NONE;
                    }

                    return defaultWinner.getFlag();
                }
                return TeamColor.NONE;
            }

            @Override
            public void setValue(TeamColor value) {
                defaultWinner.setFlag(value);
            }
        };
    }

    public @Nullable TeamColor getDefaultWinner() {
        if (defaultWinner.getFlag() != null) {
            TeamColor team = defaultWinner.getFlag();
            if (!teams.containsKey(team)) {
                return null;
            } else {
                return team;
            }
        }
        return null;
    }

    public void setDefaultWinner(@NotNull TeamColor defaultWinner) {
        this.defaultWinner.setFlag(defaultWinner);
    }

    public void clearTeams() {
        teams.clear();
        defaultWinner = null;
    }

    @Override
    public void addEditMenuOptions(Menu previousMenu) {
        Menu menu = new Menu(6, MgMenuLangKey.MENU_TEAM_NAME, previousMenu.getViewer());
        menu.setPreviousPage(previousMenu);
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItemList<>(Material.PAPER, MgMenuLangKey.MENU_TEAM_DEFAULTWIN_NAME,
                getDefaultWinnerCallback(), List.of(TeamColor.values())));
        menuItems.add(new MenuItemNewLine());
        for (Team team : this.teams.values()) {
            menuItems.add(new MenuItemTeam(team.getColoredDisplayName(), team));
        }

        menu.addItem(new MenuItemAddTeam(MgMenuLangKey.MENU_TEAMADD_NAME, this), menu.getSize() - 1);

        menu.addItems(menuItems);

        menu.addItem(new MenuItemBack(previousMenu), menu.getSize() - 9);

        MenuItemPage teamOptionsMenuPage = new MenuItemPage(Material.CHEST, MgMenuLangKey.MENU_TEAM_OPTIONS_NAME, menu);
        previousMenu.addItem(teamOptionsMenuPage);
    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        return false;
    }
}
