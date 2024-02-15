package au.com.mineauz.minigamesregions.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.set.ASetCommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import au.com.mineauz.minigamesregions.language.RegionPlaceHolderKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetNodeCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "node";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return RegionMessageManager.getMessage(RegionLangKey.COMMAND_NODE_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return RegionMessageManager.getMessage(RegionLangKey.COMMAND_NODE_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.node";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame, @NotNull String @NotNull [] args) {
        if (args.length > 0) {
            if (sender instanceof Player player) {
                MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);
                RegionModule rmod = RegionModule.getMinigameModule(minigame);

                if (rmod != null) {
                    if (args[0].equalsIgnoreCase("create") && args.length >= 2) {
                        if (!rmod.hasNode(args[1])) {
                            rmod.addNode(args[1], new Node(args[1], mgPlayer.getLocation()));
                            MinigameMessageManager.sendMessage(sender, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                                    RegionLangKey.NODE_ADDED,
                                    Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), args[1]),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                        } else {
                            MinigameMessageManager.sendMessage(sender, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                                    RegionLangKey.COMMAND_NODE_EXISTS,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("modify")) {
                        rmod.displayMenu(mgPlayer, null);
                        return true;
                    } else if (args[0].equalsIgnoreCase("remove") && args.length >= 2) {
                        if (rmod.hasNode(args[1])) {
                            rmod.removeNode(args[1]);
                            MinigameMessageManager.sendMessage(sender, MinigameMessageType.SUCCESS, RegionMessageManager.getBundleKey(),
                                    RegionLangKey.COMMAND_NODE_REMOVED,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                                    Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), args[1]));
                        } else {
                            MinigameMessageManager.sendMessage(sender, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                                    RegionLangKey.NODE_ERROR_NONODE,
                                    Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), args[1]));
                        }
                        return true;
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTGAMEMECHANIC,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                            Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), RegionModule.getFactory().getName()));
                    return true;
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER);
                return true;
            }
        }
        return false;
    }

    // create, modify, remove
    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        RegionModule rmod = RegionModule.getMinigameModule(minigame);
        if (rmod != null) {
            if (args.length == 1) {
                List<String> tab = List.of("create", "modify", "remove");
                return MinigameUtils.tabCompleteMatch(tab, args[0]);
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove")) {
                    List<String> tab = new ArrayList<>();
                    for (Node node : rmod.getNodes()) {
                        tab.add(node.getName());
                    }
                    return MinigameUtils.tabCompleteMatch(tab, args[1]);
                }
            }
        }
        return null;
    }
}
