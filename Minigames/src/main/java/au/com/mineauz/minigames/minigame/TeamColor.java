package au.com.mineauz.minigames.minigame;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum TeamColor {
    BLACK(NamedTextColor.BLACK, Material.BLACK_WOOL),
    BLUE(NamedTextColor.BLUE, Material.BLUE_WOOL),
    CYAN(NamedTextColor.DARK_AQUA, Material.CYAN_WOOL),
    DARK_BLUE(NamedTextColor.DARK_BLUE, Material.BLUE_CONCRETE),
    DARK_GRAY(NamedTextColor.DARK_GRAY, Material.GRAY_CONCRETE),
    DARK_GREEN(NamedTextColor.DARK_GREEN, Material.GREEN_CONCRETE),
    DARK_PURPLE(NamedTextColor.DARK_PURPLE, Material.PURPLE_CONCRETE),
    DARK_RED(NamedTextColor.DARK_RED, Material.RED_CONCRETE),
    GRAY(NamedTextColor.GRAY, Material.GRAY_WOOL),
    GREEN(NamedTextColor.GREEN, Material.GREEN_WOOL),
    LIGHT_BLUE(NamedTextColor.AQUA, Material.LIGHT_BLUE_WOOL),
    ORANGE(NamedTextColor.GOLD, Material.ORANGE_WOOL),
    PURPLE(NamedTextColor.LIGHT_PURPLE, Material.PURPLE_WOOL),
    RED(NamedTextColor.RED, Material.RED_WOOL),
    WHITE(NamedTextColor.WHITE, Material.WHITE_WOOL),
    YELLOW(NamedTextColor.YELLOW, Material.YELLOW_WOOL),

    NONE(NamedTextColor.RED, Material.BARRIER);

    private final NamedTextColor color;
    private final Material displaMaterial;

    TeamColor(NamedTextColor color, Material displaMaterial) {
        this.color = color;
        this.displaMaterial = displaMaterial;
    }

    public static @Nullable TeamColor matchColor(@NotNull String color) {
        for (TeamColor col : values()) {
            if (color.equalsIgnoreCase(col.toString())) {
                return col;
            }
        }
        return null;
    }

    public static Set<TeamColor> validColors() {
        return Arrays.stream(TeamColor.values()).filter(tc -> tc != NONE).collect(Collectors.toSet());
    }

    public static Set<String> validColorNames() {
        return validColors().stream().map(Enum::toString).collect(Collectors.toSet());
    }

    public static Set<String> colorNames() {
        return Arrays.stream(values()).map(Enum::toString).collect(Collectors.toSet());
    }

    public static Component validColorNamesComp() {
        return Component.join(JoinConfiguration.separator(MiniMessage.miniMessage().deserialize("<gray>, </gray>")),
                validColors().stream().map(TeamColor::getCompName).collect(Collectors.toSet()));
    }

    public Component getCompName() {
        return Component.text(this.toString(), this.getColor());
    }

    @Override
    public @NotNull String toString() {
        return WordUtils.capitalizeFully(super.toString().replaceAll("_", " "));
    }

    public NamedTextColor getColor() {
        return color;
    }

    public Material getDisplaMaterial() {
        return displaMaterial;
    }
}
