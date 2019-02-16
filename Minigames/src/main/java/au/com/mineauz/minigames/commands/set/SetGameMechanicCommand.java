package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.mechanics.GameMechanicBase;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetGameMechanicCommand implements ICommand {

    @Override
    public String getName() {
        return "gamemechanic";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"scoretype", "mech", "gamemech", "mechanic"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Sets the game mechanic for a multiplayer Minigame.";
    }

    @Override
    public String[] getParameters() {
        String[] types = new String[GameMechanics.getGameMechanics().size()];
        int inc = 0;
        for (GameMechanicBase type : GameMechanics.getGameMechanics()) {
            types[inc] = type.getMechanic();
            inc++;
        }
        return types;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> gamemechanic <Parameter>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the game mechanic!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.gamemechanic";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean bool = false;
            for (String par : getParameters()) {
                if (par.equalsIgnoreCase(args[0])) {
                    bool = true;
                    break;
                }
            }

            if (bool) {
                minigame.setMechanic(args[0].toLowerCase());
                sender.sendMessage(ChatColor.GRAY + minigame.getName(false) + " game mechanic has been set to " + args[0]);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            List<String> types = new ArrayList<>(GameMechanics.getGameMechanics().size());
            for (GameMechanicBase type : GameMechanics.getGameMechanics()) {
                types.add(type.getMechanic());
            }
            return MinigameUtils.tabCompleteMatch(types, args[0]);
        }
        return null;
    }

}
