package au.com.mineauz.minigames.managers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 22/07/2020.
 */
public class MessageManagerTest {

    @Before
    public void setUp() throws Exception {
        MessageManager.setLogger(Logger.getAnonymousLogger());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMessageBundleLoading() {
        MessageManager.registerCoreLanguage(new File("test.messages"),Locale.US);
        String message = MessageManager.getMessage(null,"player.bet.plyMsg");
        assertEquals("You have placed your bet, good luck!",message);
        MessageManager.deRegisterAll("minigames");
        Locale.setDefault(Locale.CANADA);
        MessageManager.registerCoreLanguage(new File("test.messages"),Locale.FRANCE);
        message = MessageManager.getMessage(null,"player.bet.plyMsg");
        assertEquals("You have placed your bet, good luck!",message);

    }
    @Test
    public void testMessageBundleLoadingAU() {
        MessageManager.registerCoreLanguage(new File("test.messages"),Locale.forLanguageTag("en-AU"));
        String message = MessageManager.getMessage(null,"player.bet.plyMsg");
        assertEquals("You have placed your wager, good luck!",message);
    }
}