package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetRandomizeChestsCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "randomizechests";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"randomisechests", "randomchests", "rchests"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_RNGCHEST_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_RNGCHEST_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.randomizechests";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args.length >= 2) {
                if (args[0].matches("-?[0-9]+")) {
                    if (args[1].matches("-?[0-9]+")) {
                        int min = Integer.parseInt(args[0]);
                        int max = Integer.parseInt(args[1]);

                        if (minigame.setChestRandoms(min, max)) {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_RNGCHEST_ENABLED,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MIN.getKey(), String.valueOf(min)),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(max)));
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_RNGCHEST_DISABLED,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                        }

                        return true;
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
            } // not enough args
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }

}
