package au.com.mineauz.minigames;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 8/07/2018.
 */

public class MinigamesTest {
    
    private ServerMock server;
    private Minigames plugin;
    
    @Before
    public void Setup(){
        server = MockBukkit.mock();
        String ver = server.getBukkitVersion();
        plugin = MockBukkit.load(Minigames.class);
        Logger log = Logger.getAnonymousLogger();
        plugin.setLog(log);
    }
    
    @Test
    public void onEnableTest(){
        plugin.onEnable();
    }

}