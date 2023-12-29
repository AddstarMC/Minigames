package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.RegenRegionSetResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
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
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Creates and deletes regeneration regions. This only needs to be used for Minigames that have things like leaf decay, fire, tnt etc." +
                " If the Minigame has anything that the player doesn't directly interact with that breaks, this should be used.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return new String[]{"select", "create", "delete", /*"modify"*/};
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame set <Minigame> regenarea select <1/2>",
                "/minigame set <Minigame> regenarea create <name>",
                "/minigame set <Minigame> regenarea list <page>",
                "/minigame set <Minigame> regenarea delete <name>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the regen area of a Minigame!";
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

        Component componentList = LegacyComponentSerializer.legacyAmpersand().deserialize(String.format("&6-----------{Regen Regions &e%s&6/&e%s}-----------", PAGE, NUM_OF_PAGES));

        //add the books for the page
        for (int id = (PAGE - 1) * REGIONS_PER_PAGE; id < MAX_BOOKS_THIS_PAGE; id++) {
            MgRegion region = regions.get(id);

            componentList = componentList.appendNewline();
            componentList = componentList.append(Component.text(region.getName() +
                    " from " + region.getMinX() + ", " + region.getMinY() + ", " + region.getMinZ() +
                    " to " + region.getMaxX() + ", " + region.getMaxY() + ", " + region.getMaxZ() +
                    "(" + region.getVolume() + ")"));
        }

        //add footer
        componentList = componentList.appendNewline();
        componentList = componentList.append(LegacyComponentSerializer.legacySection().deserialize("&2--"));

        //back button or none
        if (PAGE > 1) {
            componentList = componentList.append(LegacyComponentSerializer.legacySection().deserialize(String.format("&6<<( &e%s&6 ) ", PAGE - 1)).clickEvent(ClickEvent.runCommand("/minigame set " + minigame.getName(false) + " regenarea list " + (PAGE - 1))));
        } else {
            componentList = componentList.append(Component.text("-------"));
        }

        //inner part, separating both buttons
        componentList = componentList.append(LegacyComponentSerializer.legacySection().deserialize("&2---<*>---"));

        //next button
        if (PAGE < NUM_OF_PAGES) {
            componentList = componentList.append(LegacyComponentSerializer.legacySection().deserialize(String.format("&6 ( &e%s&6 )>>", PAGE + 1)).clickEvent(ClickEvent.runCommand("/minigame set " + minigame.getName(false) + " regenarea list " + (PAGE + 1))));
        } else {
            componentList = componentList.append(Component.text("-------"));
        }
        componentList = componentList.append(LegacyComponentSerializer.legacySection().deserialize("&2--"));

        return componentList;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            if (sender instanceof Player player) {
                MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);

                if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                    mgPlayer.sendInfoMessage(makeList(minigame, 1));

                } else if (args.length == 2) {
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
                                MgRegion region = minigame.getRegenRegion(name);

                                RegenRegionSetResult result = minigame.setRegenRegion(new MgRegion(name, mgPlayer.getSelectionPoints()[0], mgPlayer.getSelectionPoints()[1]));

                                if (result.success()) {
                                    if (region == null) {
                                        mgPlayer.sendInfoMessage(Component.text("Created a new regen region in " + minigame + " called " + name + ", " + result.numOfBlocksTotal() + "/" + minigame.getRegenBlocklimit()));
                                    } else {
                                        mgPlayer.sendInfoMessage(Component.text("Updated region " + name + " in " + minigame));
                                    }

                                    mgPlayer.clearSelection();
                                } else {
                                    mgPlayer.sendMessage(Component.text("Error: the limit of Blocks of all regen areas together has been reached +(" + result.numOfBlocksTotal() + "/" + minigame.getRegenBlocklimit() + ")." +
                                            " Please contact an admin if necessary.", NamedTextColor.RED), MinigameMessageType.ERROR);
                                }
                            } else {
                                mgPlayer.sendMessage(Component.text("You need to select a region with right click first!"), MinigameMessageType.ERROR);
                            }

                            return true;
                        }
                        case "list" -> {
                            if (args[1].matches("\\d+")) {
                                mgPlayer.sendInfoMessage(makeList(minigame, Integer.parseInt(args[1])));
                            } else {
                                mgPlayer.sendMessage(Component.text(args[1] + "Is not a valid number!"), MinigameMessageType.ERROR);
                            }
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
    public List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                      String alias, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            List<String> tab = new ArrayList<>();
            tab.add("select");
            tab.add("create");
            tab.add("list");
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
