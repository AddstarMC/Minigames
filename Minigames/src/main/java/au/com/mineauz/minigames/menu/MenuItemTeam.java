package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Team.OptionStatus;

import java.util.ArrayList;
import java.util.List;

public class MenuItemTeam extends MenuItem {

    private final Team team;

    public MenuItemTeam(String name, Team team) {
        super(name, Material.LEATHER_CHESTPLATE);

        setDescription(MinigameUtils.stringToList(ChatColor.DARK_PURPLE + "(Right Click to delete)"));
        this.team = team;
        setTeamIcon();
    }

    public MenuItemTeam(String name, List<String> description, Team team) {
        super(name, description, Material.LEATHER_CHESTPLATE);

        getDescription().add(0, ChatColor.DARK_PURPLE + "(Right Click to delete)");
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
        Menu m = new Menu(3, getName(), getContainer().getViewer());
        m.addItem(new MenuItemString("Display Name", Material.NAME_TAG, new Callback<>() {

            @Override
            public String getValue() {
                return team.getDisplayName();
            }

            @Override
            public void setValue(String value) {
                team.setDisplayName(value);
            }


        }));
        m.addItem(new MenuItemInteger("Max Players", Material.STONE, new Callback<>() {

            @Override
            public Integer getValue() {
                return team.getMaxPlayers();
            }

            @Override
            public void setValue(Integer value) {
                team.setMaxPlayers(value);
            }


        }, 0, null));
        for (Flag<?> flag : team.getFlags()) {
            switch (flag.getName()) {
                case "assignMsg" -> m.addItem(flag.getMenuItem("Join Team Message", Material.PAPER,
                        MinigameUtils.stringToList("Message sent to player;when they join;the team.;Use %s for team name")));
                case "gameAssignMsg" -> m.addItem(flag.getMenuItem("Join Team Broadcast Message", Material.PAPER,
                        MinigameUtils.stringToList("Message sent to all players;when someone joins;a team.;Use %s for team/player name")));
                case "autobalanceMsg" -> m.addItem(flag.getMenuItem("Autobalance Message", Material.PAPER,
                        MinigameUtils.stringToList("Message sent to player;when they are;auto-balanced.;Use %s for team name")));
                case "gameAutobalanceMsg" -> m.addItem(flag.getMenuItem("Autobalance Broadcast Message", Material.PAPER,
                        MinigameUtils.stringToList("Message sent to all players;when someone is;auto-balanced.;Use %s for team/player name")));
            }
        }
        List<String> ntvo = new ArrayList<>();
        for (OptionStatus v : OptionStatus.values()) {
            ntvo.add(v.toString());
        }
        m.addItem(new MenuItemList("NameTag Visibility", Material.NAME_TAG, team.getNameTagVisibilityCallback(), ntvo));
        m.addItem(new MenuItemBoolean("Auto Balance Team", Material.PAPER, team.getAutoBalanceCallBack()));

        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), getContainer()), m.getSize() - 9);
        m.displayMenu(getContainer().getViewer());
        return null;
    }

    @Override
    public ItemStack onRightClick() {
        TeamsModule.getMinigameModule(team.getMinigame()).removeTeam(team.getColor());
        getContainer().removeItem(getSlot());
        return null;
    }
}
