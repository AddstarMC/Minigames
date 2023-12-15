package au.com.mineauz.minigames.managers.language;

import org.intellij.lang.annotations.Subst;

public enum MinigamePlaceHolderKey implements PlaceHolderKey {
    COMMAND("command"),
    DEATHS("deaths"),
    KILLS("kills"),
    LOADOUT("loadout"),
    MAX("max"),
    MECHANIC("mechanic"),
    MINIGAME("minigame"),
    REGION("region"),
    MONEY("money"),
    NUMBER("number"),
    OBJECTIVE("objective"),
    OTHER_PLAYER("other_player"),
    OTHER_TEAM("other_team"),
    PLAYER("player"),
    REVERTS("reverts"),
    SCORE("score"),
    TEAM("team"),
    TEXT("text"),
    PERMISSION("permission"),
    TIME("time"),
    TYPE("type");

    private final String placeHolder;

    MinigamePlaceHolderKey(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    @Subst("number")
    public String getKey() {
        return placeHolder;
    }
}
