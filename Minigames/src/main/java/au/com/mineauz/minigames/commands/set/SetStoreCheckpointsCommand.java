package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetStoreCheckpointsCommand implements ICommand {

    @Override
    public String getName() {
        return "storecheckpoints";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"storecp", "spc"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return MinigameMessageManager.getMinigamesMessage("command.checkpoint.saving.description");
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> storecheckpoints <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return MinigameMessageManager.getMinigamesMessage("command.checkpoint.saving.nopermission");
    }

    @Override
    public String getPermission() {
        return "minigame.set.storecheckpoints";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setSaveCheckpoint(bool);
            MinigameMessageManager.sendMessage(sender, MinigameMessageType.INFO, null, "command.checkpoint.saving.toggle", Boolean.toString(bool), minigame.getName(true));
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
