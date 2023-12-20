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

public class SetPaintballCommand implements ICommand {

    @Override
    public String getName() {
        return "paintball";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Sets a Minigame to be in paintball mode. This lets snowballs damage players. " +
                "(Default: false, default damage: 2)";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"damage"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> paintball <true/false>",
                "/minigame set <Minigame> paintball damage <Number>"};
    }

    @Override
    public String getPermission() {
        return "minigame.set.paintball";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args.length == 1) {
                Boolean bool = BooleanUtils.toBooleanObject(args[0]);

                if (bool != null) {
                    minigame.setPaintBallMode(bool);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_PAINTBALL_MODE,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                            Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                    bool ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOBOOL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
                return true;
            } else if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("damage") && args[1].matches("[0-9]+")) {
                    minigame.setPaintBallDamage(Integer.parseInt(args[1]));
                    sender.sendMessage(ChatColor.GRAY + "Paintball damage has been set to " + args[1] + " for " + minigame);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(List.of("true", "false", "damage"), args[0]);
        return null;
    }

}
