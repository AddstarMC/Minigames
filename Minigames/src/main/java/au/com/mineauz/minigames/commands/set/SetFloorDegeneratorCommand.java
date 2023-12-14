package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetFloorDegeneratorCommand implements ICommand {

    @Override
    public String getName() {
        return "floordegenerator";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"floord", "floordegen"};
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return """
                Sets the two corners of a floor to degenerate or clears both of them (if set).
                The types of degeneration are: "inward"(default), "circle" and "random [%chance]"(Default chance: 15).
                Optionally, a degeneration time can be set, this defaults to the value set in the main config.""";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"1", "2", "create", "clear", "type", "time"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> floordegenerator <Parameters...>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the Minigames floor area!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.floordegenerator";
    }

    //todo this can easily expanded, so multible degen regions are possible. Will implement, if needed.
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            if (sender instanceof Player player) {
                MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);
                Location placerLoc = mgPlayer.getLocation();

                switch (args[0].toLowerCase()) {
                    case "1" -> {
                        Location p2 = mgPlayer.getSelectionPoints()[1];
                        mgPlayer.clearSelection();
                        mgPlayer.setSelection(placerLoc, p2);

                        mgPlayer.sendInfoMessage(Component.text("Floor degenerator point 1  for " + minigame + "selected", NamedTextColor.GRAY));
                    }
                    case "2" -> {
                        Location p2 = mgPlayer.getSelectionPoints()[0];
                        mgPlayer.clearSelection();
                        mgPlayer.setSelection(p2, placerLoc);

                        mgPlayer.sendInfoMessage(Component.text("Floor degenerator point 2  for " + minigame + "selected", NamedTextColor.GRAY));
                    }
                    case "create" -> {
                        if (mgPlayer.hasSelection()) {
                            minigame.setFloorDegen(new MgRegion("degen", mgPlayer.getSelectionPoints()[0], mgPlayer.getSelectionPoints()[1]));

                            mgPlayer.clearSelection();

                            mgPlayer.sendInfoMessage(Component.text("Set degeneration region for " + minigame.getName(false), NamedTextColor.GRAY));
                        } else {
                            mgPlayer.sendInfoMessage(Component.text("You have not made a selection!", NamedTextColor.RED));
                        }
                    }
                    case "clear" -> {
                        minigame.removeFloorDegen();
                        mgPlayer.sendInfoMessage(Component.text("Floor degenerator corners have been removed for " + minigame, NamedTextColor.GRAY));
                    }
                    case "type" -> {
                        if (args.length >= 2) {
                            switch (args[1].toLowerCase()) {
                                case "random", "inward", "circle" -> {
                                    minigame.setDegenType(args[1].toLowerCase());

                                    if (args.length > 2 && args[2].matches("[0-9]+")) {
                                        minigame.setDegenRandomChance(Integer.parseInt(args[2]));
                                    }

                                    mgPlayer.sendInfoMessage(Component.text("Floor degenerator type has been set to " + args[1] + " in " + minigame, NamedTextColor.GRAY));
                                }
                                default ->
                                        mgPlayer.sendMessage(Component.join(JoinConfiguration.newlines(), Component.text("Invalid floor degenerator type!", NamedTextColor.RED),
                                                Component.text("Possible types: \"inward\", \"circle\" and \"random\".", NamedTextColor.GRAY)), MinigameMessageType.ERROR);
                            }
                        }
                    }
                    case "time" -> {
                        if (args.length >= 2) {
                            if (args[1].matches("[0-9]+")) {
                                int time = Integer.parseInt(args[1]);
                                minigame.setFloorDegenTime(time);
                                mgPlayer.sendInfoMessage(Component.text("Floor degeneration time has been set to " + MinigameUtils.convertTime(time), NamedTextColor.GRAY));
                            }
                        }
                    }
                    default ->
                            mgPlayer.sendMessage(Component.text("Error: Invalid floor degenerator command!", NamedTextColor.RED), MinigameMessageType.ERROR);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(List.of("1", "2", "create", "clear", "type", "time"), args[0]);
        } else if (args[0].equalsIgnoreCase("type")) {
            return MinigameUtils.tabCompleteMatch(List.of("random", "inward", "circle"), args[1]);
        }
        return null;
    }

}
