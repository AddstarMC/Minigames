package au.com.mineauz.minigames.backend.sqlite;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.TestUtilities;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 21/06/2017.
 */
public class SQLiteBackendTest {

    Logger log;
    ConfigurationSection config;

    @Before
    public void Setup(){
        TestUtilities utilities = new TestUtilities();
        log = utilities.getLogger();
        config = utilities.createTestConfig().getConfigurationSection("backend");
        config.set("type","sqlite");
    }



    @Test
    public void initialize() throws Exception {
        SQLiteBackend backend = new SQLiteBackend(log);
        backend.setDatabase(File.createTempFile("miningame","db"));
        assertTrue(backend.initialize(config, false));
    }

    @Test
    public void shutdown() throws Exception {
    }

    @Test
    public void clean() throws Exception {
    }

}