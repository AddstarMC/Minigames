package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ACommand {
    protected static final Minigames PLUGIN = Minigames.getPlugin();

    abstract public @NotNull String getName();

    /**
     * if this returns null, no aliases exists. Only {@link #getName()} is always valid
     */
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    abstract public boolean canBeConsole();

    abstract public @NotNull Component getDescription();

    abstract public Component getUsage();

    /**
     * If this returns null, everyone should be able to use this command!
     */
    abstract public @Nullable String getPermission();

    abstract public boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args);

    abstract public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args);
}
