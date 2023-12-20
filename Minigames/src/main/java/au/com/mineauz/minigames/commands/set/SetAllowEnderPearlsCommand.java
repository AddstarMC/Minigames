package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.BooleanUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetAllowEnderPearlsCommand implements ICommand {

    @Override
    public String getName() {
        return "allowenderpearls";
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
    public Component getDescription() {
        return "Sets whether players can use ender pearls in a Minigame.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> allowenderpearls <true / false>"};
    }

    @Override
    public String getPermission() {
        return "minigame.set.allowenderpearls";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            Boolean bool = BooleanUtils.toBooleanObject(args[0]);

            if (bool != null) {
                minigame.setAllowEnderPearls(bool);

                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_ENDERPERLS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                        Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                bool ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)));
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOBOOL,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
            }
            return true;
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
