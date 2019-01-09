package au.com.mineauz.minigames;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.TestMinigame;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.objects.TestPlayer;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMockFactory;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
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
    private SQLiteDataSource datasource;
    private Location spawn;
    private Location lobby;
    private Location end;
    private Location start;
    private Location quit;

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
        spawn =world.getSpawnLocation();
        start = new Location(world,10,10,10);
        lobby = new Location(world,0,5,0);
        end = new Location(world,0,10,0);
        quit = new Location(world,0,20,0);
        game = new TestMinigame("TestGame",MinigameType.MULTIPLAYER,start,world,plugin.getMinigameManager(),quit,end,lobby);
        player = server.addPlayer();
        player.setLocation(new Location(world,0,0,0));
        player.setOutputOnSend(true);

    }

    private void createMinigame(){
        start = new Location(world,0,21,0);
        game = new Minigame("TestGame",MinigameType.MULTIPLAYER,start);
        game.setType(MinigameType.MULTIPLAYER);
        game.setMechanic(GameMechanics.MECHANIC_NAME.CTF.toString());
        game.setDeathDrops(true);
        quit = new Location(world,0,20,0);
        game.setQuitPosition(quit);
        lobby= new Location(world,0,5.,0);
        game.setLobbyPosition(lobby);
        end = new Location(world, 0, 25, 0);
        game.setEndPosition(end);
        game.setEnabled(true);
        game.setStartWaitTime(5);
        game.setTimer(5);
        game.setMaxScore(3);
        game.setMaxPlayers(2);
        MinigameModule module = game.getModule("LobbySettings");
        if(module != null) {
            LobbySettingsModule lMod = (LobbySettingsModule) module;
            lMod.setTeleportOnPlayerWait(true);
            lMod.setTeleportOnStart(true);
        }
        plugin.getMinigameManager().addMinigame(game);
    }
    
    @Test
    public void onJoinMinigame() {
        assertEquals(new Location(world,0,0,0),player.getLocation());
        plugin.getPlayerManager().addMinigamePlayer(player);
        MinigamePlayer mplayer = plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId());
        plugin.getPlayerManager().joinMinigame(mplayer, game, false, 0D);
        player.assertLocation(lobby,0);
        assertEquals(lobby,player.getLocation());
        server.getScheduler().performOneTick();
        TestPlayer player3  = (TestPlayer) server.addPlayer();
        plugin.getPlayerManager().addMinigamePlayer(player3);
        player3.setOutputOnSend(true);
        MinigamePlayer mPlayer2 = plugin.getPlayerManager().getMinigamePlayer(player3.getUniqueId());
        plugin.getPlayerManager().joinMinigame(mPlayer2, game, false, 0D);
        plugin.getPlayerManager().startMPMinigame(game);
        server.getScheduler().performTicks(600);
        //player.assertLocation(start,0);
    }
    
    public void onQuitMinigame(){
        plugin.getPlayerManager().addMinigamePlayer(player);
        MinigamePlayer mplayer = plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId());
        plugin.getPlayerManager().joinMinigame(mplayer, game, false, 0D);
        
    
    
    }
}