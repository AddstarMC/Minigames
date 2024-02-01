package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ASetCommand {
    protected static final Minigames PLUGIN = Minigames.getPlugin();

    abstract @NotNull String getName();

    /**
     * if this returns null, no aliases exists. Only {@link #getName()} is always valid
     *
     * @return
     */
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    abstract boolean canBeConsole();

    abstract @NotNull Component getDescription();

    abstract Component getUsage();

    /**
     * If this returns null, everyone should be able to use this command!
     */
    abstract @Nullable String getPermission();


    abstract boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame, @NotNull String @Nullable [] args);

    abstract @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame, @NotNull String @Nullable [] args);
}
