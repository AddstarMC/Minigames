package au.com.mineauz.minigames;

import java.util.logging.Level;
import java.util.logging.Logger;

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
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMockFactory;
import org.bukkit.Location;
import org.bukkit.event.player.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created for the AddstarMC Project. Created by Narimm on 6/02/2019.
 */
public class EventsTest {

    private ServerMock server;
    private Minigames plugin;
    private Minigame game;

    @Before
    public void setUp() throws Exception {
        try {
            server = MockBukkit.mock();
        } catch (IllegalStateException e) {
            server = MockBukkit.getMock();
        }
        PlayerMockFactory<TestPlayer> factory = new PlayerMockFactory<>(server,TestPlayer.class);
        server.setPlayerFactory(factory);
        ((ConsoleCommandSenderMock) server.getConsoleSender()).setOutputOnSend(true);
        plugin = MockBukkit.load(Minigames.class);
        Minigames.getPlugin().getConfig().set("saveInventory", true);
        TestWorld testworld = new TestWorld();
        testworld.setName("GAMES");
        MockBukkit.getMock().addWorld(testworld);
        Logger log = Logger.getAnonymousLogger();
        log.setLevel(Level.ALL);
        plugin.toggleDebug();
        plugin.setLog(log);
        WorldMock world = (WorldMock) MockBukkit.getMock().getWorld("GAMES");
        game = TestHelper.createMinigame(plugin, world, MinigameType.MULTIPLAYER, GameMechanics.MECHANIC_NAME.KILLS);

    }

    public void onPlayerDisconnect() {
        PlayerMock mock = server.addPlayer();
        mock.setLocation(server.getWorld("GAMES").getSpawnLocation());
        PlayerJoinEvent event = new PlayerJoinEvent(mock, "Joined the Server");
        server.getPluginManager().callEvent(event);
        MinigamePlayer player = plugin.getPlayerManager().getMinigamePlayer(mock);
        JoinCommand command = new JoinCommand();
        String[] args = {game.getName(false)};
        command.onCommand(mock, game, "", args);
        assertTrue(player.isInMinigame());
        PlayerQuitEvent event2 = new PlayerQuitEvent(mock, "has left the game");
        server.getPluginManager().callEvent(event2);
        assertFalse(player.isInMinigame());
        assertFalse(plugin.getPlayerManager().hasMinigamePlayer(player.getUUID()));
    }

    public void onPlayerConnect() {
        PlayerMock mock = server.addPlayer();
        PlayerJoinEvent event = new PlayerJoinEvent(mock, "Joined the Server");
        server.getPluginManager().callEvent(event);
        assertTrue(plugin.getPlayerManager().hasMinigamePlayer(mock.getUniqueId()));

    }

}