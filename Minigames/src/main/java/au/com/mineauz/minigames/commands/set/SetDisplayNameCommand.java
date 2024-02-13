package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetDisplayNameCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "displayname";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"dispname", "dname"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_DISPLAYNAME_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_DISPLAYNAME_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.displayname";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            String name = String.join(" ", args);

            if (name.equalsIgnoreCase("null")) {
                minigame.setDisplayName(null);
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_DISPLAYNAME_REMOVED,
                        Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getDisplayName()));
            } else {
                minigame.setDisplayName(MiniMessage.miniMessage().deserialize(name));
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_DISPLAYNAME_SUCCESS,
                        Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getDisplayName()),
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), name));
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }

}
