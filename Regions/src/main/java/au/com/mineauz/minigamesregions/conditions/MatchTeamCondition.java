package au.com.mineauz.minigamesregions.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class MatchTeamCondition extends ConditionInterface {
    
    private StringFlag team = new StringFlag("RED", "team");

    @Override
    public String getName() {
        return "MATCH_TEAM";
    }
    
    @Override
    public String getCategory(){
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
        if(player == null || !player.isInMinigame()) return false;
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
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        List<String> teams = new ArrayList<>();
        for(TeamColor t : TeamColor.values())
            teams.add(MinigameUtils.capitalize(t.toString().replace("_", " ")));
        m.addItem(new MenuItemList("Team Color", Material.WHITE_WOOL, new Callback<String>() {
            
            @Override
            public void setValue(String value) {
                team.setFlag(value.toUpperCase().replace(" ", "_"));
            }
            
            @Override
            public String getValue() {
                return MinigameUtils.capitalize(team.getFlag().replace("_", " "));
            }
        }, teams));
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

}
