package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class HealAction extends AbstractAction {
    private final IntegerFlag heal = new IntegerFlag(1, "amount");

    @Override
    public String getName() {
        return "HEAL";
    }

    @Override
    public String getCategory() {
        return "World Actions";
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Health", heal.getFlag());
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
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer,
                                  @NotNull Node node) {
        debug(mgPlayer, node);
        execute(mgPlayer);
    }

    @Override
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        debug(mgPlayer, region);
        execute(mgPlayer);
    }

    private void execute(MinigamePlayer player) {
        if (player == null || !player.isInMinigame()) return;
        if (heal.getFlag() > 0) {
            if (player.getPlayer().getHealth() != 20) {
                double health = heal.getFlag() + player.getPlayer().getHealth();
                if (health > 20)
                    health = 20;
                player.getPlayer().setHealth(health);
            }
        } else
            player.getPlayer().damage(heal.getFlag() * -1);
    }

    @Override
    public void saveArguments(FileConfiguration config,
                              String path) {
        heal.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        heal.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Heal", mgPlayer);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(heal.getMenuItem("Heal Amount", Material.GOLDEN_APPLE, null, null));
        m.displayMenu(mgPlayer);
        return true;
    }

}
