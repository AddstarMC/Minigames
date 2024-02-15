package au.com.mineauz.minigames.stats;

import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class allows you to register stats that are usable in scoreboards
 */
public final class MinigameStatistics {
    public static final MinigameStat Wins = new BasicMinigameStat("wins", MinigameLangKey.STATISTIC_WINS_NAME, StatFormat.Total);
    public static final MinigameStat Losses = new BasicMinigameStat("losses", MinigameLangKey.STATISTIC_LOSSES_NAME, StatFormat.Total); // Fake stat
    public static final MinigameStat Attempts = new BasicMinigameStat("attempts", MinigameLangKey.STATISTIC_ATTEMPTS_NAME, StatFormat.Total);
    // in milliseconds
    public static final MinigameStat CompletionTime = new BasicMinigameStat("time", MinigameLangKey.STATISTIC_TIME_NAME, StatFormat.MinMaxAndTotal);

    public static final MinigameStat Kills = new BasicMinigameStat("kills", MinigameLangKey.STATISTIC_KILLS_NAME, StatFormat.MaxAndTotal);
    public static final MinigameStat Deaths = new BasicMinigameStat("deaths", MinigameLangKey.STATISTIC_DEATHS_NAME, StatFormat.MinAndTotal);
    public static final MinigameStat Score = new BasicMinigameStat("score", MinigameLangKey.STATISTIC_SCORE_NAME, StatFormat.MaxAndTotal);
    public static final MinigameStat Reverts = new BasicMinigameStat("reverts", MinigameLangKey.STATISTIC_REVERTS_NAME, StatFormat.MinAndTotal);

    private static final Map<String, MinigameStat> stats = new HashMap<>();

    static {
        registerStat0(Wins);
        registerStat0(Losses);
        registerStat0(Attempts);
        registerStat0(CompletionTime);
        registerStat0(Kills);
        registerStat0(Deaths);
        registerStat0(Score);
        registerStat0(Reverts);
    }

    private MinigameStatistics() {
    }

    /**
     * Registers a new stat that is automatically saved and made available to scoreboards
     *
     * @param stat The stat to add. The name of the stat must be unique and must only contain only letters and numbers
     * @throws IllegalArgumentException Thrown if the stat name is not unique or contains invalid characters
     */
    public static void registerStat(DynamicMinigameStat stat) throws IllegalArgumentException {
        registerStat0(stat);
    }

    private static void registerStat0(MinigameStat stat) throws IllegalArgumentException {
        String name = stat.getName().toLowerCase();

        // Validity tests
        if (!isNameValid(name)) {
            throw new IllegalArgumentException("Invalid name '" + stat.getName() + "' for stat.");
        }

        if (stats.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate stat '" + stat.getName() + "'");
        }

        // Add the stat
        stats.put(name, stat);
    }

    private static boolean isNameValid(String name) {
        for (char c : name.toCharArray()) {
            if (!Character.isDigit(c) && !Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets a stat by name
     *
     * @param name The name of the stat case-insensitive
     * @return The found stat or null
     */
    public static MinigameStat getStat(String name) {
        return stats.get(name.toLowerCase());
    }

    /**
     * Checks if a stat exists
     *
     * @param name The name of the stat case-insensitive
     * @return True if it exists
     */
    public static boolean hasStat(String name) {
        return stats.containsKey(name.toLowerCase());
    }

    /**
     * Removes a previously registered stat. This can remove
     * any stat added through {@link #registerStat(DynamicMinigameStat)}
     *
     * @param name The name of the stat case-insensitive
     * @return True if a stat was removed
     */
    public static boolean removeStat(String name) {
        MinigameStat stat = stats.get(name.toLowerCase());

        if (stat instanceof DynamicMinigameStat) {
            return stats.remove(name.toLowerCase()) != null;
        } else {
            return false;
        }
    }

    /**
     * @return Returns an unmodifiable map of all registered stats
     */
    public static Map<String, MinigameStat> getAllStats() {
        return Collections.unmodifiableMap(stats);
    }

    /**
     * @return Returns all dynamic stats
     */
    public static Iterable<DynamicMinigameStat> getDynamicStats() {
        return stats.values().stream()
                .filter(DynamicMinigameStat.class::isInstance)
                .map(DynamicMinigameStat.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Creates a menu that allows you to select a statistic
     *
     * @param parent       The parent menu
     * @param statCallback The callback to be invoked when the statistic is chosen. Note: only the setValue() method will be called.
     * @return The menu to display
     */
    public static Menu createStatSelectMenu(final Menu parent, final Callback<MinigameStat> statCallback) {
        final Menu submenu = new Menu(6, MgMenuLangKey.MENU_STAT_SELECT_NAME, parent.getViewer());

        for (final MinigameStat stat : getAllStats().values()) {
            MenuItemCustom item = new MenuItemCustom(Material.WRITABLE_BOOK, stat.getDisplayName());
            item.setClick(() -> {
                statCallback.setValue(stat);
                parent.displayMenu(submenu.getViewer());
                return null;
            });

            submenu.addItem(item);
        }

        submenu.addItem(new MenuItemBack(parent), submenu.getSize() - 9);
        return submenu;
    }

    /**
     * Creates a menu that allows you to select a statistic field
     *
     * @param parent   The parent menu
     * @param format   The format to define the fields available
     * @param callback The callback to be invoked when the field is chosen. Note: only the setValue() method will be called.
     * @return The menu to display
     */
    public static Menu createStatFieldSelectMenu(final Menu parent, StatFormat format, final Callback<StatisticValueField> callback) {
        final Menu submenu = new Menu(6, MgMenuLangKey.MENU_STAT_SELECT_FIELD_NAME, parent.getViewer());

        for (final StatisticValueField field : format.getFields()) {
            MenuItemCustom item = new MenuItemCustom(Material.PAPER, field.getTitle());
            item.setClick(() -> {
                callback.setValue(field);
                parent.displayMenu(submenu.getViewer());
                return null;
            });

            submenu.addItem(item);
        }

        submenu.addItem(new MenuItemBack(parent), submenu.getSize() - 9);
        return submenu;
    }
}
