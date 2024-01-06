package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreateCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "create";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Creates a Minigame using the specified name. Optionally, adding the type at the end will " +
                "set the type of minigame straight up. (Type defaults to SINGLEPLAYER)";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame create <Minigame> [type]"};
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.create";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame, @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            Player player = (Player) sender;
            String mgmName = args[0];
            if (MinigameUtils.sanitizeYamlString(mgmName) == null) {
                throw new CommandException("Name is not valid for use in a Config.");
            }
            if (!plugin.getMinigameManager().hasMinigame(mgmName)) {
                MinigameType type = MinigameType.SINGLEPLAYER;
                if (args.length >= 2) {
                    if (MinigameType.hasValue(args[1].toUpperCase())) {
                        type = MinigameType.valueOf(args[1].toUpperCase());
                    } else {
                        MinigameMessageManager.sendMessage(player, MinigameMessageType.ERROR, null, "command.create.noName", args[1]);
                    }
                }
                Minigame mgm = new Minigame(mgmName, type, player.getLocation());
                MinigameMessageManager.sendMessage(player, MinigameMessageType.INFO, null, "command.create.success", args[0]);
                List<String> mgs;
                if (plugin.getConfig().contains("minigames")) {
                    mgs = plugin.getConfig().getStringList("minigames");
                } else {
                    mgs = new ArrayList<>();
                }
                mgs.add(mgmName);
                plugin.getConfig().set("minigames", mgs);
                plugin.saveConfig();

                mgm.saveMinigame();
                plugin.getMinigameManager().addMinigame(mgm);
            } else {
                MinigameMessageManager.sendMessage(sender, MinigameMessageType.ERROR, null, "command.create.nameexists");
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         String alias, @NotNull String @NotNull [] args) {
        if (args.length == 2) {
            List<String> types = new ArrayList<>(MinigameType.values().length);
            for (MinigameType type : MinigameType.values()) {
                types.add(type.toString().toLowerCase());
            }
            return MinigameUtils.tabCompleteMatch(types, args[1]);
        }
        return null;
    }
}
