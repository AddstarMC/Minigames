package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ToolMode {

    String getName();

    Component getDisplayName();

    List<Component> getDescription();

    Material getIcon();

    void onSetMode(MinigamePlayer player, MinigameTool tool);

    void onUnsetMode(@NotNull MinigamePlayer mgPlayer, MinigameTool tool);

    void onLeftClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team, @NotNull PlayerInteractEvent event);

    void onRightClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team, @NotNull PlayerInteractEvent event);

    void select(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team);

    void deselect(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team);
}
