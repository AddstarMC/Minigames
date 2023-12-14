package au.com.mineauz.minigames.minigame;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public enum TeamColor {
    RED(NamedTextColor.RED),
    DARK_RED(NamedTextColor.DARK_RED),
    ORANGE(NamedTextColor.GOLD),
    YELLOW(NamedTextColor.YELLOW),
    GREEN(NamedTextColor.GREEN),
    DARK_GREEN(NamedTextColor.DARK_GREEN),
    CYAN(NamedTextColor.DARK_AQUA),
    LIGHT_BLUE(NamedTextColor.AQUA),
    BLUE(NamedTextColor.BLUE),
    DARK_BLUE(NamedTextColor.DARK_BLUE),
    DARK_PURPLE(NamedTextColor.DARK_PURPLE),
    PURPLE(NamedTextColor.LIGHT_PURPLE),
    WHITE(NamedTextColor.WHITE),
    GRAY(NamedTextColor.GRAY),
    DARK_GRAY(NamedTextColor.DARK_GRAY),
    BLACK(NamedTextColor.BLACK);

    private final NamedTextColor color;
    private final Material displaMaterial;

    TeamColor(NamedTextColor color, Material displaMaterial) {
        this.color = color;
        this.displaMaterial = displaMaterial;
    }

    public static TeamColor matchColor(String color) {
        for (TeamColor col : values()) {
            if (color.equalsIgnoreCase(col.toString())) {
                return col;
            }
        }
        return null;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public Material getDisplaMaterial() {
        return displaMaterial;
    }
}
