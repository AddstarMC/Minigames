package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HintCommand implements ICommand {

    @Override
    public String getName() {
        return "hint";
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
        return "Hints a player to the whereabouts of a treasure hunt treasure. If more than one, the name of the Minigame must be entered. (Will be listed)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame hint [Minigame Name]"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to view a hint!";
    }

    @Override
    public String getPermission() {
        return "minigame.treasure.hint";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        MinigamePlayer player = plugin.getPlayerManager().getMinigamePlayer((Player) sender);
        if (args != null) {
            Minigame mgm = plugin.getMinigameManager().getMinigame(args[0]);

            if (mgm != null && mgm.getMinigameTimer() != null && mgm.getType() == MinigameType.GLOBAL &&
                    mgm.getMechanicName().equals("treasure_hunt")) {
                TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(mgm);
                if (thm.hasTreasureLocation() && !thm.isTreasureFound()) {
                    thm.getHints(player);
                } else {
                    player.sendInfoMessage(ChatColor.GRAY + mgm.getName(false) + " is currently not running.");
                }
            } else if (mgm == null || mgm.getType() != MinigameType.GLOBAL) {
                player.sendMessage(ChatColor.RED + "There is no treasure hunt running by the name \"" + args[0] + "\"", MinigameMessageType.ERROR);
            }
        } else {
            List<Minigame> mgs = new ArrayList<>();
            for (Minigame mg : plugin.getMinigameManager().getAllMinigames().values()) {
                if (mg.getType() == MinigameType.GLOBAL && mg.getMechanicName().equals("treasure_hunt")) {
                    mgs.add(mg);
                }
            }
            if (!mgs.isEmpty()) {
                if (mgs.size() > 1) {
                    player.sendInfoMessage(ChatColor.LIGHT_PURPLE + "Currently running Treasure Hunts:");
                    String treasures = "";
                    for (int i = 0; i < mgs.size(); i++) {
                        treasures += mgs.get(i).getName(false);
                        if (i != mgs.size() - 1) {
                            treasures += ", ";
                        }
                    }
                    player.sendInfoMessage(ChatColor.GRAY + treasures);
                } else {
                    TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(mgs.get(0));
                    if (thm.hasTreasureLocation() && !thm.isTreasureFound()) {
                        thm.getHints(player);
                    } else {
                        player.sendInfoMessage(ChatColor.GRAY + mgs.get(0).getName(false) + " is currently not running.");
                    }
                }
            } else if (mgs.isEmpty()) {
                player.sendInfoMessage(ChatColor.LIGHT_PURPLE + "There are no Treasure Hunt minigames currently running.");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            List<String> mgs = new ArrayList<>();
            for (Minigame mg : plugin.getMinigameManager().getAllMinigames().values()) {
                if (mg.getType() == MinigameType.GLOBAL && mg.getMechanicName().equals("treasure_hunt"))
                    mgs.add(mg.getName(false));
            }
            return MinigameUtils.tabCompleteMatch(mgs, args[0]);
        }
        return null;
    }

}
