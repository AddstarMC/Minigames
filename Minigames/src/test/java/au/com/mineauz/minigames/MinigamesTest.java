package au.com.mineauz.minigames;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.helpers.TestHelper;
import au.com.mineauz.minigames.mechanics.CTFMechanic;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.objects.SignBlockMock;
import au.com.mineauz.minigames.objects.TestPlayer;
import be.seeseemelk.mockbukkit.Coordinate;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMockFactory;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 8/07/2018.
 */

public class MinigamesTest {
    
    private ServerMock server;
    private Minigames plugin;
    private PlayerMock player;
    private Minigame game;
    private WorldMock world;
    private SQLiteDataSource datasource;
    private Location spawn;
    private Location lobby;
    private Location end;
    private Location start;
    private Location quit;
    private SignBlockMock flag;
    private SignBlockMock captureFlag;

    @Before
    public void Setup(){
        try {
            server = MockBukkit.mock();
        }catch (Exception e){
            server = MockBukkit.getMock();
        }
        server.setPlayerFactory(new PlayerMockFactory(server,TestPlayer.class));
        ((ConsoleCommandSenderMock)server.getConsoleSender()).setOutputOnSend(true);
        world = MockBukkit.getMock().addSimpleWorld("GAMES");
        Logger log = Logger.getAnonymousLogger();
        log.setLevel(Level.ALL);
        plugin = MockBukkit.load(Minigames.class);
        spawn =world.getSpawnLocation();
        plugin.toggleDebug();
        plugin.setLog(log);
        world = (WorldMock) MockBukkit.getMock().getWorld("GAMES");
        start = new Location(world,10,10,10);
        lobby = new Location(world,0,5,0);
        end = new Location(world,0,10,0);
        quit = new Location(world,0,20,0);
        
        game = TestHelper.createMinigame(plugin,world,MinigameType.MULTIPLAYER, GameMechanics.MECHANIC_NAME.CTF);
        Map<Integer,String> lines =  new HashMap<>();
        lines.put(0, ChatColor.GREEN +"Flag");
        lines.put(1,ChatColor.GRAY+"Neutral");
        lines.put(2,"");
        lines.put(3,"");
        BlockMock flag = TestHelper.createSignBlock(lines,world);
        lines.put(0,"");
        lines.put(1, ChatColor.GREEN +"Capture");
        lines.put(2,ChatColor.GRAY+"Neutral");
        lines.put(3,"");
        BlockMock captureFlag = TestHelper.createSignBlock(lines,world);
        world.createBlock(new Coordinate(10,40,10),flag);
        world.createBlock(new Coordinate(10,50,10),captureFlag);
        start = game.getStartLocations().get(0);
        quit = game.getQuitPosition();
        lobby = game.getLobbyPosition();
        end = game.getEndPosition();
        TeamsModule tmod = (TeamsModule) game.getModule("Teams");
        tmod.addTeam(TeamColor.BLUE);
        tmod.addTeam(TeamColor.RED);
        MinigameModule module = game.getModule("LobbySettings");
        if(module != null) {
            LobbySettingsModule lMod = (LobbySettingsModule) module;
            lMod.setTeleportOnPlayerWait(true);
            lMod.setTeleportOnStart(true);
        }
        player = server.addPlayer();
        player.setLocation(new Location(world,0,0,0));
        player.setOutputOnSend(true);

    }

    private void createMinigame(){
        game.setType(MinigameType.MULTIPLAYER);
        game.setMechanic(GameMechanics.MECHANIC_NAME.CTF.toString());
        game.setDeathDrops(true);
        game.setEnabled(true);
        game.setStartWaitTime(5);
        game.setTimer(5);
        game.setMaxScore(3);
        game.setMaxPlayers(2);
        TeamsModule tmod = (TeamsModule) game.getModule("Teams");
        tmod.addTeam(TeamColor.BLUE);
        tmod.addTeam(TeamColor.RED);
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
        player3.assertLocation(lobby,0);
        plugin.getPlayerManager().startMPMinigame(game);
        server.getScheduler().performTicks(600);
        player.assertLocation(start,0);
        player3.assertLocation(start,0);
        CTFMechanic mechanic = (CTFMechanic) game.getMechanic();
        PlayerInteractEvent event = new PlayerInteractEvent(player3,Action.RIGHT_CLICK_BLOCK,player3.getItemInHand(),flag,BlockFace.EAST);
        mechanic.takeFlag(event);
        PlayerInteractEvent event2 = new PlayerInteractEvent(player3,Action.RIGHT_CLICK_BLOCK,player3.getItemInHand(),captureFlag,BlockFace.EAST);
        mechanic.takeFlag(event2);
        
    }
    
    @After
    public void TearDown(){
        try{
            MockBukkit.unload();
        }catch (Exception e){
        }
    }
}