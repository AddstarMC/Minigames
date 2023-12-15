package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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

    /**
     * Triggers when an entity is right-clicked by a Player with permission to use a minigame Tool
     * THe event IS NOT cancelled - we expect the tools to do that if needs be
     *
     * @param player   the MinigamePlayer
     * @param minigame The Game
     * @param team     Optional team can be null
     * @param event    The event
     */
    void onEntityLeftClick(MinigamePlayer player, Minigame minigame, Team team, EntityDamageByEntityEvent event);

    /**
     * Triggers when an entity is left-clicked by a Player with permission to use a minigame Tool
     * THe event IS NOT cancelled - we expect the tools to do that if needs be
     *
     * @param player   the MinigamePlayer
     * @param minigame The Game
     * @param team     Optional team can be null
     * @param event    The event
     */
    void onEntityRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEntityEvent event);

    void select(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team);

    void deselect(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team);
}
