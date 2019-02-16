package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Menu {
    private int rows = 2;
    private ItemStack[] pageView;
    private Map<Integer, MenuItem> pageMap = new HashMap<>();
    private String name = "Menu";
    private boolean allowModify = false;
    private Menu previousPage = null;
    private Menu nextPage = null;
    private MinigamePlayer viewer = null;
    private int reopenTimerID = -1;
    private Inventory inv = null;

    public Menu(int rows, String name, MinigamePlayer viewer) {
        if (rows > 6)
            rows = 6;
        else if (rows < 2)
            rows = 2;
        this.rows = rows;
        this.name = name;
        pageView = new ItemStack[rows * 9];
        this.viewer = viewer;
    }

    public String getName() {
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
            } else if (m.getClicked(inc).getName() != null && ChatColor.stripColor(m.getClicked(inc).getName()).equals("NL")) {
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
            if (it.getName() != null && ChatColor.stripColor(it.getName()).equals("NL")) {
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
        addItem(new MenuItemPage("Next Page", MenuUtility.getBackMaterial(), nextPage), 9 * (rows - 1) + 5);
        setNextPage(nextPage);
        nextPage.setPreviousPage(this);
        nextPage.addItem(new MenuItemPage("Previous Page", MenuUtility.getBackMaterial(), this), 9 * (rows - 1) + 3);
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
                pageView[key] = pageMap.get(key).getItem();
        }
    }

    private void updateAll() {
        for (MenuItem item : pageMap.values()) {
            item.update();
        }
    }

    public void displayMenu(MinigamePlayer ply) {
        updateAll();
        populateMenu();
        Player player = ply.getPlayer();

        inv = Bukkit.createInventory(player, rows * 9, name);
        inv.setContents(pageView);
        ply.getPlayer().openInventory(inv);
        ply.setMenu(this);
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
        }, (long) (time * 20));
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
