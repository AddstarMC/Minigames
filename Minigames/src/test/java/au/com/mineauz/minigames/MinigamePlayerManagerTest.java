package au.com.mineauz.minigames;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.helpers.TestHelper;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.TestPlayer;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinigamePlayerManagerTest {
    private ServerMock server;
    private Minigames plugin;
    private Minigame game;

    @BeforeEach
    public void Setup() {
        try {
            server = MockBukkit.mock();
        } catch (IllegalStateException e) {
            server = MockBukkit.getMock();
        }
        server.addPlayer(new TestPlayer(server, "Silverzahn", UUID.randomUUID()));
        WorldMock world = new WorldMock();
        world.setName("GAMES");
        MockBukkit.getMock().addWorld(world);
        Logger log = Logger.getAnonymousLogger();
        log.setLevel(Level.ALL);
        plugin = MockBukkit.load(Minigames.class);
        plugin.toggleDebug();
        plugin.setLog(log);
        game = TestHelper.createMinigame(plugin, world, MinigameType.MULTIPLAYER, GameMechanics.MECHANIC_NAME.KILLS);
    }

    @AfterEach
    public void TearDown() {
        try {
            MockBukkit.unmock();
            server = null;
            plugin = null;
        } catch (Exception ignored) {
        }
    }

    @Test
    public void joinMinigame() {
        final PlayerMock mock = server.getPlayer(0);
        plugin.getPlayerManager().addMinigamePlayer(mock);
        Assertions.assertTrue(plugin.getPlayerManager().hasMinigamePlayer(mock.getUniqueId()));
        plugin.getPlayerManager().joinMinigame(plugin.getPlayerManager().getMinigamePlayer(mock), game, false, 0.0);
        Assertions.assertTrue(plugin.getPlayerManager().getMinigamePlayer(mock.getUniqueId()).isInMinigame());
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