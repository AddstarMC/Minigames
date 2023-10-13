package au.com.mineauz.minigames.managers;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

public class MessageManagerTest {

    @BeforeEach
    public void setUp() {
        MessageManager.setLogger(Logger.getAnonymousLogger());
    }

    @Test
    public void testMessageBundleLoading() {
        MessageManager.registerCoreLanguage(new File("test.messages"), Locale.US);
        String message = MessageManager.getMessage(null, "player.bet.plyMsg");
        Assertions.assertEquals("You have placed your bet, good luck!", message);
        MessageManager.deRegisterAll("minigames");
        Locale.setDefault(Locale.CANADA);
        MessageManager.registerCoreLanguage(new File("test.messages"), Locale.FRANCE);
        message = MessageManager.getMessage(null, "player.bet.plyMsg");
        Assertions.assertEquals("You have placed your bet, good luck!", message);
    }

    @Test
    public void testMessageBundleLoadingAU() {
        MessageManager.registerCoreLanguage(new File("test.messages"), Locale.forLanguageTag("en-AU"));
        String message = MessageManager.getMessage(null, "player.bet.plyMsg");
        Assertions.assertEquals("You have placed your wager, good luck!", message);
    }
}