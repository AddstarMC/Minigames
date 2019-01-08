package au.com.mineauz.minigames.objects;

import be.seeseemelk.mockbukkit.scoreboard.ScoreboardMock;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Team;

import java.util.Set;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 24/12/2018.
 */
public class MinigameScoreboardMock extends ScoreboardMock {

    private Team teams;

    @Override
    public Team getPlayerTeam(OfflinePlayer player) throws IllegalArgumentException {
        return super.getPlayerTeam(player);
    }

    @Override
    public Team getTeam(String teamName) throws IllegalArgumentException {
        return super.getTeam(teamName);
    }

    @Override
    public Set<Team> getTeams() {
        return super.getTeams();
    }

    @Override
    public Team registerNewTeam(String name) throws IllegalArgumentException {
        return super.registerNewTeam(name);
    }
}
