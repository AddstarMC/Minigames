package com.pauldavdesign.mineauz.minigames.blockRecorder;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntityData {
	private Entity ent;
	private EntityType entType;
	private Location entLocation;
	private Player player;
	private boolean created;
	
	public EntityData(Entity ent, Player modifier, boolean created){
		this.ent = ent;
		entType = ent.getType();
		entLocation = ent.getLocation();
		player = modifier;
		this.created = created;
	}
	
	public Entity getEntity(){
		return ent;
	}
	
	public Player getModifier(){
		return player;
	}
	
	public boolean wasCreated(){
		return created;
	}
	
	public EntityType getEntityType(){
		return entType;
	}
	
	public Location getEntityLocation(){
		return entLocation;
	}
}
