package au.com.mineauz.minigames.managers.language.langkeys;

import org.jetbrains.annotations.NotNull;

public enum MgSignLangKey implements LangKey {
    MINIGAME("sign.minigame"),
    TYPE_BET("sign.type.bet"),
    TYPE_CHECKPOINT("sign.type.checkpoint"),
    TYPE_CTFFLAG("sign.type.flag"),
    TYPE_FINISH("sign.type.finish"),
    TYPE_TELEPORT("sign.type.teleport"),
    TYPE_JOIN("sign.type.join"),
    TYPE_LOADOUT("sign.type.loadout"),
    TYPE_QUIT("sign.type.quit"),
    TYPE_REWARD("sign.type.reward"),
    TYPE_SCOREBOARD("sign.type.scoreboard"),
    TYPE_SCORE("sign.type.score"),
    TYPE_SPECTATE("sign.type.spectate"),
    TYPE_TEAM("sign.type.team");

    private final String path;

    MgSignLangKey(@NotNull String path) {
        this.path = path;
    }

    @Override
    public @NotNull String getPath() {
        return path;
    }
}
