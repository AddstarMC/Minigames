package au.com.mineauz.minigames.backend.sqlite;

import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.backend.Backend;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredGameStats;
import au.com.mineauz.minigames.stats.StoredStat;

public class SQLiteBackend implements Backend {
	
	@Override
	public boolean initialize(ConfigurationSection config) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public void saveGameStatus(StoredGameStats stats) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<StoredStat> loadStats(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StoredStat> loadStats(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order, int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public long getStat(Minigame minigame, UUID playerId, MinigameStat stat, StatValueField field) {
		// TODO Auto-generated method stub
		return 0;
	}

}
