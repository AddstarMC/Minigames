package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetSecondaryRewardCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "reward2";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"secondaryreward", "sreward"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_REWARD2_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_REWARD2_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        return SetRewardCommand.processRewardCommands(sender, minigame, args, false);
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 3 || (args.length == 2 && args[0].startsWith("$"))) {
            List<String> ls = new ArrayList<>();
            for (RewardRarity r : RewardRarity.values()) {
                ls.add(r.toString());
            }
            return MinigameUtils.tabCompleteMatch(ls, args[args.length - 1]);
        }
        return null;
    }
}
