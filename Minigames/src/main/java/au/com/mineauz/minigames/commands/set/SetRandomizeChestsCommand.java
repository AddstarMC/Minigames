package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.BooleanUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetRandomizeChestsCommand implements ICommand {

    @Override
    public String getName() {
        return "randomizechests";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"randomisechests", "randomchests", "rchests"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return """
                All chests in a Minigame will have their items randomized as soon as a Minigame player opens one.
                Items will only be randomized once and will be reverted to their default state after the game ends. A new game will result in different items again. The number of items that are set in the chests are defined in this command.
                (Defaults: false, min: 5, max: 10)""";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> randomizechests <true/false>",
                "/minigame set <Minigame> randomizechests <minValue> <maxValue>"};
    }

    @Override
    public String getPermission() {
        return "minigame.set.randomizechests";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args.length == 1) {
                Boolean bool = BooleanUtils.toBooleanObject(args[0]);

                if (bool != null) {
                    minigame.setRandomizeChests(bool);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_RNGCHEST_SIMPLE,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                            Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                    bool ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOBOOL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
                return true;
            } else if (args.length >= 2 && args[0].matches("[0-9]+") && args[1].matches("[0-9]+")) {
                int min = Integer.parseInt(args[0]);
                int max = Integer.parseInt(args[1]);
                minigame.setMinChestRandom(min);
                minigame.setMaxChestRandom(max);

                sender.sendMessage(ChatColor.GRAY + "Chest randomization set for " + minigame + ". Minimum: " + min + ", Maximum: " + max);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(List.of("true", "false"), args[0]);
        return null;
    }

}
