package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetMaxHeightCommand implements ICommand {

    @Override
    public String getName() {
        return "maxheight";
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
        return MinigameMessageManager.getMinigamesMessage("command.treasures.setMaxHeight.desc");
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> maxheight <Number>"};
    }

    @Override
    public String getPermissionMessage() {
        return MinigameMessageManager.getMinigamesMessage("command.treasures.setMaxHeight.noPerm");
    }

    @Override
    public String getPermission() {
        return "minigame.set.maxheight";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int num = Integer.parseInt(args[0]);
                TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(minigame);
                thm.setMaxHeight(num);
                MinigameMessageManager.sendMessage(sender, MinigameMessageType.INFO, null, "command.treasures.setMaxHeight.set", minigame.toString(), num);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
