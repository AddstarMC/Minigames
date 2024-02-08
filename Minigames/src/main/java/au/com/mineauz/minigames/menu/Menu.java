package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Menu {
    private final int rows;
    private final ItemStack[] pageView;
    private final Map<Integer, MenuItem> pageMap = new HashMap<>();
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
        if (rows > 6)
            rows = 6;
        else if (rows < 2)
            rows = 2;
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
                inv.setItem(slot, item.getDisplayItem());
            }
            return true;
        }
        return false;
    }

    private boolean isNewLine(@NotNull MenuItem menuItem) {
        return menuItem.getName() != null && PlainTextComponentSerializer.plainText().serialize(menuItem.getName()).equalsIgnoreCase("NL");
    }

    public void addItem(MenuItem item) {
        int inc = 0;
        Menu m = this;
        int maxItems = 9 * (rows - 1);
        while (true) {
            if (inc >= maxItems) {
                if (m.getNextPage() == null)
                    m.addPage();

                m = m.getNextPage();
                inc = 0;
            }

            if (m.getClicked(inc) == null) {
                m.addItem(item, inc);
                break;
            } else if (isNewLine(m.getClicked(inc))) {
                for (int i = 1; i < 10; i++) {
                    if ((inc + i) % 9 == 0) {
                        inc += i;
                        break;
                    }
                }
            } else
                inc++;
        }
    }

    public void addItems(List<MenuItem> items) {
        Menu curPage = this;
        int inc = 0;
        for (MenuItem it : items) {
            if (isNewLine(it)) {
                curPage.addItem(it, inc);
                for (int i = 1; i < 10; i++) {
                    if ((inc + i) % 9 == 0) {
                        inc += i;
                        break;
                    }
                }
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

    public void addPage() {
        Menu nextPage = new Menu(rows, name, viewer);
        addItem(new MenuItemPage(MenuUtility.getBackMaterial(), MgMenuLangKey.MENU_PAGE_NEXT, nextPage), 9 * (rows - 1) + 5);
        setNextPage(nextPage);
        nextPage.setPreviousPage(this);
        nextPage.addItem(new MenuItemPage(MenuUtility.getBackMaterial(), MgMenuLangKey.MENU_PAGE_PREVIOUS, this), 9 * (rows - 1) + 3);
        for (int j = 9 * (rows - 1) + 6; j < 9 * rows; j++) {
            if (getClicked(j) != null)
                nextPage.addItem(getClicked(j), j);
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
                pageView[key] = pageMap.get(key).getDisplayItem();
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

    public MenuItem getClicked(int slot) {
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

    public Set<Integer> getSlotMap() {
        return pageMap.keySet();
    }
}
