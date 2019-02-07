package au.com.mineauz.minigames;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.objects.TestPlayer;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
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
    private Location spawn;
    private Location lobby;
    private Location end;
    private Location start;
    private Location quit;

    @Before
    public void Setup(){
        try {
            server = MockBukkit.mock();
        }catch (IllegalStateException e){
            server = MockBukkit.getMock();
        }
        ConsoleCommandSenderMock sender  = (ConsoleCommandSenderMock) server.getConsoleSender();
        sender.setOutputOnSend(true);
        MockBukkit.getMock().addSimpleWorld("GAMES");
        String ver = server.getBukkitVersion();
        Logger log = Logger.getAnonymousLogger();
        log.setLevel(Level.ALL);
        plugin = MockBukkit.load(Minigames.class);
        plugin.toggleDebug();
        plugin.setLog(log);
        world = MockBukkit.getMock().getWorld("GAMES");
        spawn =world.getSpawnLocation();
        createMinigame();
        player = new TestPlayer(MockBukkit.getMock(),"TestPlayer", UUID.randomUUID());
        player.setOutputOnSend(true);
        player.setLocation(spawn);
        MockBukkit.getMock().addPlayer(player);
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
        plugin.getPlayerManager().addMinigamePlayer(player);
        MinigamePlayer mplayer = plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId());
        plugin.getPlayerManager().joinMinigame(mplayer, game, false, 0D);
        player.assertLocation(lobby,0);
        plugin.getPlayerManager().startMPMinigame(game);
        //player.assertLocation(start,0);
    }
    
    public void onQuitMinigame(){
        plugin.getPlayerManager().addMinigamePlayer(player);
        MinigamePlayer mplayer = plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId());
        plugin.getPlayerManager().joinMinigame(mplayer, game, false, 0D);
        
    
    
    }
}