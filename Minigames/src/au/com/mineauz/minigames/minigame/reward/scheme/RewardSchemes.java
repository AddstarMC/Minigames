package au.com.mineauz.minigames.minigame.reward.scheme;

import java.util.List;

import org.bukkit.Material;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.Property;

import com.google.common.base.Converter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

public final class RewardSchemes {
	private static BiMap<String, Class<? extends RewardScheme>> definedSchemes = HashBiMap.create();
	
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
			return type.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
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
				return schemeClass.newInstance();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getName(Class<? extends RewardScheme> schemeClass) {
		return definedSchemes.inverse().get(schemeClass);
	}
	
	public static MenuItem newMenuItem(String name, Material displayItem, Property<Class<? extends RewardScheme>> property) {
		Converter<Class<? extends RewardScheme>, String> converter = new Converter<Class<? extends RewardScheme>, String>() {
			@Override
			protected String doForward(Class<? extends RewardScheme> a) {
				return definedSchemes.inverse().get(a);
			}
			
			@Override
			protected Class<? extends RewardScheme> doBackward(String b) {
				return definedSchemes.get(b.toLowerCase());
			}
		};
		
		return new MenuItemRewardScheme(name, displayItem, Properties.transform(property, converter));
	}
	
	private static List<String> getSchemesAsNameList() {
		return Lists.newArrayList(definedSchemes.keySet());
	}
	
	private static class MenuItemRewardScheme extends MenuItemList {
		public MenuItemRewardScheme(String name, Material displayItem, Property<String> property) {
			super(name, displayItem, property, getSchemesAsNameList());
		}
	}
}
