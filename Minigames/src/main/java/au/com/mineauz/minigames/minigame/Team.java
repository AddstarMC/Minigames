package au.com.mineauz.minigames.minigame;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.*;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.script.ScriptCollection;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigames.script.ScriptValue;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Team implements ScriptObject {
    private String displayName = null;
    private TeamColor color;
    private final IntegerFlag maxPlayers = new IntegerFlag(0, "maxPlayers");
    private final List<Location> startLocations = new ArrayList<>();
    private final StringFlag assignMsg = new StringFlag(MinigameUtils.getLang("player.team.assign.joinTeam"), "assignMsg");
    private final StringFlag gameAssignMsg = new StringFlag(MinigameUtils.getLang("player.team.assign.joinAnnounce"), "gameAssignMsg");
    private final StringFlag autobalanceMsg = new StringFlag(MinigameUtils.getLang("player.team.autobalance.plyMsg"), "autobalanceMsg");
    private final StringFlag gameAutobalanceMsg = new StringFlag(MinigameUtils.getLang("player.team.autobalance.minigameMsg"), "gameAutobalanceMsg");
    private final EnumFlag<OptionStatus> nametagVisibility = new EnumFlag<>(OptionStatus.ALWAYS, "nametagVisibility");
    private final BooleanFlag autoBalance = new BooleanFlag(true, "autoBalance");

    private final List<MinigamePlayer> players = new ArrayList<>();
    private int score = 0;
    private final Minigame mgm;

    private String scoreboardName;

    /**
     * Creates a team for the use in a specific Minigame
     *
     * @param color    - The unique team color to identify the team by.
     * @param minigame - The Minigame this team is assigned to.
     */
    public Team(TeamColor color, Minigame minigame) {
        this.color = color;
        displayName = MinigameUtils.capitalize(color.toString()) + " Team";
        scoreboardName = color.toString().toLowerCase();
        mgm = minigame;
    }

    /**
     * Gets the teams Minigame
     *
     * @return The Minigame this team is assigned to.
     */
    public Minigame getMinigame() {
        return mgm;
    }

    /**
     * Changes the color of the team for the Minigame its assigned to.
     *
     * @param color - The color to change this team to.
     * @return true if the Minigame doesn't have the team color already available, fails if it already has that team.
     */
    public boolean setColor(TeamColor color) {
        if (!TeamsModule.getMinigameModule(mgm).hasTeam(color)) {
            if (displayName.toLowerCase().equals(this.color.toString().toLowerCase() + " team"))
                displayName = MinigameUtils.capitalize(color.toString()) + " Team";
            TeamsModule.getMinigameModule(mgm).removeTeam(this.color);
            this.color = color;
            TeamsModule.getMinigameModule(mgm).addTeam(color, this);

            return true;
        }
        return false;
    }

    /**
     * Gets the teams color.
     *
     * @return The teams color.
     */
    public TeamColor getColor() {
        return color;
    }

    /**
     * Gets the teams ChatColor alternative.
     *
     * @return The ChatColor
     */
    public ChatColor getChatColor() {
        return color.getColor();
    }

    /**
     * Gets the teams display name. If none is set, it will return the teams color followed by "Team".
     *
     * @return The display name or the teams color followed by "Team"
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the display name prefixed with its color. If none is set, 
     * it will return the teams color followed by "Team".
     * 
     * @return The colored display name or the team color followed by "Team"
     */
    public String getColoredDisplayName() {
        return getChatColor() + getDisplayName();
    }

    /**
     * Sets the display name for this team. If the name is longer than 32 characters,
     * it'll be trimmed to that length (Minecraft limitation).
     *
     * @param name - The name to change the team to.
     */
    public void setDisplayName(String name) {
        if (name.length() > 32)
            name = name.substring(0, 31);
        displayName = name;
    }

    public Set<Flag<?>> getFlags() {
        Set<Flag<?>> flags = new HashSet<>();
        flags.add(maxPlayers);
        flags.add(assignMsg);
        flags.add(gameAssignMsg);
        flags.add(gameAutobalanceMsg);
        flags.add(autobalanceMsg);
        flags.add(nametagVisibility);
        flags.add(autoBalance);

        return flags;
    }

    public int getMaxPlayers() {
        return maxPlayers.getFlag();
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers.setFlag(maxPlayers);
    }

    public boolean isFull() {
        return maxPlayers.getFlag() != 0 && players.size() >= maxPlayers.getFlag();
    }


    /**
     * Gets the teams current score
     *
     * @return The score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the teams score to a specific value.
     *
     * @param amount The score amount to set for the team.
     */
    public void setScore(int amount) {
      score = amount;
      Objective obj = mgm.getScoreboardManager().getObjective(mgm.getName(false));
      if(obj != null)obj.getScore(getColoredDisplayName()).setScore(score);
    }

    /**
     * Adds 1 point to the team.
     */
    public int addScore() {
        return addScore(1);
    }

    /**
     * Adds a specific amount to the teams score.
     *
     * @param amount - The amount of points to add to the team
     * @return The new score for the team.
     */
    public int addScore(int amount) {
        score += amount;
        Objective obj = mgm.getScoreboardManager().getObjective(mgm.getName(false));
        if(obj != null)obj.getScore(getColoredDisplayName()).setScore(score);
        return score;
    }

    /**
     * Sets the teams score back to 0.
     */
    public void resetScore() {
        score = 0;
        mgm.getScoreboardManager().resetScores(getColoredDisplayName());
    }

    /**
     * Gets a list of all the players assigned to this team.
     *
     * @return A list of all players assigned to the team.
     */
    public List<MinigamePlayer> getPlayers() {
        return players;
    }

    /**
     * Adds a player to the team.
     *
     * @param player - The player to add.
     */
    public void addPlayer(MinigamePlayer player) {
        players.add(player);
        player.setTeam(this);
        player.getPlayer().setScoreboard(mgm.getScoreboardManager());
        org.bukkit.scoreboard.Team team = mgm.getScoreboardManager().getTeam(scoreboardName);
        if(team != null)team.addEntry(player.getDisplayName(mgm.usePlayerDisplayNames()));
    }

    /**
     * Removes a player from the team.
     *
     * @param player - The player to remove.
     */
    public void removePlayer(MinigamePlayer player) {
        players.remove(player);
        Scoreboard board = mgm.getScoreboardManager();
      org.bukkit.scoreboard.Team team = board.getTeam(scoreboardName);
      if(team != null) {
       team.removeEntry(player.getDisplayName(mgm.usePlayerDisplayNames()));
      }
      player.getPlayer().setScoreboard(Minigames.getPlugin().getServer().getScoreboardManager().getMainScoreboard());
    }

    /**
     * Adds a starting location for the team to spawn at.
     *
     * @param loc - The location to add to the team.
     */
    public void addStartLocation(Location loc) {
        startLocations.add(loc);
    }

    /**
     * Replaces a starting location already assigned on the team.
     *
     * @param loc    - The new location
     * @param number - The number id of the original starting location (Ranging from 1 to the amount of start points [Not 0])
     */
    public void addStartLocation(Location loc, int number) {
        if (startLocations.size() >= number) {
            startLocations.set(number - 1, loc);
        } else {
            startLocations.add(loc);
        }
    }

    /**
     * Gets all the starting locations for this team.
     *
     * @return The teams starting locations.
     */
    public List<Location> getStartLocations() {
        return startLocations;
    }

    /**
     * Gets whether the team has start locations
     *
     * @return true if the team has start locations
     */
    public boolean hasStartLocations() {
        return !startLocations.isEmpty();
    }

    /**
     * Removes a specific start location from this team.
     *
     * @param locNumber - The id of the starting location.
     * @return true if removal was successful.
     */
    public boolean removeStartLocation(int locNumber) {
        if (startLocations.size() > locNumber) {
            startLocations.remove(locNumber);
            return true;
        }
        return false;
    }

    public String getAssignMessage() {
        return assignMsg.getFlag();
    }

    public void setAssignMessage(String msg) {
        assignMsg.setFlag(msg);
    }

    public String getGameAssignMessage() {
        return gameAssignMsg.getFlag();
    }

    public void setGameAssignMessage(String msg) {
        gameAssignMsg.setFlag(msg);
    }

    public String getAutobalanceMessage() {
        return autobalanceMsg.getFlag();
    }

    public void setAutobalanceMessage(String msg) {
        autobalanceMsg.setFlag(msg);
    }

    public String getGameAutobalanceMessage() {
        return gameAutobalanceMsg.getFlag();
    }

    public void setGameAutobalanceMessage(String msg) {
        gameAutobalanceMsg.setFlag(msg);
    }

    public OptionStatus getNameTagVisibility() {
        return nametagVisibility.getFlag();
    }

    public void setNameTagVisibility(OptionStatus vis) {
        nametagVisibility.setFlag(vis);
        org.bukkit.scoreboard.Team team = mgm.getScoreboardManager().getTeam(color.toString().toLowerCase());
        if(team != null)
          team.setOption(Option.NAME_TAG_VISIBILITY, vis);
        else
          Minigames.log().warning("No team set for visibility call");
    }

    public Callback<String> getNameTagVisibilityCallback() {
        return new Callback<String>() {

            @Override
            public String getValue() {
                return getNameTagVisibility().toString();
            }            @Override
            public void setValue(String value) {
                setNameTagVisibility(OptionStatus.valueOf(value));
            }


        };
    }

    public Callback<Boolean> getAutoBalanceCallBack() {
        return new Callback<Boolean>() {

            @Override
            public Boolean getValue() {
                return getAutoBalanceTeam();
            }            @Override
            public void setValue(Boolean value) {
                setAutoBalance(value);
            }


        };
    }

    public boolean getAutoBalanceTeam() {
        return autoBalance.getFlag();
    }

    public void setAutoBalance(Boolean flag) {
        autoBalance.setFlag(flag);
    }

    @Override
    public ScriptReference get(String name) {
        if (name.equalsIgnoreCase("colorname")) {
            return ScriptValue.of(getColor().name());
        } else if (name.equalsIgnoreCase("color")) {
            return ScriptValue.of(getChatColor().toString());
        } else if (name.equalsIgnoreCase("name")) {
            return ScriptValue.of(getDisplayName());
        } else if (name.equalsIgnoreCase("score")) {
            return ScriptValue.of(score);
        } else if (name.equalsIgnoreCase("players")) {
            return ScriptCollection.of(players);
        } else if (name.equalsIgnoreCase("minigame")) {
            return mgm;
        }

        return null;
    }

    @Override
    public Set<String> getKeys() {
        return ImmutableSet.of("colorname", "color", "name", "score", "players", "minigame");
    }

    @Override
    public String getAsString() {
        return getColor().name();
    }
}
