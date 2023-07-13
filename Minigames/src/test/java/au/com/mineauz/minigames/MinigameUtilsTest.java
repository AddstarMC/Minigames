package au.com.mineauz.minigames;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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