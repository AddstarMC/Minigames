package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MinigameTool {
    private final ItemStack tool;
    private @Nullable Minigame minigame = null;
    private ToolMode mode = null;
    private TeamColor team = null;

    public MinigameTool(ItemStack tool) {
        this.tool = tool;
        ItemMeta meta = tool.getItemMeta();
        if (meta.getLore() != null) {
            String mg = ChatColor.stripColor(meta.getLore().get(0)).replace("Minigame: ", "");
            if (Minigames.getPlugin().getMinigameManager().hasMinigame(mg))
                minigame = Minigames.getPlugin().getMinigameManager().getMinigame(mg);

            String md = ChatColor.stripColor(meta.getLore().get(1)).replace("Mode: ", "").replace(" ", "_");
            mode = ToolModes.getToolMode(md);

            team = TeamColor.matchColor(ChatColor.stripColor(meta.getLore().get(2).replace("Team: ", "")).toUpperCase());
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Minigame Tool");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "Minigame: " + ChatColor.WHITE + "None");
            lore.add(ChatColor.AQUA + "Mode: " + ChatColor.WHITE + "None");
            lore.add(ChatColor.AQUA + "Team: " + ChatColor.WHITE + "None");
            meta.setLore(lore);
            tool.setItemMeta(meta);
        }
    }

    public ItemStack getTool() {
        return tool;
    }

    public ToolMode getMode() {
        return mode;
    }

    public void setMode(@NotNull ToolMode mode) {
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(1, ChatColor.AQUA + "Mode: " + ChatColor.WHITE + WordUtils.capitalize(mode.getName().replace("_", " ")));
        meta.setLore(lore);
        tool.setItemMeta(meta);
        this.mode = mode;
    }

    public @Nullable Minigame getMinigame() {
        return minigame;
    }

    public void setMinigame(@NotNull Minigame minigame) {
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(0, ChatColor.AQUA + "Minigame: " + ChatColor.WHITE + minigame.getName(false));
        meta.setLore(lore);
        tool.setItemMeta(meta);
        this.minigame = minigame;
    }

    public TeamColor getTeam() {
        return team;
    }

    public void setTeam(TeamColor color) {
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (color == null) {
            lore.set(2, ChatColor.AQUA + "Team: " + ChatColor.WHITE + "None");
        } else {
            lore.set(2, ChatColor.AQUA + "Team: " + color.getColor() + WordUtils.capitalize(color.toString().replace("_", " ")));
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        team = color;
    }

    public void addSetting(String name, String setting) {
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(ChatColor.AQUA + name + ": " + ChatColor.WHITE + setting);
        meta.setLore(lore);
        tool.setItemMeta(meta);
    }

    public void changeSetting(String name, String setting) {
        removeSetting(name);
        addSetting(name, setting);
    }

    public @NotNull String getSetting(@NotNull String name) {
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();
        for (String l : lore) {
            if (ChatColor.stripColor(l).startsWith(name)) {
                return ChatColor.stripColor(l).replace(name + ": ", "");
            }
        }
        return "None";
    }

    public void removeSetting(String name) {
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();
        for (String l : new ArrayList<>(lore)) {
            if (ChatColor.stripColor(l).startsWith(name)) {
                lore.remove(l);
                break;
            }
        }
        meta.setLore(lore);
        tool.setItemMeta(meta);
    }

    public void openMenu(MinigamePlayer player) {
        Menu men = new Menu(2, "Set Tool Mode", player);

        final MenuItemCustom miselect = new MenuItemCustom(Material.DIAMOND_BLOCK, "Select", List.of("Selects and area", "or points visually"));
        final MenuItemCustom mideselect = new MenuItemCustom(Material.GLASS, "Deselect", List.of("Deselects an", "area or points"));
        final MinigamePlayer fply = player;
        miselect.setClick(() -> {
            if (mode != null) {
                mode.select(fply, minigame, TeamsModule.getMinigameModule(minigame).getTeam(team));
            }
            return miselect.getItem();
        });
        mideselect.setClick(() -> {
            if (mode != null) {
                mode.deselect(fply, minigame, TeamsModule.getMinigameModule(minigame).getTeam(team));
            }
            return mideselect.getItem();
        });

        men.addItem(mideselect, men.getSize() - 1);
        men.addItem(miselect, men.getSize() - 2);

        List<String> teams = new ArrayList<>(TeamColor.colorNames());

        men.addItem(new MenuItemToolTeam(Material.PAPER, "Team", new Callback<>() { //todo new MenuItemList("Lock to Team", Material.LEATHER_CHESTPLATE, loadout.getTeamColorCallback(), teams)

            @Override
            public String getValue() {
                if (getTeam() != null)
                    return WordUtils.capitalize(getTeam().toString().replace("_", " "));
                return "None";
            }

            @Override
            public void setValue(String value) {
                setTeam(TeamColor.matchColor(value.replace(" ", "_")));
            }
        }, teams), men.getSize() - 3);

        for (ToolMode toolMode : ToolModes.getToolModes()) {
            men.addItem(new MenuItemToolMode(toolMode.getIcon(), toolMode.getDisplayName(), toolMode.getDescription(), toolMode));
        }

        men.displayMenu(player);
    }
}
