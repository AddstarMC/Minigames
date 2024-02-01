package au.com.mineauz.minigames.commands;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICommandInfo {

    @NotNull String getName();

    /**
     * if this returns null, no aliases exists. Only {@link #getName()} is always valid
     */
    default @NotNull String @Nullable [] getAliases(){
        return null;
    }

    boolean canBeConsole();

    @NotNull Component getDescription();

    Component getUsage();

    /**
     * If this returns null, everyone should be able to use this command!
     */
    @Nullable String getPermission();
}
