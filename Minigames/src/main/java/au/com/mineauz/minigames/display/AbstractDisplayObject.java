package au.com.mineauz.minigames.display;

import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * The type Abstract display object.
 */
public abstract class AbstractDisplayObject implements IDisplayObject {

    private final World world;

    private final DisplayManager manager;

    protected Player player;

    /**
     * Instantiates a new Abstract display object.
     *
     * @param m the manager
     * @param w the world
     */
    public AbstractDisplayObject(final DisplayManager m, final World w) {
        this.manager = m;
        this.world = w;
    }

    /**
     * Instantiates a new Abstract display object.
     *
     * @param m the manager
     * @param p the player
     */
    public AbstractDisplayObject(final DisplayManager m, final Player p) {
        this.manager = m;
        this.world = p.getWorld();
        this.player = p;
    }

    /**
     * True if player display.
     *
     * @return boolean
     */
    @Override
    public boolean isPlayerDisplay() {
        return player != null;
    }

    /**
     * Get the player.
     *
     * @return the player
     */
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the world
     */
    @Override
    public World getWorld() {
        return world;
    }

    /**
     * Show the Display.
     */
    @Override
    public void show() {
        manager.onShow(this);
    }

    /**
     * Hide the display.
     */
    @Override
    public void hide() {
        manager.onHide(this);
    }

    /**
     * remove the display.
     */
    @Override
    public void remove() {
        hide();
        manager.onRemove(this);
    }
}
