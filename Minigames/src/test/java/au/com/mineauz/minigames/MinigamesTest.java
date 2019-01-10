package au.com.mineauz.minigames;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.CTFMechanic;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.objects.MockSign;
import au.com.mineauz.minigames.objects.SignBlockMock;
import au.com.mineauz.minigames.objects.TestPlayer;
import be.seeseemelk.mockbukkit.Coordinate;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMockFactory;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;


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

        server = MockBukkit.mock();
        server.setPlayerFactory(new PlayerMockFactory(server,TestPlayer.class));
        ((ConsoleCommandSenderMock)server.getConsoleSender()).setOutputOnSend(true);
        MockBukkit.getMock().addSimpleWorld("GAMES");
        Logger log = Logger.getAnonymousLogger();
        log.setLevel(Level.ALL);
        plugin = MockBukkit.load(Minigames.class);
        plugin.toggleDebug();
        plugin.setLog(log);
        world = (WorldMock) MockBukkit.getMock().getWorld("GAMES");
        spawn =world.getSpawnLocation();
        start = new Location(world,10,10,10);
        lobby = new Location(world,0,5,0);
        end = new Location(world,0,10,0);
        quit = new Location(world,0,20,0);
        createMinigame();
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
        MaterialData data = new MaterialData(Material.SIGN,(byte)0);
        MockSign sign = new MockSign(data,true);
        sign.setLine(1, ChatColor.GREEN +"Flag");
        sign.setLine(2,ChatColor.GRAY+"Neutral");
        BlockData bData = new BlockData() {
            @Override
            public Material getMaterial() {
                return Material.SIGN;
            }
    
            @Override
            public String getAsString() {
                return null;
            }
    
            @Override
            public String getAsString(boolean b) {
                return "SIGN";
            }
    
            @Override
            public BlockData merge(BlockData blockData) {
                return this;
            }
    
            @Override
            public boolean matches(BlockData blockData) {
                return true;
            }
    
            @Override
            public BlockData clone() {
                return this;
            }
        };
        flag = new SignBlockMock(Material.SIGN,new Location(world,10,40,10),sign,bData);
        MockSign captureSign = new MockSign(data,true);
        sign.setLine(2, ChatColor.GREEN +"Capture");
        sign.setLine(3,ChatColor.GRAY+"Neutral");
        captureFlag = new SignBlockMock(Material.SIGN, new Location(world,10,50,10),captureSign,bData);
        world.createBlock(new Coordinate(10,40,10),flag);
        world.createBlock(new Coordinate(10,50,10),captureFlag);
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
    
    public void onQuitMinigame(){
        plugin.getPlayerManager().addMinigamePlayer(player);
        MinigamePlayer mplayer = plugin.getPlayerManager().getMinigamePlayer(player.getUniqueId());
        plugin.getPlayerManager().joinMinigame(mplayer, game, false, 0D);
    
    
    }
}