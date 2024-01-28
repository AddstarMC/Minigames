package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.RegenRegionChangeResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetRegenAreaCommand implements ICommand {
    private final int REGIONS_PER_PAGE = 5;

    @Override
    public @NotNull String getName() {
        return "regenarea";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_REGENAREA_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_REGENAREA_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.regenarea";
    }

    /**
     * helper methode for creating the list-component for /minigame set <Minigame> regenarea list <page>
     *
     * @param minigame the minigame of the command
     * @param page     the given page
     * @return returns a component containing max 5 regen regions with their coordinates and volume
     */
    private Component makeList(Minigame minigame, int page) {
        //get all currently active regions
        List<MgRegion> regions = new ArrayList<>(minigame.getRegenRegions());
        //how many regions are known. Needed to calculate how many pages there are and
        //how many there should be on the given page (if the page is not full)
        final int NUM_OF_REGIONS = regions.size();
        //how many pages of regions are there? - needed in header and limit page to how many exits
        final int NUM_OF_PAGES = (int) Math.ceil((double) NUM_OF_REGIONS / (double) REGIONS_PER_PAGE);

        //limit page to range of possible pages
        final int PAGE = Math.max(Math.min(page, NUM_OF_PAGES), 1);
        //don't try to access more books than exits
        final int MAX_BOOKS_THIS_PAGE = Math.min(NUM_OF_REGIONS, PAGE * REGIONS_PER_PAGE);

        TextComponent.Builder listBuilder = Component.text();
        listBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_REGENAREA_LIST_HEADER,
                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(PAGE)),
                Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(NUM_OF_PAGES))));

        //add the books for the page
        for (int id = (PAGE - 1) * REGIONS_PER_PAGE; id < MAX_BOOKS_THIS_PAGE; id++) {
            MgRegion region = regions.get(id);
            listBuilder.appendNewline();

            listBuilder.append(MinigameMessageManager.getMgMessage(MinigameLangKey.REGION_DESCRIBE,
                    Placeholder.component(MinigamePlaceHolderKey.POSITION_1.getKey(),
                            MinigameMessageManager.getMgMessage(MinigameLangKey.REGION_POSITION,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.COORDINATE_X.getKey(), String.valueOf(region.getMinX())),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.COORDINATE_Y.getKey(), String.valueOf(region.getMinY())),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.COORDINATE_Z.getKey(), String.valueOf(region.getMinZ())))),
                    Placeholder.component(MinigamePlaceHolderKey.POSITION_2.getKey(),
                            MinigameMessageManager.getMgMessage(MinigameLangKey.REGION_POSITION,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.COORDINATE_X.getKey(), String.valueOf(region.getMaxX())),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.COORDINATE_Y.getKey(), String.valueOf(region.getMaxY())),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.COORDINATE_Z.getKey(), String.valueOf(region.getMaxZ()))))));
        }

        //todo footer in messages and not legacy formatting
        //add footer
        listBuilder.appendNewline();
        listBuilder.append(LegacyComponentSerializer.legacySection().deserialize("&2--"));

        //back button or none
        if (PAGE > 1) {
            listBuilder.append(LegacyComponentSerializer.legacySection().deserialize(String.format("&6<<( &e%s&6 ) ", PAGE - 1)).
                    clickEvent(ClickEvent.runCommand("/minigame set " + minigame.getName(false) + " regenarea list " + (PAGE - 1))));
        } else {
            listBuilder.append(Component.text("-------"));
        }

        //inner part, separating both buttons
        listBuilder.append(LegacyComponentSerializer.legacySection().deserialize("&2---<*>---"));

        //next button
        if (PAGE < NUM_OF_PAGES) {
            listBuilder.append(LegacyComponentSerializer.legacySection().deserialize(String.format("&6 ( &e%s&6 )>>", PAGE + 1)).
                    clickEvent(ClickEvent.runCommand("/minigame set " + minigame.getName(false) + " regenarea list " + (PAGE + 1))));
        } else {
            listBuilder.append(Component.text("-------"));
        }
        listBuilder.append(LegacyComponentSerializer.legacySection().deserialize("&2--"));

        return listBuilder.build();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            if (sender instanceof Player player) {
                MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);

                if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                    MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE, makeList(minigame, 1));

                } else if (args.length == 2) {
                    switch (args[0].toLowerCase()) {
                        case "create" -> {
                            if (mgPlayer.hasSelection()) {
                                String name = args[1];
                                MgRegion region = minigame.getRegenRegion(name);

                                RegenRegionChangeResult result = minigame.setRegenRegion(new MgRegion(name, mgPlayer.getSelectionLocations()[0], mgPlayer.getSelectionLocations()[1]));

                                if (result.success()) {
                                    if (region == null) {
                                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.REGION_REGENREGION_CREATED,
                                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                                                Placeholder.unparsed(MinigamePlaceHolderKey.REGION.getKey(), name),
                                                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(result.numOfBlocksTotal())),
                                                Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(minigame.getRegenBlocklimit())));
                                    } else {
                                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.REGION_REGENREGION_UPDATED,
                                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(true)),
                                                Placeholder.unparsed(MinigamePlaceHolderKey.REGION.getKey(), name),
                                                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(result.numOfBlocksTotal())),
                                                Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(minigame.getRegenBlocklimit())));
                                    }

                                    mgPlayer.clearSelection();
                                } else {
                                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.REGION_REGENREGION_ERROR_LIMIT,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(result.numOfBlocksTotal())),
                                            Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(minigame.getRegenBlocklimit())));
                                }
                            } else {
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_SET_REGENAREA_ERROR_NOTSELECTED);
                            }

                            return true;
                        }
                        case "list" -> {
                            Tag.selfClosingInserting(Component.text());

                            if (args[1].matches("\\d+")) {
                                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.NONE, makeList(minigame, Integer.parseInt(args[1])));
                            } else {
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                            }
                        }
                        case "remove" -> {
                            RegenRegionChangeResult result = minigame.removeRegenRegion(args[1]);

                            if (result.success()) {
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.REGION_REGENREGION_REMOVED,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(true)),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.REGION.getKey(), args[1]),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(result.numOfBlocksTotal())),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(minigame.getRegenBlocklimit())));
                            } else {
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.REGION_ERROR_NOREGENREION,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.REGION.getKey(), args[1]));
                            }
                            return true;
                        }
                    }
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTAPLAYER);
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            List<String> tab = new ArrayList<>();
            tab.add("create");
            tab.add("list");
            tab.add("remove");
            return MinigameUtils.tabCompleteMatch(tab, args[0]);
        } else if (args.length == 2) {
            List<String> tab = new ArrayList<>();
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove")) {
                for (MgRegion region : minigame.getRegenRegions()) {
                    tab.add(region.getName());
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                //cache number of pages to not recalculate every loop
                final int PAGES = (int) Math.ceil((double) minigame.getRegenRegions().size() / (double) REGIONS_PER_PAGE);

                //make list of all known pages
                for (int page = 1; page <= PAGES; page++) {
                    tab.add(String.valueOf(page));
                }
            }
            return MinigameUtils.tabCompleteMatch(tab, args[1]);
        }
        return null;
    }
}
