package au.com.mineauz.minigames.display;

import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class AbstractDisplayObject implements IDisplayObject {
    private DisplayManager manager;
    
    protected Player player;
    protected World world;
    
    public AbstractDisplayObject(DisplayManager manager, World world) {
        this.manager = manager;
        this.world = world;
    }
    
    public AbstractDisplayObject(DisplayManager manager, Player player) {
        this.manager = manager;
        this.world = player.getWorld();
        this.player = player;
    }
    
    @Override
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public boolean isPlayerDisplay() {
        return player != null;
    }
    
    @Override
    public World getWorld() {
        return world;
    }
    
    @Override
    public void show() {
        manager.onShow(this);
    }

    @Override
    public void hide() {
        manager.onHide(this);
    }
    
    @Override
    public void remove() {
        hide();
        manager.onRemove(this);
    }
}
