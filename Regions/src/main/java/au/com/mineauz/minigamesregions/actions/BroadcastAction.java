package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ExpressionParser;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import com.google.common.collect.ImmutableSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class BroadcastAction extends AAction {
    private final StringFlag message = new StringFlag("Hello World", "message");
    private final BooleanFlag excludeExecutor = new BooleanFlag(false, "exludeExecutor");
    private final BooleanFlag redText = new BooleanFlag(false, "redText");

    protected BroadcastAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_BROADCAST_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.MINIGAME;
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Message", message.getFlag());
        out.put("Is Excluding", excludeExecutor.getFlag());
        out.put("Red Text", redText.getFlag());
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public void executeRegionAction(final @Nullable MinigamePlayer mgPlayer, final @NotNull Region region) {
        ScriptObject base = new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return ImmutableSet.of("player", "area", "minigame", "team");
            }

            @Override
            public String getAsString() {
                return "";
            }

            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("player")) {
                    return mgPlayer;
                } else if (name.equalsIgnoreCase("area")) {
                    return region;
                } else if (name.equalsIgnoreCase("minigame")) {
                    return mgPlayer.getMinigame();
                } else if (name.equalsIgnoreCase("team")) {
                    return mgPlayer.getTeam();
                }

                return null;
            }
        };
        debug(mgPlayer, base);
        execute(mgPlayer, base);
    }

    @Override
    public void executeNodeAction(final @Nullable MinigamePlayer mgPlayer, final @NotNull Node node) {
        ScriptObject base = new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return ImmutableSet.of("player", "area", "minigame", "team");
            }

            @Override
            public String getAsString() {
                return "";
            }

            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("player")) {
                    return mgPlayer;
                } else if (name.equalsIgnoreCase("area")) {
                    return node;
                } else if (name.equalsIgnoreCase("minigame")) {
                    return mgPlayer.getMinigame();
                } else if (name.equalsIgnoreCase("team")) {
                    return mgPlayer.getTeam();
                }

                return null;
            }
        };
        debug(mgPlayer, base);
        execute(mgPlayer, base);
    }

    private void execute(@Nullable MinigamePlayer mgPlayer, @NotNull ScriptObject base) {
        MinigameMessageType type = redText.getFlag() ? MinigameMessageType.ERROR : MinigameMessageType.INFO;

        MinigamePlayer exclude = null;
        if (excludeExecutor.getFlag()) {
            exclude = mgPlayer;
        }

        // Old replacement
        String message = this.message.getFlag();
        if (mgPlayer != null) {
            message = message.replace("%player%", mgPlayer.getDisplayName(mgPlayer.getMinigame().usePlayerDisplayNames()));
        }
        // New expression system
        message = ExpressionParser.stringResolve(message, base, true, true);
        if (exclude != null) {
            MinigameMessageManager.sendMinigameMessage(mgPlayer.getMinigame(), MiniMessage.miniMessage().deserialize(message), type, exclude);
        } else {
            MinigameMessageManager.sendMinigameMessage(mgPlayer.getMinigame(), MiniMessage.miniMessage().deserialize(message), type);
        }

    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        message.saveValue(path, config);
        excludeExecutor.saveValue(path, config);
        redText.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        message.loadValue(path, config);
        excludeExecutor.loadValue(path, config);
        redText.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, getDisplayname(), mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);

        m.addItem(message.getMenuItem(Material.NAME_TAG, "Message"));
        m.addItem(excludeExecutor.getMenuItem(Material.ENDER_PEARL, "Don't Send to Executor"));
        m.addItem(redText.getMenuItem(Material.ENDER_PEARL, "Red Message"));

        m.displayMenu(mgPlayer);
        return true;
    }

}
