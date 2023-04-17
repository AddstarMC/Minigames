package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchTeamCondition extends ConditionInterface {

    private final StringFlag team = new StringFlag("RED", "team");

    @Override
    public String getName() {
        return "MATCH_TEAM";
    }

    @Override
    public String getCategory() {
        return "Team Conditions";
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
        Menu m = new Menu(3, "Match Team", player);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        List<String> teams = new ArrayList<>();
        for (TeamColor t : TeamColor.values())
            teams.add(WordUtils.capitalize(t.toString().replace("_", " ")));

        m.addItem(new MenuItemList("Team Color", getTeamMaterial(), new Callback<>() {

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
        return switch (team.getFlag()) {
            case "RED" -> Material.RED_WOOL;
            case "BLUE" -> Material.BLUE_WOOL;
            case "GREEN" -> Material.GREEN_WOOL;
            case "YELLOW" -> Material.YELLOW_WOOL;
            case "PURPLE" -> Material.PURPLE_WOOL;
            case "BLACK" -> Material.BLACK_WOOL;
            case "DARK_RED" -> Material.RED_CONCRETE;
            case "DARK_BLUE" -> Material.BLUE_CONCRETE;
            case "DARK_GREEN" -> Material.GREEN_CONCRETE;
            case "DARK_PURPLE" -> Material.PURPLE_CONCRETE;
            case "GRAY" -> Material.GRAY_WOOL;
            default -> Material.WHITE_WOOL;
        };
    }

    @Override
    public boolean onPlayerApplicable() {
        return true;
    }

}
