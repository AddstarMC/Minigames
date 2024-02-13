package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeleteCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "delete";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_DELETE_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_DELETE_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.delete";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args.length > 0) {
            Minigame mgm = PLUGIN.getMinigameManager().getMinigame(args[0]);

            if (mgm != null) {
                File save = new File(PLUGIN.getDataFolder() + "/minigames/" + mgm.getName());
                if (save.exists() && save.isDirectory()) {
                    try {
                        FileUtils.deleteDirectory(save);
                    } catch (IOException e) {
                        PLUGIN.getComponentLogger().warn("couldn't delete files for minigame " + save.getPath() + ". Still going to try to delete from config.");
                    }

                    List<String> ls = PLUGIN.getConfig().getStringList("minigames");
                    ls.remove(mgm.getName());
                    PLUGIN.getConfig().set("minigames", ls);
                    PLUGIN.getMinigameManager().removeMinigame(mgm.getName());
                    PLUGIN.saveConfig();
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_DELETE_SUCCESS,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName()));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> mgs = new ArrayList<>(Minigames.getPlugin().getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[0]);
        }
        return null;
    }

}
