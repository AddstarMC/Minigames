package au.com.mineauz.minigames.objects;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 24/12/2018.
 */
public class TeamMock implements Team {
    @Override
    public String getName() throws IllegalStateException {
        return null;
    }

    @Override
    public String getDisplayName() throws IllegalStateException {
        return null;
    }

    @Override
    public void setDisplayName(String s) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public String getPrefix() throws IllegalStateException {
        return null;
    }

    @Override
    public void setPrefix(String s) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public String getSuffix() throws IllegalStateException {
        return null;
    }

    @Override
    public void setSuffix(String s) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public ChatColor getColor() throws IllegalStateException {
        return null;
    }

    @Override
    public void setColor(ChatColor chatColor) {

    }

    @Override
    public boolean allowFriendlyFire() throws IllegalStateException {
        return false;
    }

    @Override
    public void setAllowFriendlyFire(boolean b) throws IllegalStateException {

    }

    @Override
    public boolean canSeeFriendlyInvisibles() throws IllegalStateException {
        return false;
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean b) throws IllegalStateException {

    }

    @Override
    public NameTagVisibility getNameTagVisibility() throws IllegalArgumentException {
        return null;
    }

    @Override
    public void setNameTagVisibility(NameTagVisibility nameTagVisibility) throws IllegalArgumentException {

    }

    @Override
    public Set<OfflinePlayer> getPlayers() throws IllegalStateException {
        return null;
    }

    @Override
    public Set<String> getEntries() throws IllegalStateException {
        return null;
    }

    @Override
    public int getSize() throws IllegalStateException {
        return 0;
    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public void addPlayer(OfflinePlayer offlinePlayer) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public void addEntry(String s) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public boolean removePlayer(OfflinePlayer offlinePlayer) throws IllegalStateException, IllegalArgumentException {
        return false;
    }

    @Override
    public boolean removeEntry(String s) throws IllegalStateException, IllegalArgumentException {
        return false;
    }

    @Override
    public void unregister() throws IllegalStateException {

    }

    @Override
    public boolean hasPlayer(OfflinePlayer offlinePlayer) throws IllegalArgumentException, IllegalStateException {
        return false;
    }

    @Override
    public boolean hasEntry(String s) throws IllegalArgumentException, IllegalStateException {
        return false;
    }

    @Override
    public OptionStatus getOption(Option option) throws IllegalStateException {
        return null;
    }

    @Override
    public void setOption(Option option, OptionStatus optionStatus) throws IllegalStateException {

    }
}
