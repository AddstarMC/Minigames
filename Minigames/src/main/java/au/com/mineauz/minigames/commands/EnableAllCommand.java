package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnableAllCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "enableall";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"enall"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage("command.enableAll.desc");
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame enableall [ExcludedMinigame]..."};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.enableall";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
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
    public List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                      String alias, @NotNull String @NotNull [] args) {
        List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }

}
