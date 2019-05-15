package au.com.mineauz.minigames;

import java.util.logging.Level;
import java.util.logging.Logger;

import au.com.mineauz.minigames.commands.JoinCommand;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.helpers.TestHelper;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.SignBlockMock;
import au.com.mineauz.minigames.objects.TestPlayer;
import au.com.mineauz.minigames.objects.TestWorld;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMockFactory;
import org.bukkit.Location;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;

import static org.junit.Assert.*;

/**
 * Created for the AddstarMC Project. Created by Narimm on 6/02/2019.
 */
public class MinigamePlayerManagerTest {
    private ServerMock server;
    private Minigames plugin;
    private Minigame game;

    @Before
    public void Setup() {
        try {
            server = MockBukkit.mock();
        } catch (IllegalStateException e) {
            server = MockBukkit.getMock();
        }
        server.setPlayerFactory(new PlayerMockFactory(server, TestPlayer.class));
        ((ConsoleCommandSenderMock) server.getConsoleSender()).setOutputOnSend(false);
        WorldMock world = new TestWorld();
        world.setName("GAMES");
        world = MockBukkit.getMock().addWorld(world);
        Logger log = Logger.getAnonymousLogger();
        log.setLevel(Level.ALL);
        plugin = MockBukkit.load(Minigames.class);
        plugin.toggleDebug();
        plugin.setLog(log);
        game = TestHelper.createMinigame(plugin, world, MinigameType.MULTIPLAYER, GameMechanics.MECHANIC_NAME.KILLS);
    }

    @After
    public void TearDown() {
        try {
            MockBukkit.unload();
            server = null;
            plugin = null;
        } catch (Exception e) {
        }
    }
    @Test
    public void joinMinigame() {
        final PlayerMock mock = server.addPlayer();
        mock.setOutputOnSend(false);
        plugin.getPlayerManager().addMinigamePlayer(mock);
        assertTrue(plugin.getPlayerManager().hasMinigamePlayer(mock.getUniqueId()));
        plugin.getPlayerManager().joinMinigame(plugin.getPlayerManager().getMinigamePlayer(mock), game, false, 0.0);
        assertTrue(plugin.getPlayerManager().getMinigamePlayer(mock.getUniqueId()).isInMinigame());

    }
    
    /*@Test
    public void spectateMinigame() {
    
    }
    
    @Test
    public void startMPMinigame() {
    }
    
    @Test
    public void startMPMinigame1() {
    }
    
    @Test
    public void balanceGame() {
    }
    
    @Test
    public void teleportToStart() {
    }
    
    @Test
    public void getStartLocations() {
    }
    
    @Test
    public void revertToCheckpoint() {
    }
    */

    /*public void quitMinigame() {
        final PlayerMock mock = server.addPlayer();
        mock.setOutputOnSend(false);
        plugin.getPlayerManager().addMinigamePlayer(mock);
        assertTrue(plugin.getPlayerManager().hasMinigamePlayer(mock.getUniqueId()));
        plugin.getPlayerManager().joinMinigame(plugin.getPlayerManager().getMinigamePlayer(mock), game, false, 0.0);
        assertTrue(plugin.getPlayerManager().getMinigamePlayer(mock.getUniqueId()).isInMinigame());
        plugin.getPlayerManager().quitMinigame(plugin.getPlayerManager().getMinigamePlayer(mock), false);
        assertFalse(plugin.getPlayerManager().getMinigamePlayer(mock.getUniqueId()).isInMinigame());
        MockBukkit.getMock().getScheduler().performTicks(20);

    }*/
    /*
    @Test
    public void endMinigame() {
    }
    
    @Test
    public void endMinigame1() {
    }
    
    @Test
    public void broadcastEndGame() {
    }
    
    @Test
    public void playerInMinigame() {
    }
    
    @Test
    public void playersInMinigame() {
    }
    
    @Test
    public void addMinigamePlayer() {
    }
    
    @Test
    public void removeMinigamePlayer() {
    }
    
    @Test
    public void getMinigamePlayer() {
    }
    
    @Test
    public void getMinigamePlayer1() {
    }
    
    @Test
    public void getMinigamePlayer2() {
    }
    
    @Test
    public void getAllMinigamePlayers() {
    }
    
    @Test
    public void hasMinigamePlayer() {
    }
    
    @Test
    public void hasMinigamePlayer1() {
    }*/
}