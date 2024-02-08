package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SwitchTeamAction extends AAction {
    private final StringFlag teamto = new StringFlag("ALL", "To");
    private final StringFlag teamfrom = new StringFlag("ALL", "From");

    protected SwitchTeamAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_SWITCHTEAM_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.TEAM;
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
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        executeAction(mgPlayer);
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {
        executeAction(mgPlayer);
    }

    private void executeAction(MinigamePlayer player) {
        if (player == null || !player.isInMinigame()) return;
        if (teamfrom.getFlag().equals("NONE")) return;
        if (!teamfrom.getFlag().equals("ALL") || !teamfrom.getFlag().equals(player.getTeam().getColor().toString()))
            return;
        if (teamto.getFlag().equals("ALL")) {
            List<Team> teams = TeamsModule.getMinigameModule(player.getMinigame()).getTeams();
            Collections.shuffle(teams);
            for (Team t : teams) {
                if (t != player.getTeam()) {
                    player.setTeam(t);
                    return;
                }
            }

        } else {
            if (teamto.getFlag().equals("NONE")) {
                player.setTeam(null);
            }
        }
        for (Team t : TeamsModule.getMinigameModule(player.getMinigame()).getTeams()) {
            if (t.getColor().toString().equals(teamto.getFlag())) {
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
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu prev) {
        Menu m = new Menu(3, getDisplayname(), mgPlayer);
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        List<String> teams = new ArrayList<>(TeamColor.colorNames());
        teams.add("All"); //todo ?
        m.addItem(new MenuItemList<String>("Switch From:", List.of("If 'ALL' will switch on everyone, otherwise specific team."), Material.PAPER, new Callback<>() {

            @Override
            public String getValue() {
                return WordUtils.capitalize(teamfrom.getFlag());
            }

            @Override
            public void setValue(String value) {
                teamfrom.setFlag(value.toUpperCase());
            }


        }, teams));
        m.addItem(new MenuItemList<String>("Switch To:", List.of("If 'None' will set the player to no team, otherwise specific team.  If All - will randomly chose a team"), Material.PAPER, new Callback<>() {

            @Override
            public String getValue() {
                return WordUtils.capitalize(teamto.getFlag());
            }

            @Override
            public void setValue(String value) {
                teamto.setFlag(value.toUpperCase());
            }


        }, teams));
        m.displayMenu(mgPlayer);
        return true;
    }
}
