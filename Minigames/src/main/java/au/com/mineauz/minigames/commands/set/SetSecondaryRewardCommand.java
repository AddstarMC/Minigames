package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SetSecondaryRewardCommand implements ICommand {
    private final Pattern MONEY_PATTERN = Pattern.compile("\\$-?(\\d+(\\.\\d+)?)");

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
        return """
                Sets the players secondary reward for completing the Minigame after the first time.This can be one item or a randomly selected item added to the rewards, depending on its defined rarity.\s
                Possible rarities are: very_common, common, normal, rare and very_rare
                NOTE: This can only be used on minigames using the standard reward scheme""";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> reward2 <Item Name> [Quantity] [Rarity]",
                "/minigame set <Minigame> reward2 $<Money Amount> [Rarity]"
        };
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
