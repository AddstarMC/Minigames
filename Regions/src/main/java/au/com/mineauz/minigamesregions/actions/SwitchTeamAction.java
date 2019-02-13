package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created for the AddstarMC
 * Created by Narimm on 10/10/2017.
 */
public class SwitchTeamAction extends AbstractAction {

    private StringFlag teamto = new StringFlag("ALL", "To");
    private StringFlag teamfrom = new StringFlag("ALL", "From");


    @Override
    public String getName() {
        return "SWITCH_TEAM";
    }

    @Override
    public String getCategory() {
        return "Team Actions";
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Team From", teamfrom.getFlag());
        out.put("Team To", teamto.getFlag());
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
    public void executeRegionAction(MinigamePlayer player, Region region) {
        executeAction(player);
    }

    @Override
    public void executeNodeAction(MinigamePlayer player, Node node) {
        executeAction(player);
    }

    private void executeAction(MinigamePlayer player){
        if(player == null || !player.isInMinigame()) return;
        if(teamfrom.getFlag().equals("NONE"))return;
        if(!teamfrom.getFlag().equals("ALL") || !teamfrom.getFlag().equals(player.getTeam().getColor().toString()))return;
        if (teamto.getFlag().equals("ALL")) {
            List<Team> teams = TeamsModule.getMinigameModule(player.getMinigame()).getTeams();
            Collections.shuffle(teams);
            for(Team t: teams){
                if(t != player.getTeam()){
                    player.setTeam(t);
                    return;
                }
            }

        }else{
            if(teamto.getFlag().equals("NONE")){
                player.setTeam(null);
            }
        }
        for(Team t : TeamsModule.getMinigameModule(player.getMinigame()).getTeams()){
            if (t.getColor().toString().equals(teamto.getFlag())){
                player.setTeam(t);
            }
        }
    }


    @Override
    public void saveArguments(FileConfiguration config, String path) {

    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {

    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Switch Team", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        List<String> teams = new ArrayList<>();
        teams.add("All");
        for(TeamColor team : TeamColor.values()){
            teams.add(MinigameUtils.capitalize(team.toString()));
        }
        teams.add("None");
        m.addItem(new MenuItemList("Switch From:", MinigameUtils.stringToList("If 'ALL' will switch on everyone, otherwise specific team."), Material.PAPER, new Callback<String>() {

            @Override
            public void setValue(String value) {
                teamfrom.setFlag(value.toUpperCase());
            }

            @Override
            public String getValue() {
                return MinigameUtils.capitalize(teamfrom.getFlag());
            }
        }, teams));
        m.addItem(new MenuItemList("Switch To:", MinigameUtils.stringToList("If 'None' will set the player to no team, otherwise specific team.  If All - will randomly chose a team"), Material.PAPER, new Callback<String>() {

            @Override
            public void setValue(String value) {
                teamto.setFlag(value.toUpperCase());
            }

            @Override
            public String getValue() {
                return MinigameUtils.capitalize(teamto.getFlag());
            }
        }, teams));
        m.displayMenu(player);
        return true;
    }
}
