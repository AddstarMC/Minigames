package au.com.mineauz.minigamesregions.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import au.com.mineauz.minigamesregions.language.RegionPlaceHolderKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetNodeCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "node";
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
    public @NotNull Component getDescription() { //todo translation String
        return List.of(
                "Creates and modifies customizable nodes");
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public Component getUsage() {
        return new String[]{"/minigame set <Minigame> node create <name>",
                "/minigame set <Minigame> node modify",
                "/minigame set <minigame> node delete <name>"
        };
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.node";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
            RegionModule rmod = RegionModule.getMinigameModule(minigame);
            if (args[0].equalsIgnoreCase("create") && args.length >= 2) {
                if (!rmod.hasNode(args[1])) {
                    rmod.addNode(args[1], new Node(args[1], mgPlayer.getLocation()));
                    MinigameMessageManager.sendMessage(sender, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                            RegionLangKey.NODE_ADDED,
                            Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), args[1]),
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(true)));
                } else {
                    MinigameMessageManager.sendMessage(sender, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                            RegionLangKey.COMMAND_NODE_EXISTS,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]),
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(true)));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("modify")) {
                rmod.displayMenu(mgPlayer, null);
                return true;
            } else if (args[0].equalsIgnoreCase("delete") && args.length >= 2) {
                if (rmod.hasNode(args[1])) {
                    rmod.removeNode(args[1]);
                    sender.sendMessage(ChatColor.GRAY + "Removed a node called " + args[1] + " from " + minigame);
                } else
                    sender.sendMessage(ChatColor.RED + "A node by the name " + args[1] + " doesn't exists in " + minigame);
                return true;
            }
        }
        return false;
    }

    // create, modify, delete
    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> tab = new ArrayList<>();
            tab.add("create");
            tab.add("modify");
            tab.add("delete");
            return MinigameUtils.tabCompleteMatch(tab, args[0]);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("delete")) {
                List<String> tab = new ArrayList<>();
                RegionModule rmod = RegionModule.getMinigameModule(minigame);
                for (Node node : rmod.getNodes()) {
                    tab.add(node.getName());
                }
                return MinigameUtils.tabCompleteMatch(tab, args[1]);
            }
        }
        return null;
    }
}
