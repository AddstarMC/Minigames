package au.com.mineauz.minigames;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class MultiplayerBets {
    private final Map<MinigamePlayer, ItemStack> itemBet = new HashMap<>();
    private double greatestMoneyBet = 0;
    private ItemStack greatestItemBet = new ItemStack(Material.AIR);
    private final Map<MinigamePlayer, Double> moneyBet = new HashMap<>();

    public MultiplayerBets() {
    }

    public void addBet(MinigamePlayer player, ItemStack item) {
        itemBet.put(player, item);
        greatestItemBet = item.clone();
    }

    public void addBet(MinigamePlayer player, Double money) {
        greatestMoneyBet = money;
        moneyBet.put(player, money);
    }

    public boolean hasAlreadyBet(MinigamePlayer player) {
        return itemBet.containsKey(player) || moneyBet.containsKey(player);
    }

    public boolean isHighestBetter(@Nullable Double money, @Nullable ItemStack item) {
        if (money != null) {
            return greatestMoneyBet == 0 || money >= greatestMoneyBet;
        } else if (item != null) {
            return itemBet.isEmpty() || betValueItem(item) >= betValueItem(getHighestItemBet());
        }

        return true;
    }

    /**
     * returns a set of all bettet items, where all amounts of similar items where added together
     */
    public HashSet<ItemStack> claimItemBets() {
        HashSet<ItemStack> resultItems = new HashSet<>();

        for (ItemStack itemToAdd : itemBet.values()) {
            boolean inResult = false;

            Iterator<ItemStack> it = resultItems.iterator();
            ItemStack alreadyIn = null;

            //count together item amounts, if nbt (except amount) matches
            while (it.hasNext() && !inResult) {
                alreadyIn = it.next();

                if (itemToAdd.isSimilar(alreadyIn)) {
                    alreadyIn.setAmount(alreadyIn.getAmount() + itemToAdd.getAmount());

                    it.remove();
                    inResult = true;
                }
            }

            // readd item or add new one
            if (inResult) {
                resultItems.add(alreadyIn);
            } else {
                resultItems.add(itemToAdd);
            }
        }

        return resultItems;
    }

    public Double claimMoneyBets() {
        Double money = 0d;
        for (Double mon : moneyBet.values()) {
            money += mon;
        }
        return money;
    }

    public int betValueMaterial(Material material) {
        return switch (material) {
            case DIAMOND -> 3;
            case GOLD_INGOT -> 2;
            case IRON_INGOT -> 1;
            default -> 0;
        };
    }

    public int betValueItem(ItemStack item) {
        return betValueMaterial(item.getType()) * item.getAmount();
    }

    public ItemStack getPlayersItemBet(MinigamePlayer player) {
        if (itemBet.containsKey(player)) {
            return itemBet.get(player);
        }
        return null;
    }

    public Double getPlayersMoneyBet(MinigamePlayer player) {
        if (moneyBet.containsKey(player)) {
            return moneyBet.get(player);
        }
        return null;
    }

    public void removePlayersBet(MinigamePlayer player) {
        itemBet.remove(player);
        moneyBet.remove(player);
    }

    public boolean hasItemBets() {
        return !itemBet.isEmpty();
    }

    public boolean hasMoneyBets() {
        return !moneyBet.isEmpty();
    }

    public double getHighestMoneyBet() {
        return greatestMoneyBet;
    }

    public ItemStack getHighestItemBet() {
        return greatestItemBet;
    }
}
