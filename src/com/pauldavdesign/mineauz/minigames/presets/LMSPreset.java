package com.pauldavdesign.mineauz.minigames.presets;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class LMSPreset implements BasePreset {

	@Override
	public String getName() {
		return "LMS";
	}

	@Override
	public String getInfo() {
		return "Creates a last man standing game (in the same way the old LMS gametype used to be). This creates a deathmatch Minigame where each player" +
				" has only one life, meaning the last player standing in the arena will be the winner.";
	}

	@Override
	public void execute(Minigame minigame) {
		minigame.setLives(1);
		minigame.setType("dm");
		minigame.setScoreType("none");
	}

}
