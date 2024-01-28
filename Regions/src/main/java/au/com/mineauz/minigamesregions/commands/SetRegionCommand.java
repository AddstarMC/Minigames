package au.com.mineauz.minigamesregions.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
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

public class SetRegionCommand implements ICommand {

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
        return List.of("Creates, edits and removes Minigame regions");
    }

    @Override
    public Component getUsage() {
        return new String[]{
                "/minigame set <Minigame> region create <name>",
                "/minigame set <Minigame> region remove <name>",
                "/minigame set <Minigame> region modify"
        };
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.region";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
            RegionModule rmod = RegionModule.getMinigameModule(minigame);
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (mgPlayer.hasSelection()) {
                        String name = args[1];
                        rmod.addRegion(name, new Region(name, minigame, mgPlayer.getSelectionLocations()[0], mgPlayer.getSelectionLocations()[1]));
                        mgPlayer.clearSelection();

                        MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                                RegionLangKey.REGION_CREATED,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
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
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                    } else {
                        MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                                RegionLangKey.REGION_ERROR_NOREGION,
                                Placeholder.unparsed(RegionPlaceHolderKey.REGION.getKey(), args[1]),
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                    }
                    return true;
                }

            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("modify")) {
                    rmod.displayMenu(mgPlayer, null);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            List<String> tab = new ArrayList<>();
            tab.add("create");
            tab.add("modify");
            tab.add("remove");
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
