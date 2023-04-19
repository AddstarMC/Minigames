package au.com.mineauz.minigames.blockRecorder;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityData {
    //uuid of the entity
    private final UUID uuid;
    //type of entity
    private final EntityType entType;
    //location the entity had when it was changed
    private final Location entLocation;
    // the player who has changed the entity. If null, the entity doesn't get reset if the player left the minigame
    private final @Nullable MinigamePlayer player;
    // was this entity created and needs to removed or was it changed / killed?
    private final boolean created;

    //todo save metadata like armor
    public EntityData(Entity entity, @Nullable MinigamePlayer modifier, boolean created) {
        this.uuid = entity.getUniqueId();
        this.entType = entity.getType();
        this.entLocation = entity.getLocation();
        this.player = modifier;
        this.created = created;
    }

    public @Nullable Entity getEntity() {
        return Bukkit.getEntity(uuid);
    }

    public @Nullable MinigamePlayer getModifier() {
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
