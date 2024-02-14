package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MenuItemTeam extends MenuItem {
    private final @NotNull Team team;

    public MenuItemTeam(@Nullable Component name, @NotNull Team team) {
        super(Material.LEATHER_CHESTPLATE, name);

        setDescription(MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_DELETE_RIGHTCLICK));
        this.team = team;
        setTeamIcon();
    }

    public MenuItemTeam(@Nullable Component name, @NotNull List<@NotNull Component> description, @NotNull Team team) {
        super(Material.LEATHER_CHESTPLATE, name, description);

        getDescription().add(0, MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_DELETE_RIGHTCLICK));
        this.team = team;
        setTeamIcon();
    }

    private void setTeamIcon() {
        LeatherArmorMeta m = (LeatherArmorMeta) getItem().getItemMeta();
        switch (team.getColor()) {
            case RED -> m.setColor(Color.RED);
            case DARK_RED -> m.setColor(Color.MAROON);
            case ORANGE -> m.setColor(Color.ORANGE);
            case YELLOW -> m.setColor(Color.YELLOW);
            case GREEN -> m.setColor(Color.LIME);
            case DARK_GREEN -> m.setColor(Color.GREEN);
            case CYAN -> m.setColor(Color.TEAL);
            case LIGHT_BLUE -> m.setColor(Color.AQUA);
            case BLUE -> m.setColor(Color.BLUE);
            case DARK_BLUE -> m.setColor(Color.NAVY);
            case DARK_PURPLE -> m.setColor(Color.PURPLE);
            case PURPLE -> m.setColor(Color.FUCHSIA);
            case WHITE -> m.setColor(Color.WHITE);
            case GRAY -> m.setColor(Color.SILVER);
            case DARK_GRAY -> m.setColor(Color.GRAY);
            case BLACK -> m.setColor(Color.BLACK);
        }
        getItem().setItemMeta(m);
    }

    @Override
    public ItemStack onClick() {
        Menu menu = new Menu(3, getName(), getContainer().getViewer());
        menu.addItem(new MenuItemString(Material.NAME_TAG, MgMenuLangKey.MENU_DISPLAYNAME_NAME, new Callback<>() {

            @Override
            public String getValue() {
                return team.getDisplayName();
            }

            @Override
            public void setValue(String value) {
                team.setDisplayName(value);
            }
        }));
        menu.addItem(new MenuItemInteger(Material.STONE, MgMenuLangKey.MENU_TEAM_MAXPLAYERS, new Callback<>() {

            @Override
            public Integer getValue() {
                return team.getMaxPlayers();
            }

            @Override
            public void setValue(Integer value) {
                team.setMaxPlayers(value);
            }
        }, 0, null));


        menu.addItem(team.getPlayerAssignMessageFlag().getMenuItem(Material.PAPER, MgMenuLangKey.MENU_TEAM_ASSIGNMSG_NAME,
                MgMenuLangKey.MENU_TEAM_ASSIGNMSG_DESCRIPTION));
        menu.addItem(team.getAutoBalanceMsgFlag().getMenuItem(Material.PAPER, MgMenuLangKey.MENU_TEAM_AUTOBALANCEMSG_NAME,
                MgMenuLangKey.MENU_TEAM_AUTOBALANCEMSG_DESCRIPTION));
        menu.addItem(team.getGameAutoBalanceMsgFlag().getMenuItem(Material.PAPER, MgMenuLangKey.MENU_TEAM_GAMEAUTOBALANCEMSG_NAME,
                MgMenuLangKey.MENU_TEAM_GAMEAUTOBALANCEMSG_DESCRIPTION));

        menu.addItem(new MenuItemList<>(Material.NAME_TAG, MgMenuLangKey.MENU_TEAM_NAMEVISIBILITY_NAME, team.getNameTagVisibilityCallback(),
                Arrays.asList(Team.VisibilityMapper.values())));
        menu.addItem(new MenuItemBoolean(Material.PAPER, MgMenuLangKey.MENU_TEAM_AUTOBALANCE, team.getAutoBalanceCallBack()));

        menu.addItem(new MenuItemBack(getContainer()), menu.getSize() - 9);
        menu.displayMenu(getContainer().getViewer());
        return null;
    }

    @Override
    public ItemStack onRightClick() {
        TeamsModule.getMinigameModule(team.getMinigame()).removeTeam(team.getColor());
        getContainer().removeItem(getSlot());
        return null;
    }
}
