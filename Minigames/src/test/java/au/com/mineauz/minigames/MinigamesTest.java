package au.com.mineauz.minigames;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.TestMinigame;
import au.com.mineauz.minigames.objects.TestPlayer;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMockFactory;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;


import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 8/07/2018.
 */

public class MinigamesTest {
    
    private ServerMock server;
    private Minigames plugin;
    private PlayerMock player;
    private Minigame game;
    private World world;
    private Location start;
    private Location lobby;
    private Location quit;
    private Location end;
    private SQLiteDataSource datasource;//to ensure sqlite jbdc is loaded

    @Before
    public void Setup(){

        server = MockBukkit.mock();
        server.setPlayerFactory(new PlayerMockFactory(server,TestPlayer.class));
        ((ConsoleCommandSenderMock)server.getConsoleSender()).setOutputOnSend(true);
        MockBukkit.getMock().addSimpleWorld("GAMES");
        Logger log = Logger.getAnonymousLogger();
        log.setLevel(Level.ALL);
        plugin = MockBukkit.load(Minigames.class);
        plugin.toggleDebug();
        plugin.setLog(log);
        world = MockBukkit.getMock().getWorld("GAMES");
        start = new Location(world,10,10,10);
        lobby = new Location(world,0,5,0);
        end = new Location(world,0,10,0);
        quit = new Location(world,0,20,0);
        game = new TestMinigame("TestGame",MinigameType.MULTIPLAYER,start,world,plugin.getMinigameManager(),quit,end,lobby);
        player = server.addPlayer();
        player.setLocation(new Location(world,0,0,0));
        player.setOutputOnSend(true);

    }


    
    @Test
    public void onJoinMinigame() {
        assertEquals(new Location(world,0,0,0),player.getLocation());
        plugin.getPlayerManager().addMinigamePlayer(player);
        MinigamePlayer mplayer = plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId());
        System.out.println(player.getLocation().toString());
        plugin.getPlayerManager().joinMinigame(mplayer, game, false, 0D);
        assertEquals(lobby,player.getLocation());
        server.getScheduler().performOneTick();
        TestPlayer player3  = (TestPlayer) server.addPlayer();
        plugin.getPlayerManager().addMinigamePlayer(player3);
        player3.setOutputOnSend(true);
        MinigamePlayer mPlayer2 = plugin.getPlayerManager().getMinigamePlayer(player3.getUniqueId());
        plugin.getPlayerManager().joinMinigame(mPlayer2, game, false, 0D);
        plugin.getPlayerManager().startMPMinigame(game);
        server.getScheduler().performTicks(600);

    }
}