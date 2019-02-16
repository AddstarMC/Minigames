package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface ToolMode {

    String getName();

    String getDisplayName();

    String getDescription();

    Material getIcon();

    void onSetMode(MinigamePlayer player, MinigameTool tool);

    void onUnsetMode(MinigamePlayer player, MinigameTool tool);

    void onLeftClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event);

    void onRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event);

    /**
     * Triggers when an entity is right clicked by a Player with permission to use a minigame Tool
     * THe event IS NOT cancelled - we expect the tools to do that if needs be
     *
     * @param player   the MinigamePlayer
     * @param minigame The Game
     * @param team     Optional team can be null
     * @param event    The event
     */
    void onEntityLeftClick(MinigamePlayer player, Minigame minigame, Team team, EntityDamageByEntityEvent event);

    /**
     * Triggers when an entity is left clicked by a Player with permission to use a minigame Tool
     * THe event IS NOT cancelled - we expect the tools to do that if needs be
     *
     * @param player   the MinigamePlayer
     * @param minigame The Game
     * @param team     Optional team can be null
     * @param event    The event
     */
    void onEntityRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEntityEvent event);

    void select(MinigamePlayer player, Minigame minigame, Team team);

    void deselect(MinigamePlayer player, Minigame minigame, Team team);
}
