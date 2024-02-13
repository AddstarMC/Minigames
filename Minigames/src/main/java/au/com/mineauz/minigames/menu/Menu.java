package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class Menu {
    private final int rows;
    private final ItemStack[] pageView;
    private final TreeMap<Integer, MenuItem> pageMap = new TreeMap<>(); // sorts by index
    private final Component name;
    private final MinigamePlayer viewer;
    private boolean allowModify = false;
    private Menu previousPage = null;
    private Menu nextPage = null;
    private int reopenTimerID = -1;
    private Inventory inv = null;

    public Menu(int rows, @NotNull LangKey langKey, @NotNull MinigamePlayer viewer) {
        if (rows > 6)
            rows = 6;
        else if (rows < 2)
            rows = 2;
        this.rows = rows;
        this.name = MinigameMessageManager.getMgMessage(langKey);
        pageView = new ItemStack[rows * 9];
        this.viewer = viewer;
    }

    public Menu(int rows, Component name, @NotNull MinigamePlayer viewer) {
        if (rows > 6) {
            rows = 6;
        } else if (rows < 2) {
            rows = 2;
        }
        this.rows = rows;
        this.name = name;
        pageView = new ItemStack[rows * 9];
        this.viewer = viewer;
    }

    public Component getName() {
        return name;
    }

    public boolean addItem(MenuItem item, int slot) {
        if (!pageMap.containsKey(slot) && slot < pageView.length) {
            item.setContainer(this);
            item.setSlot(slot);
            pageMap.put(slot, item);
            if (inv != null) {
                inv.setItem(slot, item.getItem());
            }
            return true;
        }
        return false;
    }

    private boolean isNewLine(@NotNull MenuItem menuItem) {
        return menuItem instanceof MenuItemNewLine;
    }

    public void addItem(MenuItem item) {
        int inc = 0;
        Menu menu = this;
        int maxItems = 9 * (rows - 1);

        while (true) {
            if (inc >= maxItems) {
                if (menu.getNextPage() == null)
                    menu.addPage();

                menu = menu.getNextPage();
                inc = 0;
            }

            if (menu.getMenuItem(inc) == null) {
                menu.addItem(item, inc);
                break;
            } else if (isNewLine(menu.getMenuItem(inc))) {
                // jump to next line, aka where inc % 9 == 0
                inc += 9 - inc % 9;
            } else {
                inc++;
            }
        }
    }

    /**
     * Danger! if this Menu already contains items some of the new ones might not get added! <-- todo solve this!
     */
    public void addItems(@NotNull List<@NotNull MenuItem> items) {
        Menu curPage = this;
        int inc = 0;
        for (MenuItem it : items) {
            if (isNewLine(it)) {
                curPage.addItem(it, inc);
                // jump to next line, aka where inc % 9 == 0
                inc += 9 - inc % 9;
            } else {
                curPage.addItem(it, inc);
                inc++;
            }
            if (inc >= (9 * (rows - 1))) {
                inc = 0;
                if (curPage.getNextPage() == null && items.indexOf(it) < items.size()) {
                    curPage.addPage();
                }
                curPage = curPage.getNextPage();
            }
        }
    }

    protected void addPage() {
        Menu nextPage = new Menu(rows, name, viewer);
        addItem(new MenuItemPage(MenuUtility.getBackMaterial(), MgMenuLangKey.MENU_PAGE_NEXT, nextPage), 9 * (rows - 1) + 5);
        setNextPage(nextPage);
        nextPage.setPreviousPage(this);
        nextPage.addItem(new MenuItemPage(MenuUtility.getBackMaterial(), MgMenuLangKey.MENU_PAGE_PREVIOUS, this), 9 * (rows - 1) + 3);
        for (int j = 9 * (rows - 1) + 6; j < 9 * rows; j++) {
            if (getMenuItem(j) != null)
                nextPage.addItem(getMenuItem(j), j);
        }
    }

    public void removeItem(int slot) {
        if (pageMap.containsKey(slot)) {
            pageMap.remove(slot);
            pageView[slot] = null;
            if (inv != null) {
                inv.setItem(slot, null);
            }
        }
    }

    public void clearMenu() {
        for (Integer i : new ArrayList<>(pageMap.keySet())) {
            pageMap.remove(i);
            pageView[i] = null;
        }
    }

    public void addItemStack(ItemStack item, int slot) {
        inv.setItem(slot, item);
    }

    private void populateMenu() {
        for (Integer key : pageMap.keySet()) {
            if (!(pageMap.get(key) instanceof MenuItemNewLine))
                pageView[key] = pageMap.get(key).getItem();
        }
    }

    private void updateAll() {
        for (MenuItem item : pageMap.values()) {
            item.update();
        }
    }

    public void displayMenu(final @NotNull MinigamePlayer mgPlayer) {
        Menu t = this;
        Player player = mgPlayer.getPlayer();
        updateAll();
        populateMenu();
        inv = Bukkit.createInventory(player, rows * 9, name);
        inv.setContents(pageView);
        // Some calls of displayMenu are async, which is not allowed.
        Minigames.getPlugin().getServer().getScheduler().runTask(Minigames.getPlugin(), () -> {
            mgPlayer.getPlayer().openInventory(inv);
            mgPlayer.setMenu(t);
        });
    }

    public boolean getAllowModify() {
        return allowModify;
    }

    public void setAllowModify(boolean canModify) {
        allowModify = canModify;
    }

    public MenuItem getMenuItem(int slot) {
        return pageMap.get(slot);
    }

    public boolean hasMenuItem(int slot) {
        return pageMap.containsKey(slot);
    }

    public int getSize() {
        return rows * 9;
    }

    public Menu getNextPage() {
        return nextPage;
    }

    public void setNextPage(Menu page) {
        nextPage = page;
    }

    public boolean hasNextPage() {
        return nextPage != null;
    }

    public Menu getPreviousPage() {
        return previousPage;
    }

    public void setPreviousPage(Menu page) {
        previousPage = page;
    }

    public boolean hasPreviousPage() {
        return previousPage != null;
    }

    public MinigamePlayer getViewer() {
        return viewer;
    }

    public void startReopenTimer(int time) {
        reopenTimerID = Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.getPlugin(), () -> {
            viewer.setNoClose(false);
            viewer.setManualEntry(null);
            displayMenu(viewer);
        }, time * 20L);
    }

    public void cancelReopenTimer() {
        if (reopenTimerID != -1) {
            viewer.setNoClose(false);
            viewer.setManualEntry(null);
            Bukkit.getScheduler().cancelTask(reopenTimerID);
        }
    }

    public ItemStack[] getInventory() {
        ItemStack[] inv = new ItemStack[getSize()];

        for (int i = 0; i < this.inv.getContents().length; i++) {
            if (!pageMap.containsKey(i)) {
                inv[i] = this.inv.getContents()[i];
            }
        }

        return inv;
    }

    public Set<Integer> getUsedSlots() {
        return pageMap.keySet();
    }
}
