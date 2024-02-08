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
import org.apache.commons.text.WordUtils;
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
    public void describe(Map<String, Object> out) {
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
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return player.getTeam() != null && player.getTeam().getColor().toString().equals(team.getFlag());
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, Region region) {
        if (player == null || !player.isInMinigame()) return false;
        return player.getTeam() != null && player.getTeam().getColor().toString().equals(team.getFlag());
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        team.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        team.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, getDisplayName(), player);
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);

        List<String> teams = new ArrayList<>(TeamColor.validColorNames());

        m.addItem(new MenuItemList<String>(getTeamMaterial(), "Team Color", new Callback<>() {
            @Override
            public String getValue() {
                return WordUtils.capitalize(team.getFlag().replace("_", " "));
            }

            @Override
            public void setValue(String value) {
                team.setFlag(value.toUpperCase().replace(" ", "_"));
            }
        }, teams) {
            @Override
            public ItemStack getItem() {
                ItemStack stack = super.getItem();
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
    public boolean onPlayerApplicable() {
        return true;
    }

}
