package au.com.mineauz.minigames.gametypes;

public enum MinigameType {
    SINGLEPLAYER("Singleplayer"),
    MULTIPLAYER("Multiplayer"),
    //    TREASURE_HUNT("Treasure Hunt");
    GLOBAL("Global");

    private String name;

    MinigameType(String name) {
        this.name = name;
    }

    public static boolean hasValue(String value) {
        for (MinigameType type : values()) {
            if (type.toString().equalsIgnoreCase(value))
                return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }
}
