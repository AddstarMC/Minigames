package au.com.mineauz.minigames.minigame;

import org.bukkit.ChatColor;

public enum TeamColor {
    RED(ChatColor.RED),
    BLUE(ChatColor.BLUE),
    GREEN(ChatColor.GREEN),
    YELLOW(ChatColor.GOLD),
    PURPLE(ChatColor.LIGHT_PURPLE),
    WHITE(ChatColor.WHITE),
    BLACK(ChatColor.BLACK),
    DARK_RED(ChatColor.DARK_RED),
    DARK_PURPLE(ChatColor.DARK_PURPLE),
    DARK_GREEN(ChatColor.DARK_GREEN),
    DARK_BLUE(ChatColor.DARK_BLUE),
    GRAY(ChatColor.GRAY);

    private ChatColor color;

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
