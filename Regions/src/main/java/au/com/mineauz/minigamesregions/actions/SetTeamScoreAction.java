package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetTeamScoreAction extends ScoreAction {
    private final IntegerFlag score = new IntegerFlag(1, "amount");
    private final StringFlag team = new StringFlag("NONE", "team");

    @Override
    public @NotNull String getName() {
        return "SET_TEAM_SCORE";
    }

    @Override
    public @NotNull String getCategory() {
        return "Team Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        out.put("Score", score.getFlag());
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
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer,
                                    @NotNull Region region) {
        debug(mgPlayer, region);
        executeAction(mgPlayer);
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer,
                                  @NotNull Node node) {
        debug(mgPlayer, node);
        executeAction(mgPlayer);
    }

    private void executeAction(MinigamePlayer player) {
        if (player == null || !player.isInMinigame()) return;
        if (player.getTeam() != null && team.getFlag().equals("NONE")) {
            player.getTeam().setScore(score.getFlag());
        } else if (!team.getFlag().equals("NONE")) {
            TeamsModule tm = TeamsModule.getMinigameModule(player.getMinigame());
            if (tm.hasTeam(TeamColor.valueOf(team.getFlag()))) {
                tm.getTeam(TeamColor.valueOf(team.getFlag())).setScore(score.getFlag());
            }
        }
        checkScore(player);
    }


    @Override
    public void saveArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        score.saveValue(path, config);
        team.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        score.loadValue(path, config);
        team.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Set Team Score", mgPlayer);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(score.getMenuItem("Set Score Amount", Material.STONE, null, null));

        List<String> teams = new ArrayList<>(TeamColor.colorNames());
        m.addItem(new MenuItemList("Specific Team", List.of("If 'None', the players", "team will be used"), Material.PAPER, new Callback<>() {

            @Override
            public String getValue() {
                return WordUtils.capitalizeFully(team.getFlag());
            }

            @Override
            public void setValue(String value) {
                team.setFlag(value.toUpperCase());
            }
        }, teams));
        m.displayMenu(mgPlayer);
        return true;
    }

}
