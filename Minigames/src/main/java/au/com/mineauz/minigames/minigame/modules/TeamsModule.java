package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.config.TeamSetFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static au.com.mineauz.minigames.menu.MenuUtility.getBackMaterial;

public class TeamsModule extends MinigameModule {
    private Map<TeamColor, Team> teams = new HashMap<>();
    private TeamSetFlag teamsFlag;
    private StringFlag defaultWinner = new StringFlag(null, "defaultwinner");

    public TeamsModule(Minigame mgm) {
        super(mgm);
        teamsFlag = new TeamSetFlag(null, "teams", getMinigame());
        teamsFlag.setFlag(teams);
    }

    public static TeamsModule getMinigameModule(Minigame minigame) {
        return (TeamsModule) minigame.getModule("Teams");
    }

    @Override
    public String getName() {
        return "Teams";
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
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
    public void save(FileConfiguration config) {
    }

    @Override
    public void load(FileConfiguration config) {
        if (config.contains(getMinigame() + ".startposred") || config.contains(getMinigame() + ".startposblue")) {
            Minigames.getPlugin().getLogger().warning(config.getCurrentPath() + " contains unsupported configurations: " + getMinigame() + ".startpos*");
        }
    }

    public Team getTeam(TeamColor color) {
        return teams.get(color);
    }

    public List<Team> getTeams() {
        return new ArrayList<>(teams.values());
    }

    public Map<String, Team> getTeamsNameMap() {
        ImmutableMap.Builder<String, Team> builder = ImmutableMap.builder();

        for (Team team : teams.values()) {
            builder.put(team.getColor().name().toLowerCase(), team);
        }

        return builder.build();
    }

  /**
   *  Adds or returns the existing team of TeamColor
   * @param color  {@link TeamColor}
   * @return {@link Team}
   */
    public Team addTeam(TeamColor color) {
        return addTeam(color, "");
    }

  /**
   * Adds a new team with the color unless one already exists in which case this returns the
   * existing team
   * @param color {@link TeamColor}
   * @param name Team name
   * @return Team
   */
    public Team addTeam(TeamColor color, String name) {
        if (!hasTeam(color)) {
            teams.put(color, new Team(color, getMinigame()));
            String teamNameString = color.toString().toLowerCase();
            org.bukkit.scoreboard.@NotNull Team bukkitTeam = getMinigame().getScoreboardManager().registerNewTeam(teamNameString);
            bukkitTeam.setAllowFriendlyFire(false);
            bukkitTeam.setCanSeeFriendlyInvisibles(true);
            bukkitTeam.setColor(color.getColor());
            if (name != null && !name.equals("")) {
                teams.get(color).setDisplayName(name);
                bukkitTeam.setDisplayName(name);
            }
        }
      return teams.get(color);
    }

  /**
   * Adds a team with the new color -and removes any other team with that color name from the scoreboard
   * @param color  {@link TeamColor}  the TeamColor to set
   * @param team The new Team
   */
    public void addTeam(TeamColor color, Team team) {
        teams.put(color, team);
        String sbTeam = color.toString().toLowerCase();
        Scoreboard scoreboard = getMinigame().getScoreboardManager();
        org.bukkit.scoreboard.Team bukkitTeam = scoreboard.getTeam(sbTeam);
        if(bukkitTeam != null) {
            bukkitTeam.unregister();
        }
        bukkitTeam = getMinigame().getScoreboardManager().registerNewTeam(sbTeam);
        bukkitTeam.setAllowFriendlyFire(false);
        bukkitTeam.setCanSeeFriendlyInvisibles(true);
        bukkitTeam.setDisplayName(team.getDisplayName());
        bukkitTeam.setColor(color.getColor());
    }

  /**
   * True of {@link TeamColor} exists as a team
   * @param color {@link TeamColor}
   * @return boolean
   */
    public boolean hasTeam(TeamColor color) {
        return teams.containsKey(color);
    }

  /**
   * Removes a team from the module and the scoreboard
   * @param color {@link TeamColor}
   */
    public void removeTeam(TeamColor color) {
        if (hasTeam(color)) {
            teams.remove(color);
          org.bukkit.scoreboard.Team bukkitTeam =
              getMinigame().getScoreboardManager().getTeam(color.toString().toLowerCase());
          if(bukkitTeam != null)bukkitTeam.unregister();
        }
    }

    public boolean hasTeamStartLocations() {
        for (Team t : teams.values()) {
            if (!t.hasStartLocations())
                return false;
        }
        return true;
    }

    public Callback<String> getDefaultWinnerCallback() {
        return new Callback<String>() {

            @Override
            public String getValue() {
                if (defaultWinner.getFlag() != null) {
                    if (!teams.containsKey(TeamColor.matchColor(defaultWinner.getFlag()))) {
                        return "None";
                    }

                    return MinigameUtils.capitalize(defaultWinner.getFlag().replace("_", " "));
                }
                return "None";
            }

            @Override
            public void setValue(String value) {
                if (!value.equals("None"))
                    defaultWinner.setFlag(TeamColor.matchColor(value.replace(" ", "_")).toString());
                else
                    defaultWinner.setFlag(null);
            }
        };
    }

    public TeamColor getDefaultWinner() {
        if (defaultWinner.getFlag() != null) {
            TeamColor team = TeamColor.matchColor(defaultWinner.getFlag());
            if (!teams.containsKey(team)) {
                return null;
            } else {
                return team;
            }
        }
        return null;
    }

    public void setDefaultWinner(TeamColor defaultWinner) {
        this.defaultWinner.setFlag(defaultWinner.toString());
    }

    public void clearTeams() {
        teams.clear();
        defaultWinner = null;
    }

    @Override
    public void addEditMenuOptions(Menu menu) {
        Menu m = new Menu(6, "Teams", menu.getViewer());
        m.setPreviousPage(menu);
        List<MenuItem> items = new ArrayList<>();
        List<String> teams = new ArrayList<>(this.teams.size() + 1);
        for (TeamColor t : this.teams.keySet()) {
            teams.add(MinigameUtils.capitalize(t.toString().replace("_", " ")));
        }
        teams.add("None");
        items.add(new MenuItemList("Default Winning Team", Material.PAPER, getDefaultWinnerCallback(), teams));
        items.add(new MenuItemNewLine());
        for (Team t : this.teams.values()) {
            items.add(new MenuItemTeam(t.getChatColor() + t.getDisplayName(), t));
        }

        m.addItem(new MenuItemAddTeam("Add Team", getMinigame()), m.getSize() - 1);

        m.addItems(items);

        m.addItem(new MenuItemPage("Back", getBackMaterial(), menu), m.getSize() - 9);

        MenuItemPage p = new MenuItemPage("Team Options", Material.CHEST, m);
        menu.addItem(p);
    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        return false;
    }
}
