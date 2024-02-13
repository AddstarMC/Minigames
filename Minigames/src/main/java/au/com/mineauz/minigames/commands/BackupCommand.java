package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.recorder.RecorderData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BackupCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "backup";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_BACKUP_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_BACKUP_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.backup";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args.length > 0) {
            Minigame minigame = Minigames.getPlugin().getMinigameManager().getMinigame(args[0]);

            if (minigame != null) {
                if (!minigame.getRegenRegions().isEmpty()) {
                    if (args.length == 1) {
                        if (minigame.getPlayers().isEmpty()) {
                            minigame.setState(MinigameState.REGENERATING);
                            Minigames.getPlugin().getMinigameManager().addRegenDataToRecorder(minigame);
                            RecorderData recorderData = minigame.getRecorderData();
                            recorderData.saveAllBlockData();

                            recorderData.clearRestoreData();

                            minigame.setState(MinigameState.IDLE);

                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_BACKUP_SUCCESS,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_BACKUP_ERROR_PLAYERS,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                        }
                    } else if (args.length == 2 && args[1].equalsIgnoreCase("restore")) {
                        if (minigame.getPlayers().isEmpty()) {
                            if (minigame.getRecorderData().restoreBlockData()) {
                                minigame.getRecorderData().restoreBlocks();

                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_BACKUP_RESTORE_SUCCESS,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_BACKUP_ERROR_NOBACKUP,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                            }
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_BACKUP_ERROR_PLAYERS,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                        }
                    } else { // unknown argument
                        return false;
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_BACKUP_ERROR_NOREGENREGION,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[0]));
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(new ArrayList<>(Minigames.getPlugin().getMinigameManager().getAllMinigames().keySet()), args[0]);
        } else if (args.length == 2) {
            return MinigameUtils.tabCompleteMatch(List.of("restore"), args[1]);
        }
        return null;
    }

}
