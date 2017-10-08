package au.com.mineauz.minigames.backend.mysql;

import au.com.mineauz.minigames.TestUtilities;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.After;
import org.junit.Before;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 21/06/2017.
 */
public class MySQLBackendITest {

    ConfigurationSection config;
    Logger log;

    @Before
    public void setUp() throws Exception {
        TestUtilities utilities = new TestUtilities();
        log = utilities.getLogger();
        config = utilities.createTestConfig().getConfigurationSection("backend");
        config.set("type","mysql");
        config.get("host","localhost:3306");
        config.get("database","games");
        config.get("username", "games");
        config.get("password", "games");
        config.get("useSSL", "false");
    }

    //@Test todo failing
    public void initializeTest() throws Exception {
        MySQLBackend backend = new MySQLBackend(log);
        assertTrue(backend.initialize(config,true));
        Connection handler = backend.getPool().getConnection().getConnection();
        assertTrue(handler.isValid(10));
        backend.shutdown();
        assertFalse(handler.isValid(10));
        handler = backend.getPool().getConnection().getConnection();
        assertTrue(handler.isValid(10));
        PreparedStatement statement = handler.prepareStatement("SELECT 1 FROM `Players` LIMIT 0;");
        assertTrue(statement.execute());
        backend.clean();
        assertFalse(handler.isValid(10));
        config.set("useSSL ", "true");
        config.set("password", "password");
        assertFalse(backend.initialize(config,false));
    }

    @After
    public void tearDown() throws Exception {
    }

}