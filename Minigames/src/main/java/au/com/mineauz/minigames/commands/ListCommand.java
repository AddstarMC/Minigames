package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "list";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_LIST_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_LIST_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.list";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        Component result = Component.join(JoinConfiguration.commas(true),
                PLUGIN.getMinigameManager().getAllMinigames().values().stream().
                        //filter permission
                                filter(mgm -> (!mgm.getUsePermissions() ||
                                sender.hasPermission("minigame.join." + mgm.getName().toLowerCase()))).
                        // map to name
                                map(mgm -> {
                            Component name = mgm.getDisplayName();

                            // color indicates status of minigame
                            if (!mgm.isEnabled()) {
                                return name.color(NamedTextColor.GRAY);
                            } else if (mgm.getType() == MinigameType.GLOBAL) {
                                return name.color(NamedTextColor.DARK_GREEN);
                            } else {
                                return switch (mgm.getState()) {
                                    case ENDED, IDLE -> name.color(NamedTextColor.GREEN);
                                    case WAITING -> name.color(NamedTextColor.BLUE);
                                    case STARTED -> name.color(NamedTextColor.DARK_GREEN);
                                    case ENDING, STARTING, REGENERATING, OCCUPIED -> name.color(NamedTextColor.GOLD);
                                };
                            }
                        }).toList());

        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_LIST_LIST,
                Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), result));
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }
}
