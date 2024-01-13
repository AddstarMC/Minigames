package au.com.mineauz.minigames.managers;

import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import net.kyori.adventure.text.Component;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.Locale;

public class MessageManagerTest {

    @Test
    public void testMessageBundleLoading() {
        MinigameMessageManager.registerCoreLanguage(new File("test.messages"), Locale.US);
        Component message = MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_BET_PLAYERMSG);
        Assertions.assertEquals("You have placed your bet, good luck!", message); //todo
        MinigameMessageManager.deRegisterMessageFile("minigames");
        Locale.setDefault(Locale.CANADA);
        MinigameMessageManager.registerCoreLanguage(new File("test.messages"), Locale.FRANCE);
        message = MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_BET_PLAYERMSG);
        Assertions.assertEquals("You have placed your bet, good luck!", message); //todo
    }

    @Test
    public void testMessageBundleLoadingAU() {
        MinigameMessageManager.registerCoreLanguage(new File("test.messages"), Locale.forLanguageTag("en-AU"));
        Component message = MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_BET_PLAYERMSG);
        Assertions.assertEquals("You have placed your wager, good luck!", message); //todo
    }
}