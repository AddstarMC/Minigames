package au.com.mineauz.minigames.minigame;

import org.bukkit.ChatColor;

public enum TeamColor {
    RED(ChatColor.RED),
    DARK_RED(ChatColor.DARK_RED),
    ORANGE(ChatColor.GOLD),
    YELLOW(ChatColor.YELLOW),
    GREEN(ChatColor.GREEN),
    DARK_GREEN(ChatColor.DARK_GREEN),
    CYAN(ChatColor.DARK_AQUA),
    LIGHT_BLUE(ChatColor.AQUA),
    BLUE(ChatColor.BLUE),
    DARK_BLUE(ChatColor.DARK_BLUE),
    DARK_PURPLE(ChatColor.DARK_PURPLE),
    PURPLE(ChatColor.LIGHT_PURPLE),
    WHITE(ChatColor.WHITE),
    GRAY(ChatColor.GRAY),
    DARK_GRAY(ChatColor.DARK_GRAY),
    BLACK(ChatColor.BLACK);

    private final ChatColor color;

    TeamColor(ChatColor color) {
        this.color = color;
    }

    public static TeamColor matchColor(String color) {
        for (TeamColor col : values()) {
            if (color.equalsIgnoreCase(col.toString())) {
                return col;
            }
        }
        return null;
    }

    public ChatColor getColor() {
        return color;
    }
}
