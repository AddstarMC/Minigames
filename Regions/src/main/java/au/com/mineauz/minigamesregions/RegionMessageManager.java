package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.LangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

public class RegionMessageManager {
    private final static String LANG_KEY = "minigames-regions";

    public static void register(){
        MinigameMessageManager.registerMessageFile(LANG_KEY, ResourceBundle.getBundle("minigames_regions"));
    }

    public static Component getMessage(RegionLangKey key, TagResolver... resolvers) {
        return MinigameMessageManager.getMessage(LANG_KEY, key, resolvers);
    }

    public enum RegionLangKey implements LangKey {
        COMMAND_NODE_ADDED("command.node.addedNode"),
        COMMAND_NODE_EXISTS("command.node.nodeExists"),
        ;

        private final @NotNull String path;

        RegionLangKey(@NotNull String path){
            this.path = path;
        }

        @Override
        public @NotNull String getPath() {
            return path;
        }
    }
}
