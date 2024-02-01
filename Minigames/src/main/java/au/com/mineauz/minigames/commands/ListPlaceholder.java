package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 3/06/2020.
 */
public class ListPlaceholder extends ACommand {
    private final int PLACEHOLDERS_PER_SITE = 20; // just a random number. Change it if you know a better one!
    private final Pattern NUM_PATTERN = Pattern.compile("^\\d*$");

    @Override
    public @NotNull String getName() {
        return "placeholders";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_LISTPLACEHOLDERS_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_LISTPLACEHOLDERS_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.placeholders";
    }

    private Component makePage(int pageNumber) {
        List<String> placeholders = new ArrayList<>(PLUGIN.getPlaceHolderManager().getRegisteredPlaceHolders());

        final int numPages = (int) Math.ceil((float) placeholders.size() / PLACEHOLDERS_PER_SITE);
        pageNumber = Math.max(1, Math.min(pageNumber, numPages)); // stay in range

        // get sublist just containing the page
        final List<String> placeholdersOfPage = placeholders.subList(PLACEHOLDERS_PER_SITE * (pageNumber - 1),
                Math.min(placeholders.size(), pageNumber * PLACEHOLDERS_PER_SITE));

        // make page
        final Component pageCore = Component.join(JoinConfiguration.commas(true), placeholdersOfPage.stream().
                map(pHolder -> Component.text("%" + PLUGIN.getName() + "_" + pHolder + "%")).toList());

        // footer / header
        final Component header = MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_LISTPLACEHOLDERS_HEADER,
                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(pageNumber)),
                Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(numPages)));
        final Component footer = MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_DIVIDER_LARGE); //todo clickable next/back buttons on footer

        return header.appendNewline().append(pageCore).appendNewline().append(footer);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args.length > 0) {
            if (NUM_PATTERN.matcher(args[0]).matches()) {
                MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE, makePage(Integer.parseInt(args[0])));
            } else {
                return false;
            }
        } else {
            MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE, makePage(1));
        }
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> placeholders = new ArrayList<>(PLUGIN.getPlaceHolderManager().getRegisteredPlaceHolders());
            final int numPages = (int) Math.ceil((float) placeholders.size() / PLACEHOLDERS_PER_SITE);

            // numbers from 0 --> #pages
            return MinigameUtils.tabCompleteMatch(IntStream.range(1, numPages).boxed().map(String::valueOf).toList(), args[0]);
        }

        return null;
    }
}
