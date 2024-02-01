package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommandInfo;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ASetCommand implements ICommandInfo {
    protected static final Minigames PLUGIN = Minigames.getPlugin();

    abstract boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame, @NotNull String @Nullable [] args);

    abstract @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame, @NotNull String @Nullable [] args);
}
