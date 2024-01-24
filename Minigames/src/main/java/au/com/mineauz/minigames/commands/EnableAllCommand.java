package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnableAllCommand implements ICommand {

    @Override
    public String getName() {
        return "enableall";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"enall"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return MinigameMessageManager.getMinigamesMessage("command.enableAll.desc");
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame enableall [ExcludedMinigame]..."};
    }

    @Override
    public String getPermissionMessage() {
        return MinigameMessageManager.getMinigamesMessage("command.enable.noPerm");
    }

    @Override
    public String getPermission() {
        return "minigame.enableall";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        MinigameManager mdata = Minigames.getPlugin().getMinigameManager();
        List<Minigame> minigames = new ArrayList<>(mdata.getAllMinigames().values());
        if (args != null) {
            for (String arg : args) {
                if (mdata.hasMinigame(arg))
                    minigames.remove(mdata.getMinigame(arg));
                else
                    MinigameMessageManager.sendMessage(sender, MinigameMessageType.ERROR, null, "command.enable.notfound", arg);
            }
        }
        for (Minigame mg : minigames) {
            mg.setEnabled(true);
        }
        MinigameMessageManager.sendMessage(sender, MinigameMessageType.INFO, null, "command.enable.resultnum", minigames.size());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }

}
