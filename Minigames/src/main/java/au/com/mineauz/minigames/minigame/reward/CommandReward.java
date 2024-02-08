package au.com.mineauz.minigames.minigame.reward;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandReward extends RewardType {
    private String command = "say Hello World!";

    public CommandReward(Rewards rewards) {
        super(rewards);
    }

    public static CommandReward getMinigameReward(@NotNull Rewards rewards) {
        return (CommandReward) RewardTypes.getRewardType(RewardTypes.MgRewardType.COMMAND.getName(), rewards);
    }

    @Override
    public String getName() {
        return "COMMAND";
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public void giveReward(@NotNull MinigamePlayer mgPlayer) {
        String finalCommand = command.replace("%player%", mgPlayer.getName());
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
    }

    @Override
    public MenuItem getMenuItem() {
        return new CommandRewardItem(this);
    }

    @Override
    public void saveReward(String path, ConfigurationSection config) {
        config.set(path, command);
    }

    @Override
    public void loadReward(String path, ConfigurationSection config) {
        command = config.getString(path);
    }

    private class CommandRewardItem extends MenuItemString {
        private final CommandReward reward;
        private final @NotNull List<@NotNull String> options = new ArrayList<>();

        public CommandRewardItem(CommandReward reward) {
            super(Material.COMMAND_BLOCK, Component.text("/" + command), new Callback<>() {

                @Override
                public String getValue() {
                    return command;
                }

                @Override
                public void setValue(String value) {
                    if (value.startsWith("./"))
                        value = value.replace("./", "/");
                    command = value;
                }
            });

            for (RewardRarity rarity : RewardRarity.values()) {
                options.add(rarity.toString());
            }
            this.reward = reward;
            updateDescription();
        }

        public void updateName(String newName) {
            ItemMeta meta = getDisplayItem().getItemMeta();
            if (newName.length() > 16) {
                newName = newName.substring(0, 15);
                newName += "...";
            }
            meta.setDisplayName(ChatColor.RESET + newName);
            getDisplayItem().setItemMeta(meta);
            getContainer().removeItem(getSlot());
            getContainer().addItem(this, getSlot());
        }

        @Override
        public void updateDescription() {
            List<Component> description;
            int pos = options.indexOf(getRarity().toString());
            int before = pos - 1;
            int after = pos + 1;
            if (before == -1)
                before = options.size() - 1;
            if (after == options.size())
                after = 0;

            if (getDescriptionStr() != null) {
                description = getDescriptionStr();
                if (getDescriptionStr().size() >= 3) {
                    String desc = ChatColor.stripColor(getDescriptionStr().get(1));

                    if (options.contains(desc)) {
                        description.set(0, ChatColor.GRAY + options.get(before));
                        description.set(1, ChatColor.GREEN + getRarity().toString());
                        description.set(2, ChatColor.GRAY + options.get(after));
                    } else {
                        description.add(0, ChatColor.GRAY + options.get(before));
                        description.add(1, ChatColor.GREEN + getRarity().toString());
                        description.add(2, ChatColor.GRAY + options.get(after));
                        description.add(3, ChatColor.DARK_PURPLE + "Shift + Left Click to change");
                        description.add(4, ChatColor.DARK_PURPLE + "Shift + Right Click to remove");
                    }
                } else {
                    description.add(0, ChatColor.GRAY + options.get(before));
                    description.add(1, ChatColor.GREEN + getRarity().toString());
                    description.add(2, ChatColor.GRAY + options.get(after));
                    description.add(3, ChatColor.DARK_PURPLE + "Shift + Left Click to change");
                    description.add(4, ChatColor.DARK_PURPLE + "Shift + Right Click to remove");
                }
            } else {
                description = new ArrayList<>();
                description.add(ChatColor.GRAY + options.get(before));
                description.add(ChatColor.GREEN + getRarity().toString());
                description.add(ChatColor.GRAY + options.get(after));
                description.add(3, ChatColor.DARK_PURPLE + "Shift + Left Click to change");
                description.add(4, ChatColor.DARK_PURPLE + "Shift + Right Click to remove");
            }

            setDescriptionStr(description);
        }

        @Override
        public ItemStack onDoubleClick() {
            return getDisplayItem();
        }

        @Override
        public ItemStack onClick() {
            int ind = options.lastIndexOf(getRarity().toString());
            ind++;
            if (ind == options.size())
                ind = 0;

            setRarity(RewardRarity.valueOf(options.get(ind)));
            updateDescription();

            return getDisplayItem();
        }

        @Override
        public ItemStack onRightClick() {
            int ind = options.lastIndexOf(getRarity().toString());
            ind--;
            if (ind == -1) {
                ind = options.size() - 1;
            }

            setRarity(RewardRarity.valueOf(options.get(ind)));
            updateDescription();

            return getDisplayItem();
        }

        @Override
        public ItemStack onShiftRightClick() {
            getRewards().removeReward(reward);
            getContainer().removeItem(getSlot());
            return null;
        }

        @Override
        public ItemStack onShiftClick() {
            super.onDoubleClick();
            return null;
        }

        @Override
        public void checkValidEntry(String entry) {
            super.checkValidEntry(entry);
            updateName(entry);
        }
    }
}
