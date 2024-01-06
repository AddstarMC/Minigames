package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//import au.com.mineauz.minigames.StoredPlayerCheckpoints;

public class RevertCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "revert";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"r"};
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameUtils.getLang("command.revert.description");
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame revert"};
    }

    @Override
    public String getPermissionMessage() {
        return MinigameUtils.getLang("command.revert.noPermission");
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.revert";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        MinigamePlayer player = plugin.getPlayerManager().getMinigamePlayer((Player) sender);

        if (player.isInMinigame()) {
            plugin.getPlayerManager().revertToCheckpoint(player);
        }
//        else if(plugin.playerManager.hasStoredPlayerCheckpoint(player)){
//            StoredPlayerCheckpoints spc = plugin.playerManager.getPlayersStoredCheckpoints(player);
//            if(spc.hasGlobalCheckpoint()){
//                player.getPlayer().teleport(spc.getGlobalCheckpoint());
//            }
//        }
        else {
            player.sendMessage(MinigameUtils.getLang("command.revert.noGlobal"), MinigameMessageType.ERROR);
        }
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         String alias, @NotNull String @NotNull [] args) {
        return null;
    }

}
