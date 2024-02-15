package au.com.mineauz.minigames.managers.language;

import org.intellij.lang.annotations.Subst;

public enum MinigamePlaceHolderKey implements PlaceHolderKey {
    BIOME("biome"),
    COMMAND("command"),
    COORDINATE_X("x"),
    COORDINATE_Y("y"),
    COORDINATE_Z("z"),
    REWARD("reward"),
    DEATHS("deaths"),
    DIRECTION("direction"),
    FLAG("flag"),
    KILLS("kills"),
    LOADOUT("loadout"),
    LOCATION("location"),
    MATERIAL("material"),
    MAX("max"),
    MECHANIC("mechanic"),
    MIN("min"),
    MINIGAME("minigame"),
    MONEY("money"),
    NUMBER("number"),
    OBJECTIVE("objective"),
    OTHER_PLAYER("other_player"),
    OTHER_TEAM("other_team"),
    PERMISSION("permission"),
    PLAYER("player"),
    POSITION_1("pos1"),
    POSITION_2("pos2"),
    PRESET("preset"),
    RARITY("rarity"),
    REGION("region"),
    REVERTS("reverts"),
    SCORE("score"),
    STAT("stat"),
    STATE("state"),
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
