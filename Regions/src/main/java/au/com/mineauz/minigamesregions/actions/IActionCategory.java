package au.com.mineauz.minigamesregions.actions;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface IActionCategory {
    @NotNull Component getDisplayName();
}
