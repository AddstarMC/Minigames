package au.com.mineauz.minigames.blockRecorder;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import au.com.mineauz.minigames.objects.MinigamePlayer;

public class EntityData {
    private Entity ent;
    private EntityType entType;
    private Location entLocation;
    private MinigamePlayer player;
    private boolean created;

    public EntityData(Entity ent, MinigamePlayer modifier, boolean created) {
        this.ent = ent;
        entType = ent.getType();
        entLocation = ent.getLocation();
        player = modifier;
        this.created = created;
    }

    public Entity getEntity() {
        return ent;
    }

    public MinigamePlayer getModifier() {
        return player;
    }

    public boolean wasCreated() {
        return created;
    }

    public EntityType getEntityType() {
        return entType;
    }

    public Location getEntityLocation() {
        return entLocation;
    }
}
