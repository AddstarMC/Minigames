package au.com.mineauz.minigames.minigame.reward;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandReward extends RewardType {
    private final static String DESCRIPTION_TOKEN = "CommandReward_description";
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
        private final @NotNull List<@NotNull RewardRarity> options = new ArrayList<>();

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

            Collections.addAll(options, RewardRarity.values());
            this.reward = reward;
            updateDescription();
        }

        public void updateName(String newName) {
            ItemMeta meta = getDisplayItem().getItemMeta();
            if (newName.length() > 16) {
                newName = newName.substring(0, 15);
                newName += "...";
            }
            meta.displayName(Component.text(newName));
            getDisplayItem().setItemMeta(meta);
        }

        @Override
        public void updateDescription() {
            List<Component> description;
            int pos = options.indexOf(getRarity());
            int before = pos - 1;
            int after = pos + 1;
            if (before == -1) {
                before = options.size() - 1;
            }
            if (after == options.size()) {
                after = 0;
            }

            description = new ArrayList<>();
            description.add(options.get(before).getDisplayName().color(NamedTextColor.GRAY));
            description.add(getRarity().getDisplayName().color(NamedTextColor.GREEN));
            description.add(options.get(after).getDisplayName().color(NamedTextColor.GRAY));
            description.add(MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_EDIT_SHIFTLEFT).color(NamedTextColor.DARK_PURPLE));
            description.addAll(MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK));

            setDescriptionPartAtEnd(DESCRIPTION_TOKEN, description);
        }

        @Override
        public ItemStack onDoubleClick() {
            return getDisplayItem();
        }

        @Override
        public ItemStack onClick() {
            int ind = options.lastIndexOf(getRarity());
            ind++;
            if (ind == options.size())
                ind = 0;

            setRarity(options.get(ind));
            updateDescription();

            return getDisplayItem();
        }

        @Override
        public ItemStack onRightClick() {
            int ind = options.lastIndexOf(getRarity());
            ind--;
            if (ind == -1) {
                ind = options.size() - 1;
            }

            setRarity(options.get(ind));
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
