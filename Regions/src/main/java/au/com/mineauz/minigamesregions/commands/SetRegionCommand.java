package au.com.mineauz.minigamesregions.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.set.ASetCommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Region;
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

public class SetRegionCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "region";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return RegionMessageManager.getMessage(RegionLangKey.COMMAND_REGION_DESCIPTION);
    }

    @Override
    public Component getUsage() { //todo remove selection and delete to remove
        return RegionMessageManager.getMessage(RegionLangKey.COMMAND_REGION_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.region";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame, @NotNull String @NotNull [] args) {
        if (args != null) {
            if (sender instanceof Player player) {
                MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);
                RegionModule rmod = RegionModule.getMinigameModule(minigame);

                if (rmod != null) {
                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("create")) {
                            if (mgPlayer.hasSelection()) {
                                String name = args[1];
                                rmod.addRegion(name, new Region(name, minigame, mgPlayer.getSelectionLocations()[0], mgPlayer.getSelectionLocations()[1]));
                                mgPlayer.clearSelection();

                                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                                        RegionLangKey.REGION_CREATED,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                                        Placeholder.unparsed(RegionPlaceHolderKey.REGION.getKey(), name));
                            } else {
                                // this is a message already in the main plugin
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.REGION_ERROR_NOSELECTION);
                            }
                            return true;
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (rmod.hasRegion(args[1])) {
                                rmod.removeRegion(args[1]);
                                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                                        RegionLangKey.REGION_REMOVED,
                                        Placeholder.unparsed(RegionPlaceHolderKey.REGION.getKey(), args[1]),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                            } else {
                                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                                        RegionLangKey.REGION_ERROR_NOREGION,
                                        Placeholder.unparsed(RegionPlaceHolderKey.REGION.getKey(), args[1]),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                            }
                            return true;
                        }

                    } else if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("modify")) {
                            rmod.displayMenu(mgPlayer, null);
                            return true;
                        }
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTGAMEMECHANIC,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                            Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), RegionModule.getFactory().getName()));
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER);
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> tab = List.of("create", "modify", "remove");
            return MinigameUtils.tabCompleteMatch(tab, args[0]);
        } else if (args.length == 2) {
            List<String> tab = new ArrayList<>();
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove")) {
                RegionModule rmod = RegionModule.getMinigameModule(minigame);
                for (Region reg : rmod.getRegions()) {
                    tab.add(reg.getName());
                }
            }
            return MinigameUtils.tabCompleteMatch(tab, args[1]);
        }
        return null;
    }
}
