package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Main;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchTeamCondition extends ACondition {
    private final StringFlag team = new StringFlag(TeamColor.RED.toString(), "team");

    protected MatchTeamCondition(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITION_MATCHTEAM_NAME);
    }

    @Override
    public @NotNull IConditionCategory getCategory() {
        return RegionConditionCategories.TEAM;
    }

    @Override
    public void describe(@NotNull Map<String, Object> out) {
        out.put("Team", team.getFlag());
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
    public boolean checkNodeCondition(MinigamePlayer player, @NotNull Node node) {
        return player.getTeam() != null && player.getTeam().getColor().toString().equals(team.getFlag());
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, @NotNull Region region) {
        if (player == null || !player.isInMinigame()) return false;
        return player.getTeam() != null && player.getTeam().getColor().toString().equals(team.getFlag());
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        team.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        team.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, getDisplayName(), player);
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);

        List<TeamColor> teams = new ArrayList<>(TeamColor.validColors());

        m.addItem(new MenuItemList<>(getTeamMaterial(), RegionMessageManager.getMessage(RegionLangKey.MENU_TEAM_NAME), new Callback<>() {
            @Override
            public TeamColor getValue() {
                return TeamColor.matchColor(team.getFlag());
            }

            @Override
            public void setValue(TeamColor value) {
                team.setFlag(value.toString());
            }
        }, teams) {
            @Override
            public @NotNull ItemStack getDisplayItem() {
                ItemStack stack = super.getDisplayItem();
                stack.setType(getTeamMaterial());
                return stack;
            }
        });

        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    private Material getTeamMaterial() {
        TeamColor teamColor = TeamColor.matchColor(team.getFlag());

        if (teamColor != null) {
            return teamColor.getDisplaMaterial();
        } else {
            Main.getPlugin().getComponentLogger().warn("Couldn't get TeamColor for " + team);
            return Material.BARRIER;
        }
    }

    @Override
    public boolean PlayerNeeded() {
        return true;
    }

}
