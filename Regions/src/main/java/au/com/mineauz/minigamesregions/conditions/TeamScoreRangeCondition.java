package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamScoreRangeCondition extends ConditionInterface {
    
    private IntegerFlag min = new IntegerFlag(5, "min");
    private IntegerFlag max = new IntegerFlag(10, "max");
    private StringFlag team = new StringFlag("NONE", "team");

    @Override
    public String getName() {
        return "TEAM_SCORE_RANGE";
    }

    @Override
    public String getCategory() {
        return "Team Conditions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        out.put("Score", min.getFlag() + " - " + max.getFlag());
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
    public boolean checkRegionCondition(MinigamePlayer player, Region region) {
        return checkCondition(player);
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return checkCondition(player);
    }
    
    private boolean checkCondition(MinigamePlayer player){
        if (player == null || !player.isInMinigame()) {
            return false;
        }
        
        Team team;
        if (player.getTeam() != null && this.team.getFlag().equals("NONE")) {
            team = player.getTeam();
        } else if (!this.team.getFlag().equals("NONE")) {
            TeamsModule tm = TeamsModule.getMinigameModule(player.getMinigame());
            team = tm.getTeam(TeamColor.valueOf(this.team.getFlag()));
        } else {
            team = null;
        }
        
        if (team != null) {
            return team.getScore() >= min.getFlag() && team.getScore() <= max.getFlag();
        } else {
            return false;
        }
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        min.saveValue(path, config);
        max.saveValue(path, config);
        team.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        min.loadValue(path, config);
        max.loadValue(path, config);
        team.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Team Score Range", player);
        m.addItem(min.getMenuItem("Minimum Score", Material.STONE_SLAB, 0, null));
        m.addItem(max.getMenuItem("Maximum Score", Material.STONE, 0, null));
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
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }
}
