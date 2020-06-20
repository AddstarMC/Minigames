package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MessageManager;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class CreateCommand implements ICommand {

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Creates a Minigame using the specified name. Optionally, adding the type at the end will " +
                "set the type of minigame straight up. (Type defaults to SINGLEPLAYER)";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame create <Minigame> [type]"};
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to create Minigames!";
    }

    @Override
    public String getPermission() {
        return "minigame.create";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args) {
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
                        MessageManager.sendMessage(player, MinigameMessageType.ERROR,null,"command.createMinigame.noName",args[1]);
                    }
                }
                Minigame mgm = new Minigame(mgmName, type, player.getLocation());
                MessageManager.sendMessage(player,MinigameMessageType.INFO,null,"command.create.success",args[0]);
                List<String> mgs = null;
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
                MessageManager.sendMessage(sender,MinigameMessageType.ERROR,null,"command.create.nameexists");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
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
