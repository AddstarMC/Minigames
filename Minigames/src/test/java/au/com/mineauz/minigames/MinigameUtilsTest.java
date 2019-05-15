package au.com.mineauz.minigames;

import org.junit.Before;
import org.junit.Test;


import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 31/03/2018.
 */
public class MinigameUtilsTest {


    @Test
    public void TestSanitize() {
        assertNull(sanitize("EnderJump2.0"));
        assertEquals("TestGame", sanitize("TestGame"));
        assertEquals("asdasgfar231123asd__", sanitize("asdasgfar231123asd__"));
    }

    private String sanitize(String input) {
        final Pattern pattern = Pattern.compile("^[a-zA-Z\\d_]+$");
        if (!pattern.matcher(input).matches()) {
            return null;
        } else {
            return input;
        }
    }


}