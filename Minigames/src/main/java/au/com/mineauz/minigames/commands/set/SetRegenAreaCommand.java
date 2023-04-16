package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetRegenAreaCommand implements ICommand {

    @Override
    public String getName() {
        return "regenarea";
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
        return "Creates and deletes regeneration regions. This only needs to be used for Minigames that have things like leaf decay, fire, tnt etc." +
                " If the Minigame has anything that the player doesn't directly interact with that breaks, this should be used.";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"select", "create", "delete", /*"modify"*/};
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame set <Minigame> regenarea select <1/2>",
                "/minigame set <Minigame> regenarea create <name>",
                "/minigame set <Minigame> regenarea delete <name>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the regen area of a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.regenarea";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (sender instanceof Player player) {
                MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);

                if (args.length == 2) {
                    switch (args[0].toLowerCase()) {
                        case "select" -> {
                            Location placerLoc = mgPlayer.getLocation();
                            placerLoc.subtract(0, 1, 0);

                            if (args[1].equals("1")) {
                                Location p2 = mgPlayer.getSelectionPoints()[1];
                                mgPlayer.clearSelection();
                                mgPlayer.setSelection(placerLoc, p2);

                                mgPlayer.sendInfoMessage(Component.text("Point 1 selected", NamedTextColor.GRAY));
                            } else {
                                Location p2 = mgPlayer.getSelectionPoints()[0];
                                mgPlayer.clearSelection();
                                mgPlayer.setSelection(p2, placerLoc);

                                mgPlayer.sendInfoMessage(Component.text("Point 2 selected", NamedTextColor.GRAY));
                            }
                            return true;
                        }
                        case "create" -> {
                            if (mgPlayer.hasSelection()) {
                                String name = args[1];
                                minigame.setRegenRegion(new MgRegion(name, mgPlayer.getSelectionPoints()[0], mgPlayer.getSelectionPoints()[1]));

                                mgPlayer.clearSelection();

                                mgPlayer.sendInfoMessage(Component.text("Created new regen region for " + minigame.getName(false) + " named " + name, NamedTextColor.GRAY));
                            } else {
                                mgPlayer.sendInfoMessage(Component.text("You have not made a selection!", NamedTextColor.RED));
                            }
                            return true;
                        }
                        case "delete" -> {
                            if (minigame.removeRegenRegion(args[1])) {
                                mgPlayer.sendInfoMessage(Component.text("Removed the regen region named " + args[1] + " from " + minigame.getName(false), NamedTextColor.GRAY));
                            } else {
                                mgPlayer.sendInfoMessage(Component.text("No regen region by the name " + args[1] + " was found in " + minigame.getName(false), NamedTextColor.GRAY));
                            }
                            return true;
                        }
                    }
                }
            } else {
                sender.sendMessage(Component.text("You have to be a player.", NamedTextColor.RED));
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {

        if (args.length == 1) {
            List<String> tab = new ArrayList<>();
            tab.add("select");
            tab.add("create");
            tab.add("delete");
            return MinigameUtils.tabCompleteMatch(tab, args[0]);
        } else if (args.length == 2) {
            List<String> tab = new ArrayList<>();
            if (args[0].equalsIgnoreCase("select")) {
                tab.add("1");
                tab.add("2");
            } else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("delete")) {
                for (MgRegion region : minigame.getRegenRegions()) {
                    tab.add(region.getName());
                }
            }
            return MinigameUtils.tabCompleteMatch(tab, args[1]);
        }
        return null;
    }
}
