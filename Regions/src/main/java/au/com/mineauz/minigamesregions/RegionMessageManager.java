package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

public class RegionMessageManager {
    private final static String BUNDLE_KEY = "minigames-regions";

    public static void register(){
        MinigameMessageManager.registerMessageFile(BUNDLE_KEY, ResourceBundle.getBundle("minigames_regions"));
    }

    public static Component getMessage(RegionLangKey key, TagResolver... resolvers) {
        return MinigameMessageManager.getMessage(BUNDLE_KEY, key, resolvers);
    }

    public static String getBundleKey() {
        return BUNDLE_KEY;
    }

    public static void debugMessage(@NotNull String message) { //todo
        if (Minigames.getPlugin().isDebugging()) {
            Main.getPlugin().getLogger().info(ChatColor.RED + "[Debug] " + ChatColor.WHITE + message);
        }
    }
}
