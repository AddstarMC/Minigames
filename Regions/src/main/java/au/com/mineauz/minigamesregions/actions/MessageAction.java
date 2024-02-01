package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ExpressionParser;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class MessageAction extends AbstractAction {

    private final StringFlag msg = new StringFlag("Hello World", "message");

    @Override
    public @NotNull String getName() {
        return "MESSAGE";
    }

    @Override
    public @NotNull String getCategory() {
        return "Minigame Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        out.put("Message", msg.getFlag());
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
    public void executeNodeAction(final MinigamePlayer player, final @NotNull Node node) {
        debug(player, node);
        if (player == null || !player.isInMinigame()) return;

        ScriptObject base = new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return Set.of("player", "area", "minigame", "team");
            }

            @Override
            public String getAsString() {
                return "";
            }

            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("player")) {
                    return player;
                } else if (name.equalsIgnoreCase("area")) {
                    return node;
                } else if (name.equalsIgnoreCase("minigame")) {
                    return player.getMinigame();
                } else if (name.equalsIgnoreCase("team")) {
                    return player.getTeam();
                }

                return null;
            }
        };
        debug(player, base);
        execute(player, base);
    }

    @Override
    public void executeRegionAction(final MinigamePlayer player, final @NotNull Region region) {
        debug(player, region);
        if (player == null || !player.isInMinigame()) return;
        player.sendMessage(msg.getFlag(), MinigameMessageType.INFO);

        ScriptObject base = new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return Set.of("player", "area", "minigame", "team");
            }

            @Override
            public String getAsString() {
                return "";
            }

            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("player")) {
                    return player;
                } else if (name.equalsIgnoreCase("area")) {
                    return region;
                } else if (name.equalsIgnoreCase("minigame")) {
                    return player.getMinigame();
                } else if (name.equalsIgnoreCase("team")) {
                    return player.getTeam();
                }

                return null;
            }
        };
        debug(player, base);
        execute(player, base);
    }

    private void execute(MinigamePlayer player, ScriptObject base) {
        String message = msg.getFlag();

        message = ExpressionParser.stringResolve(message, base, true, true);
        player.sendMessage(message, MinigameMessageType.INFO);
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        msg.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
        msg.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Options", player);
        m.setPreviousPage(previous);
        m.addItem(msg.getMenuItem("Message", Material.PAPER));
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), m.getPreviousPage()), m.getSize() - 9);
        m.displayMenu(player);
        return true;
    }
}
