package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.MaterialFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import com.google.common.base.Joiner;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerHasItemCondition extends ConditionInterface {
    
    private final MaterialFlag type = new MaterialFlag(Material.STONE, "type");
    private final StringFlag where = new StringFlag("ANYWHERE", "where");
    private final IntegerFlag slot = new IntegerFlag(0, "slot");
    
    private final BooleanFlag matchName = new BooleanFlag(false, "matchName");
    private final BooleanFlag matchLore = new BooleanFlag(false, "matchLore");
    
    private final StringFlag name = new StringFlag(null, "name");
    private final StringFlag lore = new StringFlag(null, "lore");

    @Override
    public String getName() {
        return "PLAYER_HAS_ITEM";
    }

    @Override
    public String getCategory() {
        return "Player Conditions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        out.put("Item", type.getFlag());
        out.put("Where", where.getFlag());
        out.put("Slot", slot.getFlag());
        
        if (matchName.getFlag()) {
            out.put("Name", name.getFlag());
        }
        
        if (matchLore.getFlag()) {
            out.put("Lore", lore.getFlag());
        }
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
        return check(player);
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return check(player);
    }
    
    private boolean check(MinigamePlayer player) {
        PositionType checkType = PositionType.ANYWHERE;
        try {
            checkType = PositionType.valueOf(where.getFlag().toUpperCase());
        }catch (IllegalArgumentException e){
            Minigames.log().warning(e.getMessage());
        }
        PlayerInventory inventory = player.getPlayer().getInventory();
        ItemStack[] searchItems;
        int startSlot;
        int endSlot;
        
        if (checkType == PositionType.ARMOR) {
            searchItems = inventory.getArmorContents();
            startSlot = 0;
            endSlot = searchItems.length;
        } else {
            searchItems = inventory.getContents();
            
            if (checkType == PositionType.HOTBAR) {
                startSlot = 0;
                endSlot = 9;
            } else if (checkType == PositionType.MAIN) {
                startSlot = 9;
                endSlot = 36;
            } else if (checkType == PositionType.SLOT) {
                startSlot = slot.getFlag();
                endSlot = startSlot + 1;
            } else {
                startSlot = 0;
                endSlot = searchItems.length;
            }
        }
        
        Material material = type.getFlag();
        
        Pattern namePattern = null;
        Pattern lorePattern = null;
        
        if (matchName.getFlag()) {
            namePattern = createNamePattern();
        }
        
        if (matchLore.getFlag()) {
            lorePattern = createLorePattern();
        }
        int i =0;
        for(ItemStack slot: searchItems){
            if((i<startSlot) && (i>endSlot))
                continue;
            if (slot.getType() == material) {
                
                ItemMeta meta = slot.getItemMeta();
                
                if (namePattern !=null) {
                    Matcher m = namePattern.matcher(meta.getDisplayName());
                    if (!m.matches()) {
                        continue;
                    }
                }
                
                if (matchLore.getFlag()) {
                    if (lorePattern != null) {
                        Matcher m = lorePattern.matcher(Joiner.on('\n').join(meta.getLore()));
                        if (!m.matches()) {
                            continue;
                        }
                    } else {
                        // Only an unset lore pattern can match this
                        if (lore.getFlag() != null) {
                            continue;
                        }
                    }
                }
                
                // This item completely matches
                return true;
            }
        }
        return false;
    }
    
    private Pattern createNamePattern() {
        String name = this.name.getFlag();
        if (name == null) {
            return Pattern.compile(".*");
        }
        
        StringBuffer buffer = new StringBuffer();
        int start = 0;
        int index = 0;

        createPattern(name, buffer, start);

        return Pattern.compile(buffer.toString());
    }
    
    private Pattern createLorePattern() {
        String lore = this.lore.getFlag();
        if (lore == null) {
            return Pattern.compile(".*");
        }
        
        lore = lore.replace(';', '\n');
        
        StringBuffer buffer = new StringBuffer();
        int start = 0;
        int index = 0;
        createPattern(lore, buffer, start);
        return Pattern.compile(buffer.toString());
    }

    static void createPattern(String value, StringBuffer buffer, int start) {
        int index;
        while (true) {
            index = value.indexOf('%', start);
            // End of input, append the rest
            if (index == -1) {
                buffer.append(Pattern.quote(value.substring(start)));
                break;
            }

            // Append the start
            buffer.append(Pattern.quote(value.substring(start, index)));

            // Append the wildcard code
            buffer.append(".*?");

            // Move to next position
            start = index + 1;
        }
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        type.saveValue(path, config);
        where.saveValue(path, config);
        slot.saveValue(path, config);
        
        matchName.saveValue(path, config);
        matchLore.saveValue(path, config);
        name.saveValue(path, config);
        lore.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        type.loadValue(path, config);
        where.loadValue(path, config);
        slot.loadValue(path, config);
        
        matchName.loadValue(path, config);
        matchLore.loadValue(path, config);
        name.loadValue(path, config);
        lore.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Player Has Item", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        final MinigamePlayer fply = player;
        Material display = type.getFlag();
        if(display == null)display = Material.STONE;
        m.addItem(new MenuItemMaterial("Item",display, new Callback<Material>() {
            
            @Override
            public void setValue(Material value) {
                    type.setFlag(value);
            }
            
            @Override
            public Material getValue() {
                return type.getFlag();
            }
        }));
        m.addItem(new MenuItemList("Search Where", Material.COMPASS, new Callback<String>() {
            @Override
            public void setValue(String value) {
                where.setFlag(value.toUpperCase());
            }
            
            @Override
            public String getValue() {
                return WordUtils.capitalizeFully(where.getFlag());
            }
        }, Arrays.asList("Anywhere", "Hotbar", "Main", "Armor", "Slot")));
        m.addItem(slot.getMenuItem("Slot", Material.DIAMOND, 0, 35));
        m.addItem(new MenuItemNewLine());
        
        m.addItem(matchName.getMenuItem("Match Display Name", Material.NAME_TAG));
        MenuItemString menuItem = (MenuItemString)name.getMenuItem("Display Name", Material.NAME_TAG, MinigameUtils.stringToList("The name to match.;Use % to do a wildcard match"));
        menuItem.setAllowNull(true);
        m.addItem(menuItem);
        
        m.addItem(matchLore.getMenuItem("Match Lore", Material.BOOK));
        menuItem = (MenuItemString)lore.getMenuItem("Lore", Material.BOOK, MinigameUtils.stringToList("The lore to match. Separate;with semi-colons;for new lines.;Use % to do a wildcard match"));
        menuItem.setAllowNull(true);
        m.addItem(menuItem);
        
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }
    
    private enum PositionType {
        ANYWHERE,
        HOTBAR,
        MAIN,
        ARMOR,
        SLOT
    }

}
