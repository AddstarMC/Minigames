package au.com.mineauz.minigames.managers.language;

import org.intellij.lang.annotations.Subst;

public enum MinigamePlaceHolderKey implements PlaceHolderKey {
    BIOME("biome"),
    COMMAND("command"),
    DEATHS("deaths"),
    DIRECTION("direction"),
    KILLS("kills"),
    LOADOUT("loadout"),
    MAX("max"),
    MECHANIC("mechanic"),
    MINIGAME("minigame"),
    MONEY("money"),
    NUMBER("number"),
    OBJECTIVE("objective"),
    OTHER_PLAYER("other_player"),
    OTHER_TEAM("other_team"),
    PERMISSION("permission"),
    PLAYER("player"),
    POSITION("position"),
    REGION("region"),
    REVERTS("reverts"),
    SCORE("score"),
    TEAM("team"),
    TEXT("text"),
    TIME("time"),
    TYPE("type"),
    WORLD("world");

    private final String placeHolder;

    MinigamePlaceHolderKey(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    @Subst("number")
    public String getKey() {
        return placeHolder;
    }
}
