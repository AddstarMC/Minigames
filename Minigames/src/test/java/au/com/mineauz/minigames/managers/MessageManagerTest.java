package au.com.mineauz.minigames.managers;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.Locale;

public class MessageManagerTest {

    @Test
    public void testMessageBundleLoading() {
        MinigameMessageManager.registerCoreLanguage(new File("test.messages"), Locale.US);
        String message = MinigameMessageManager.getMessage(null, "player.bet.plyMsg");
        Assertions.assertEquals("You have placed your bet, good luck!", message);
        MinigameMessageManager.deRegisterMessageFile("minigames");
        Locale.setDefault(Locale.CANADA);
        MinigameMessageManager.registerCoreLanguage(new File("test.messages"), Locale.FRANCE);
        message = MinigameMessageManager.getMessage(null, "player.bet.plyMsg");
        Assertions.assertEquals("You have placed your bet, good luck!", message);
    }

    @Test
    public void testMessageBundleLoadingAU() {
        MinigameMessageManager.registerCoreLanguage(new File("test.messages"), Locale.forLanguageTag("en-AU"));
        String message = MinigameMessageManager.getMessage(null, "player.bet.plyMsg");
        Assertions.assertEquals("You have placed your wager, good luck!", message);
    }
}