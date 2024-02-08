package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ACommand implements ICommandInfo {
    protected static final Minigames PLUGIN = Minigames.getPlugin();

    abstract public boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args);

    abstract public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args);
}
