package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import com.google.common.base.Strings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContainsEntityCondition extends ConditionInterface {
    private EnumFlag<EntityType> entityType = new EnumFlag<>(EntityType.PLAYER, "entity");
    
    private BooleanFlag matchName = new BooleanFlag(false, "matchName");
    private StringFlag customName = new StringFlag(null, "name");
    
    @Override
    public String getName() {
        return "CONTAINS_ENTITY";
    }

    @Override
    public String getCategory() {
        return "World Conditions";
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return false;
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, Region region) {
        Collection<? extends Entity> entities = region.getFirstPoint().getWorld().getEntitiesByClass(entityType.getFlag().getEntityClass());
        
        Pattern namePattern = null;
        if (matchName.getFlag()) {
            namePattern = createNamePattern();
        }
        
        Location temp = new Location(null, 0, 0, 0);
        for (Entity entity : entities) {
            if (entity.getType() == entityType.getFlag() && region.locationInRegion(entity.getLocation(temp))) {
                if (matchName.getFlag()) {
                    Matcher m = namePattern.matcher(Strings.nullToEmpty(entity.getCustomName()));
                    if (!m.matches()) {
                        continue;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    private Pattern createNamePattern() {
        String name = customName.getFlag();
        if (name == null) {
            return Pattern.compile(".*");
        }
        
        StringBuffer buffer = new StringBuffer();
        int start = 0;
        int index = 0;

        PlayerHasItemCondition.createPattern(name, buffer, start);

        return Pattern.compile(buffer.toString());
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return false;
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        entityType.saveValue(path, config);
        matchName.saveValue(path, config);
        customName.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        entityType.loadValue(path, config);
        matchName.loadValue(path, config);
        customName.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu menu = new Menu(3, "Contains Entity", player);

        menu.addItem(new MenuItemEnum<>("Entity Type", Material.CHICKEN_SPAWN_EGG, entityType.getCallback(), EntityType.class));
        
        menu.addItem(new MenuItemNewLine());
        
        menu.addItem(matchName.getMenuItem("Match Display Name", Material.NAME_TAG));
        MenuItemString menuItem = (MenuItemString)customName.getMenuItem("Display Name", Material.NAME_TAG, MinigameUtils.stringToList("The name to match.;Use % to do a wildcard match"));
        menuItem.setAllowNull(true);
        menu.addItem(menuItem);
        
        menu.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), menu.getSize() - 9);
        addInvertMenuItem(menu);
        menu.displayMenu(player);
        return true;
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Type", entityType.getFlag());
        if (matchName.getFlag()) {
            out.put("Match Name", customName.getFlag());
        }
    }
}
