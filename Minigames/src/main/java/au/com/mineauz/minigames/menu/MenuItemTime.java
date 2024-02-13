package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MenuItemTime extends MenuItemLong {

    public MenuItemTime(@Nullable Material displayMat, @Nullable Component name, @NotNull Callback<Long> value,
                        @Nullable Long min, @Nullable Long max) {
        super(displayMat, name, value, min, max);
    }

    public MenuItemTime(@Nullable Material displayMat, @NotNull LangKey langKey, @Nullable List<@NotNull Component> description,
                        @NotNull Callback<Long> value, @Nullable Long min, @Nullable Long max) {
        super(displayMat, langKey, description, value, min, max);
    }

    public MenuItemTime(@Nullable Material displayMat, @Nullable Component name, @Nullable List<@NotNull Component> description,
                        @NotNull Callback<Long> value, @Nullable Long min, @Nullable Long max) {
        super(displayMat, name, description, value, min, max);
    }

    @Override
    public void updateDescription() {
        Component timeComponent = MinigameUtils.convertTime(Duration.ofMillis(value.getValue()), true);

        List<Component> description;
        if (getDescription() != null) {
            description = getDescription();
            String desc = ChatColor.stripColor(getDescription().get(0));

            if (desc.matches("([0-9]+[dhms]:?)+")) {
                description.set(0, ChatColor.GREEN + timeComponent);
            } else {
                description.add(0, ChatColor.GREEN + timeComponent);
            }
        } else {
            description = new ArrayList<>();
            description.add(ChatColor.GREEN + timeComponent);
        }

        setDescription(description);
    }
}
