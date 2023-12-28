package au.com.mineauz.minigames;

import au.com.mineauz.minigames.commands.JoinCommand;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.helpers.TestHelper;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.TestPlayer;
import au.com.mineauz.minigames.objects.TestWorld;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

public class EventsTest {
    private ServerMock server;
    private Minigames plugin;
    private Minigame game;

    @BeforeEach
    public void setUp() {
        try {
            server = MockBukkit.mock();
        } catch (IllegalStateException e) {
            server = MockBukkit.getMock();
        }

        server.addPlayer(new TestPlayer(server, "testplayer", UUID.randomUUID()));
        plugin = MockBootstrap.createPluginWithTestContext(server);
        Minigames.getPlugin().getConfig().set("saveInventory", true);
        TestWorld testworld = new TestWorld();
        testworld.setName("GAMES");
        MockBukkit.getMock().addWorld(testworld);
        plugin.toggleDebug();
        WorldMock world = (WorldMock) MockBukkit.getMock().getWorld("GAMES");
        game = TestHelper.createMinigame(plugin, world, MinigameType.MULTIPLAYER, GameMechanics.MECHANIC_NAME.KILLS);

    }

    public void onPlayerDisconnect() {
        PlayerMock mock = server.getPlayer(0);
        mock.setLocation(server.getWorld("GAMES").getSpawnLocation());
        PlayerJoinEvent event = new PlayerJoinEvent(mock, "Joined the Server");
        server.getPluginManager().callEvent(event);
        MinigamePlayer player = plugin.getPlayerManager().getMinigamePlayer(mock);
        JoinCommand command = new JoinCommand();
        String[] args = new String[]{game.getName(false)};
        command.onCommand(mock, game, "", args);
        Assertions.assertTrue(player.isInMinigame());
        PlayerQuitEvent event2 = new PlayerQuitEvent(mock, "has left the game");
        server.getPluginManager().callEvent(event2);
        Assertions.assertFalse(player.isInMinigame());
        Assertions.assertFalse(plugin.getPlayerManager().hasMinigamePlayer(player.getUUID()));
    }

    public void onPlayerConnect() {
        PlayerMock mock = server.addPlayer();
        PlayerJoinEvent event = new PlayerJoinEvent(mock, "Joined the Server");
        server.getPluginManager().callEvent(event);
        Assertions.assertTrue(plugin.getPlayerManager().hasMinigamePlayer(mock.getUniqueId()));
    }
}