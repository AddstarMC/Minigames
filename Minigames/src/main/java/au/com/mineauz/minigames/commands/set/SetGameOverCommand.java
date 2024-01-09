package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.GameOverModule;
import au.com.mineauz.minigames.minigame.modules.MgModules;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.BooleanUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SetGameOverCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "gameover";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MinigameLangKey.COMMAND_SET_GAMEOVER_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MinigameLangKey.COMMAND_SET_GAMEOVER_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.gameover";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            GameOverModule gameOverModule = GameOverModule.getMinigameModule(minigame);

            if (gameOverModule != null) {
                if (args[0].equalsIgnoreCase("timer") && args.length == 2) {
                    Long millis = MinigameUtils.parsePeriod(args[1]);
                    if (millis != null) {
                        gameOverModule.setTimer(TimeUnit.MICROSECONDS.toSeconds(millis));
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_GAMEOVER_TIME,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                                Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofMillis(millis))));
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTTIME,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("invincible") && args.length == 2) {
                    Boolean bool = BooleanUtils.toBooleanObject(args[1]);
                    if (bool != null) {
                        gameOverModule.setInvincible(bool);

                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_GAMEOVER_INVINCIBLE,
                                Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(),
                                        MinigameMessageManager.getMgMessage(bool ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)),
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                        return true;
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTBOOL,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                    }
                } else if (args[0].equalsIgnoreCase("humiliation") && args.length == 2) {
                    Boolean bool = BooleanUtils.toBooleanObject(args[1]);
                    if (bool != null) {
                        gameOverModule.setHumiliationMode(bool);

                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_GAMEOVER_HUMILIATION,
                                Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(),
                                        MinigameMessageManager.getMgMessage(bool ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)),
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                        return true;
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTBOOL,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                    }
                } else if (args[0].equalsIgnoreCase("interact") && args.length == 2) {
                    Boolean bool = BooleanUtils.toBooleanObject(args[1]);
                    if (bool != null) {
                        gameOverModule.setInteractAllowed(bool);

                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_GAMEOVER_INTERACTION,
                                Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(),
                                        MinigameMessageManager.getMgMessage(bool ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)),
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTBOOL,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                    }
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTGAMEMECHANIC,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                        Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), MgModules.GAME_OVER.getName()));
            }

        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(List.of("timer", "invincible", "humiliation", "interact"), args[0]);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("invincible") ||
                    args[0].equalsIgnoreCase("humiliation") ||
                    args[0].equalsIgnoreCase("interact")) {
                return MinigameUtils.tabCompleteMatch(List.of("true", "false"), args[1]);
            } else if (args[0].equalsIgnoreCase("timer")) {
                return List.of("s", "m", "h");
            }
        }
        return null;
    }

}
