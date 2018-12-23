package au.com.mineauz.minigames;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.TestPlayer;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;


import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private SQLiteDataSource datasource;

    @Before
    public void Setup(){

        server = MockBukkit.mock();
        MockBukkit.getMock().addSimpleWorld("GAMES");
        String ver = server.getBukkitVersion();
        Logger log = Logger.getAnonymousLogger();
        log.setLevel(Level.ALL);
        plugin = MockBukkit.load(Minigames.class);
        plugin.toggleDebug();
        plugin.setLog(log);
        world = MockBukkit.getMock().getWorld("GAMES");
        createMinigame();
        player = new TestPlayer(MockBukkit.getMock(),"TestPlayer", UUID.randomUUID());
        player.setLocation(new Location(world,0,0,0));
        MockBukkit.getMock().addPlayer(player);
    }

    private void createMinigame(){
        game = new Minigame("TestGame",MinigameType.MULTIPLAYER,new Location(world,0,10,0));
        game.setType(MinigameType.MULTIPLAYER);
        game.setMechanic(GameMechanics.MECHANIC_NAME.CTF.toString());
        game.setDeathDrops(true);
        game.setQuitPosition(new Location(world,0,20,0));
        game.setLobbyPosition(new Location(world,0,5.,0));
        game.setEndPosition(new Location(world,0,21,0));
        game.setEnabled(true);
        game.setStartWaitTime(5);
        game.setTimer(5);
        game.setMaxScore(3);
        game.setMaxPlayers(2);
        plugin.getMinigameManager().addMinigame(game);
    }
    
    @Test
    public void onJoinMinigame() {
        plugin.getPlayerManager().addMinigamePlayer(player);
        MinigamePlayer mplayer = plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId());
        System.out.println(player.getLocation().toString());
        plugin.getPlayerManager().joinMinigame(mplayer, game, false, 0D);
        String message = player.nextMessage();
        while (message != null) {
            System.out.println(message);
            message = player.nextMessage();
        }
        plugin.getPlayerManager().startMPMinigame(game);
    }
}