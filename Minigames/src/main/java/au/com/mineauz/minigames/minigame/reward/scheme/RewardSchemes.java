package au.com.mineauz.minigames.minigame.reward.scheme;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemList;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Material;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class RewardSchemes {
    private static final BiMap<String, Class<? extends RewardScheme>> definedSchemes = HashBiMap.create();

    static {
        addRewardScheme("standard", StandardRewardScheme.class);
        addRewardScheme("score", ScoreRewardScheme.class);
        addRewardScheme("time", TimeRewardScheme.class);
        addRewardScheme("kills", KillsRewardScheme.class);
        addRewardScheme("deaths", DeathsRewardScheme.class);
        addRewardScheme("reverts", RevertsRewardScheme.class);
    }

    public static void addRewardScheme(String name, Class<? extends RewardScheme> scheme) {
        definedSchemes.put(name.toLowerCase(), scheme);
    }

    public static <T extends RewardScheme> T createScheme(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static RewardScheme createScheme(String name) {
        try {
            Class<? extends RewardScheme> schemeClass = definedSchemes.get(name.toLowerCase());
            if (schemeClass == null) {
                return null;
            } else {
                return schemeClass.getDeclaredConstructor().newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getName(Class<? extends RewardScheme> schemeClass) {
        return definedSchemes.inverse().get(schemeClass);
    }

    public static MenuItem newMenuItem(String name, Material displayItem, Callback<Class<? extends RewardScheme>> callback) {
        return new MenuItemRewardScheme(name, displayItem, callback);
    }

    private static List<String> getSchemesAsNameList() {
        return new ArrayList<>(definedSchemes.keySet());
    }

    private static Callback<String> transformCallback(final Callback<Class<? extends RewardScheme>> callback) {
        return new Callback<>() {
            @Override
            public String getValue() {
                return definedSchemes.inverse().get(callback.getValue());
            }

            @Override
            public void setValue(String value) {
                callback.setValue(definedSchemes.get(value.toLowerCase()));
            }
        };
    }

    private static class MenuItemRewardScheme extends MenuItemList {
        public MenuItemRewardScheme(String name, Material displayItem, Callback<Class<? extends RewardScheme>> callback) {
            super(name, displayItem, transformCallback(callback), getSchemesAsNameList());
        }
    }
}
