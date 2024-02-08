package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.util.*;

public class RegionMessageManager {
    private final static String BUNDLE_KEY = "minigames-regions";

    public static void register(){
        CodeSource src = Main.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            MinigameMessageManager.initLangFiles(src, BUNDLE_KEY);
        } else {
            Minigames.getCmpnntLogger().warn("Couldn't save lang files: no CodeSource!");
        }

        String tag = Minigames.getPlugin().getConfig().getString("lang", Locale.getDefault().toLanguageTag());
        Locale locale = Locale.forLanguageTag(tag.replace("_", "-"));

        // fall back if locale is undefined
        if (locale.getLanguage().isEmpty()) {
            locale = Locale.getDefault();
        }

        File file = new File(new File(Minigames.getPlugin().getDataFolder(), "lang"), "minigames_regions.properties");

        ResourceBundle langBundleMinigameRegions = null;
        if (file.exists()) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                langBundleMinigameRegions = new PropertyResourceBundle(inputStreamReader);
            } catch (IOException e) {
                Minigames.getCmpnntLogger().warn("couldn't get Ressource bundle from file " + file.getName(), e);
            }
        } else {
            try {
                langBundleMinigameRegions = ResourceBundle.getBundle(BUNDLE_KEY, locale, Minigames.getPlugin().getClass().getClassLoader(), new UTF8ResourceBundleControl());
            } catch (MissingResourceException e) {
                Minigames.getCmpnntLogger().warn("couldn't get Ressource bundle for lang " + locale.toLanguageTag(), e);
            }
        }
        if (langBundleMinigameRegions != null) {
            MinigameMessageManager.registerMessageFile(BUNDLE_KEY, langBundleMinigameRegions);
        } else {
            Minigames.getCmpnntLogger().error("No region language Resource Could be loaded...messaging will be broken");
        }
    }

    public static Component getMessage(RegionLangKey key, TagResolver... resolvers) {
        return MinigameMessageManager.getMessage(BUNDLE_KEY, key, resolvers);
    }

    public static @NotNull List<Component> getMessageList(@NotNull LangKey key, TagResolver... resolvers) {
        return MinigameMessageManager.getMessageList(BUNDLE_KEY, key, resolvers);
    }

    public static String getBundleKey() {
        return BUNDLE_KEY;
    }

    public static void debugMessage(@NotNull String message) { //todo
        if (Minigames.getPlugin().isDebugging()) {
            Main.getPlugin().getComponentLogger().info(ChatColor.RED + "[Debug] " + ChatColor.WHITE + message);
        }
    }
}
