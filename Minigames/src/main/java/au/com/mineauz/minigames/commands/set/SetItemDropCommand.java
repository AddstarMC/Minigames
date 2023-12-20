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
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetItemDropCommand implements ICommand {

    @Override
    public String getName() {
        return "itemdrop";
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
        return "Sets whether players can drop items on the ground either after they die or by dropping it themselves. (Both disabled by default)";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"player", "death"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> itemdrop player <true/false>",
                "/minigame set <Minigame> itemdrop death <true/false>"};
    }

    @Override
    public String getPermission() {
        return "minigame.set.itemdrop";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("player") && args.length >= 2) {
                Boolean bool = BooleanUtils.toBooleanObject(args[1]);

                if (bool != null) {
                    minigame.setItemDrops(bool);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_ITEMSDROP_DROP,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                            Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                    bool ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOBOOL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("death") && args.length >= 2) {
                Boolean bool = BooleanUtils.toBooleanObject(args[1]);

                if (bool != null) {
                    minigame.setDeathDrops(bool);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_ITEMSDROP_DEATH,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                            Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                    bool ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOBOOL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
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
