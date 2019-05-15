package au.com.mineauz.minigames;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.TestPlayer;
import au.com.mineauz.minigames.objects.TestWorld;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.sun.org.apache.bcel.internal.generic.LMUL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;


import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

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
    public void Setup() {
        try {
            server = MockBukkit.mock();
        } catch (IllegalStateException e) {
            server = MockBukkit.getMock();
        }
        ConsoleCommandSenderMock sender = (ConsoleCommandSenderMock) server.getConsoleSender();
        sender.setOutputOnSend(true);
        TestWorld testworld = new TestWorld();
        testworld.setName("GAMES");
        MockBukkit.getMock().addWorld(testworld);
        String ver = server.getBukkitVersion();
        Logger log = Logger.getAnonymousLogger();
        log.setLevel(Level.ALL);
        plugin = MockBukkit.load(Minigames.class);
        plugin.toggleDebug();
        plugin.setLog(log);
        world = MockBukkit.getMock().getWorld("GAMES");
        spawn = world.getSpawnLocation();
        createMinigame();
        player = new TestPlayer(MockBukkit.getMock(), "TestPlayer", UUID.randomUUID());
        player.setOutputOnSend(true);
        player.setLocation(spawn);
        MockBukkit.getMock().addPlayer(player);
    }

    private void createMinigame() {
        start = new Location(world, 0, 21, 0);
        game = new Minigame("TestGame", MinigameType.MULTIPLAYER, start);
        game.setType(MinigameType.MULTIPLAYER);
        game.setMechanic(GameMechanics.MECHANIC_NAME.CTF.toString());
        game.setDeathDrops(true);
        quit = new Location(world, 0, 20, 0);
        game.setQuitPosition(quit);
        lobby = new Location(world, 0, 5., 0);
        game.setLobbyPosition(lobby);
        end = new Location(world, 0, 25, 0);
        game.setEndPosition(end);
        game.setEnabled(true);
        game.setStartWaitTime(5);
        game.setTimer(5);
        game.setMaxScore(3);
        game.setMaxPlayers(2);
        game.setMinPlayers(1);
        MinigameModule module = game.getModule("LobbySettings");
        if (module != null) {
            LobbySettingsModule lMod = (LobbySettingsModule) module;
            lMod.setTeleportOnPlayerWait(true);
            lMod.setTeleportOnStart(true);
            lMod.setPlayerWaitTime(5);
        }
        MinigameModule lmod = game.getModule("Loadouts");
        if (lmod != null) {
            LoadoutModule lmodule = (LoadoutModule) lmod;
            PlayerLoadout pl = new PlayerLoadout("TestLoadout");
            pl.addItem(new ItemStack(Material.LEATHER_HELMET,1),103);
            lmodule.getLoadoutMap().put("TestLoadout",pl);
        }
        plugin.getMinigameManager().addMinigame(game);
    }
    @Test
    public void onJoinMinigame() {
        assertNotSame(player.getLocation(),game.getLobbyPosition());
        plugin.getPlayerManager().addMinigamePlayer(player);
        MinigamePlayer mplayer = plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId());
        plugin.getPlayerManager().joinMinigame(mplayer, game, false, 0D);
        LobbySettingsModule module = (LobbySettingsModule) game.getModule("LobbySettings");
        player.assertLocation(lobby, 0);
        assertTrue(module.isTeleportOnStart());
        assertNotSame(player.getLocation(),game.getStartLocations().indexOf(0));
        server.getScheduler().performTicks(400L);
        player.assertLocation(start,0);
    }
    @Test
    public void onQuitMinigame() {
        plugin.getPlayerManager().addMinigamePlayer(player);
        MinigamePlayer mplayer = plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId());
        plugin.getPlayerManager().joinMinigame(mplayer, game, false, 0D);
        player.assertLocation(lobby, 0);
        Assert.assertTrue(plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId()).isInMinigame());

        plugin.getPlayerManager().quitMinigame(plugin.getPlayerManager().getMinigamePlayer(player), false);
        player.assertLocation(quit, 0);
        assertFalse(plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId()).isInMinigame());
        server.getScheduler().performTicks(400L);

    }

    public void testOnDisable(){
        assertTrue(plugin.isEnabled());
        server.getPluginManager().disablePlugin(plugin);
        assertFalse(plugin.isEnabled());
    }
}