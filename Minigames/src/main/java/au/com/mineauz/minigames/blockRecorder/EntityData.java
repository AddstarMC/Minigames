package au.com.mineauz.minigames.blockRecorder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import au.com.mineauz.minigames.objects.MinigamePlayer;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityData {
    private final UUID uuid;
    private final EntityType entType;
    private final Location entLocation;
    private final MinigamePlayer player;
    private final boolean created;

    //todo save metadata like armor
    public EntityData(Entity entity, MinigamePlayer modifier, boolean created) {
        this.uuid = entity.getUniqueId();
        this.entType = entity.getType();
        this.entLocation = entity.getLocation();
        this.player = modifier;
        this.created = created;
    }

    public @Nullable Entity getEntity() {
        return Bukkit.getEntity(uuid);
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
