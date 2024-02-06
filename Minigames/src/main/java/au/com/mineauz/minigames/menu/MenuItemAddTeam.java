package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MenuItemAddTeam extends MenuItem {
    private final TeamsModule tm;

    public MenuItemAddTeam(@NotNull LangKey name, @NotNull TeamsModule tm) {
        super(name, MenuUtility.getCreateMaterial());
        this.tm = tm;
    }

    @Override
    public ItemStack onClick() {
        MinigamePlayer mgPlayer = getContainer().getViewer();
        mgPlayer.setNoClose(true);
        mgPlayer.getPlayer().closeInventory();

        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TEAM_ADD,
                Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), TeamColor.validColorNamesComp()));
        mgPlayer.setManualEntry(this);

        getContainer().startReopenTimer(30);
        return null;
    }


    @Override
    public void checkValidEntry(String entry) {
        TeamColor col = TeamColor.matchColor(entry.toUpperCase().replace(" ", "_"));
        if (col != null) {
            if (!tm.hasTeam(col)) {
                tm.addTeam(col);
                Team team = tm.getTeam(col);

                getContainer().addItem(new MenuItemTeam(team.getColoredDisplayName(), team));
            } else {
                MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MinigameLangKey.TEAM_ERROR_COLOR_TAKEN);
            }

            List<String> teams = new ArrayList<>(tm.getTeams().size() + 1);
            for (Team t : tm.getTeams()) {
                teams.add(WordUtils.capitalize(t.getColor().toString().replace("_", " ")));
            }
            teams.add("None");
            getContainer().removeItem(0);
            getContainer().addItem(new MenuItemList(MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_DEFAULTWINNINGTEAM_NAME), Material.PAPER, tm.getDefaultWinnerCallback(), teams), 0);

            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());
        } else {
            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());

            MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MinigameLangKey.TEAM_ERROR_COLOR_INVALID,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), entry));
        }
    }
}
