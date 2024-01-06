package au.com.mineauz.minigames;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.helpers.TestHelper;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.TestPlayer;
import au.com.mineauz.minigames.objects.TestWorld;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class MinigamesTest {
    private ServerMock server;
    private Minigames plugin;
    private PlayerMock player;
    private Minigame game;
    private WorldMock world;
    private Location spawn;
    private Location lobby;
    private Location start;
    private Location quit;

    @BeforeEach
    public void Setup() {
        try {
            server = MockBukkit.mock();
        } catch (IllegalStateException e) {
            server = MockBukkit.getMock();
        }

        TestWorld testworld = new TestWorld();
        testworld.setName("GAMES");
        MockBukkit.getMock().addWorld(testworld);

        plugin = MockBootstrap.createPluginWithTestContext(server);

        server.getPluginManager().registerLoadedPlugin(plugin);
        server.getPluginManager().enablePlugin(plugin);
        plugin.toggleDebug();
        world = (WorldMock) MockBukkit.getMock().getWorld("GAMES");
        spawn = world.getSpawnLocation();
        TestHelper.createMinigame(plugin, world, MinigameType.MULTIPLAYER, GameMechanics.MG_MECHANICS.CTF.getMechanic());
        player = new TestPlayer(MockBukkit.getMock(), "TestPlayer", UUID.randomUUID());
        player.setLocation(spawn);
        MockBukkit.getMock().addPlayer(player);
    }

    @Test
    public void onJoinMinigame() {
        Assertions.assertNotSame(player.getLocation(), game.getLobbyLocation());
        plugin.getPlayerManager().addMinigamePlayer(player);
        MinigamePlayer mplayer = plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId());
        plugin.getPlayerManager().joinMinigame(mplayer, game, false, 0.0);
        LobbySettingsModule module = LobbySettingsModule.getMinigameModule(game);
        player.assertLocation(lobby, 0.0);
        Assertions.assertTrue(module.isTeleportOnStart());
        Assertions.assertNotSame(game.getStartLocations().indexOf(player.getLocation()), -1);
        server.getScheduler().performTicks(200L);
        player.assertLocation(start, 0);
        server.getScheduler().performTicks(200L);
        player.assertLocation(quit, 0);
    }

    @Test
    public void onQuitMinigame() {
        plugin.getPlayerManager().addMinigamePlayer(player);
        MinigamePlayer mplayer = plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId());
        plugin.getPlayerManager().joinMinigame(mplayer, game, false, 0D);
        player.assertLocation(lobby, 0);
        Assertions.assertTrue(plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId()).isInMinigame());
        plugin.getPlayerManager().quitMinigame(plugin.getPlayerManager().getMinigamePlayer(player), false);
        player.assertLocation(quit, 0);
        Assertions.assertFalse(plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId()).isInMinigame());
    }

    @Test
    public void testOnDisable() {
        Assertions.assertTrue(plugin.isEnabled());
        server.getPluginManager().disablePlugin(plugin);
        Assertions.assertFalse(plugin.isEnabled());
    }
}