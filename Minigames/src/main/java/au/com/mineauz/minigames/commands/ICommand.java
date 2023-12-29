package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ICommand {
    Minigames plugin = Minigames.getPlugin();

    /**
     * get the name of this (sub) command
     * the name get used as it's first parameter
     * in case of the command /mg the set subcommand /mg set implements this interface,
     * as well as every sub-subcommand of it. set is the name of this specific subcommand
     * and the name of all the ones one layer down are its parameters.
     */
    @NotNull String getName();

    /**
     * Get the aliases to the name of this command.
     * An alias is an alternative name that should also show up as a parameter
     * but calls the same command.
     */
    @NotNull String @Nullable [] getAliases();

    /**
     * If the command can be called from the console or if it is strictly only callable by a player
     */
    boolean canBeConsole();

    /**
     * Used in help command to describe what this (sub)command does.
     */
    @NotNull Component getDescription();

    @NotNull String @Nullable [] getParameters();

    Component getUsage();

    /**
     * If this returns null, everyone should be able to use this command!
     */
    @Nullable String getPermission();

    /**
     * @param sender
     * @param minigame all set commands are guarantied to have not null minigame parameter. Everything else probably is null.
     * @param label
     * @param args
     * @return
     */
    boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                      @NotNull String label, @NotNull String @Nullable [] args);

    /**
     * @param sender
     * @param minigame
     * @param alias    not null for all set commands, else wise may or may not be null
     * @param args
     * @return
     */
    List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame, String alias, @NotNull String[] args);
}
