package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.*;
import au.com.mineauz.minigames.minigame.reward.scheme.StandardRewardScheme;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetRewardCommand extends ASetCommand { //todo allow commands
    private final static Pattern MONEY_PATTERN = Pattern.compile("\\$-?(\\d+(\\.\\d+)?)");

    private static void setItemReward(@NotNull Minigame minigame, @NotNull Rewards rewards, @NotNull CommandSender sender,
                                      @NotNull ItemStack item, @NotNull RewardRarity rarity, boolean isPrimary) {
        if (item.getType().isAir()) {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_SET_REWARD_ITEM_ERROR_AIR,
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
            return;
        }

        ItemReward ir = ItemReward.getMinigameReward(rewards);
        ir.setRewardItem(item);
        ir.setRarity(rarity);
        rewards.addReward(ir);

        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO,
                isPrimary ? MgCommandLangKey.COMMAND_SET_REWARD_ITEM_SUCCESS : MgCommandLangKey.COMMAND_SET_REWARD2_ITEM_SUCCESS,
                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(item.getAmount())),
                Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), item.displayName()),
                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                Placeholder.unparsed(MinigamePlaceHolderKey.RARITY.getKey(), rarity.toString().toLowerCase().replace("_", " ")));
    }

    protected static boolean processRewardCommands(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                   @NotNull String @Nullable [] args, boolean isPrimary) {
        if (args != null) {
            RewardsModule module = RewardsModule.getModule(minigame);

            if (module.getScheme() instanceof StandardRewardScheme standardRewardScheme) {
                Rewards rewards;
                if (isPrimary) {
                    rewards = standardRewardScheme.getPrimaryReward();
                } else {
                    rewards = standardRewardScheme.getSecondaryReward();
                }

                if (args.length >= 1) {
                    if (args[0].startsWith("$")) {
                        Economy economy = PLUGIN.getEconomy();

                        if (economy != null) {
                            Matcher matcher = MONEY_PATTERN.matcher(args[0]);

                            if (matcher.matches()) {
                                double money = Double.parseDouble(matcher.group(1));

                                RewardRarity rarity;
                                if (args.length >= 2) {
                                    rarity = RewardRarity.matchRarity(args[1]);

                                    if (rarity == null) {
                                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_SET_REWARD_ITEM_ERROR_NOTRARITY,
                                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                                        return false;
                                    }
                                } else {
                                    rarity = RewardRarity.NORMAL;
                                }

                                MoneyReward moneyReward = MoneyReward.getMinigameReward(rewards);
                                moneyReward.setRewardMoney(money);
                                moneyReward.setRarity(rarity);
                                rewards.addReward(moneyReward);

                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO,
                                        isPrimary ? MgCommandLangKey.COMMAND_SET_REWARD_MONEY_SUCCESS : MgCommandLangKey.COMMAND_SET_REWARD2_MONEY_SUCCESS,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MONEY.getKey(), economy.format(money)),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.RARITY.getKey(), rarity.toString().toLowerCase().replace("_", " ")));
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                            }
                        } else if (args[0].equals("SLOT")) {
                            if (sender instanceof Player player) {
                                ItemStack item = player.getInventory().getItemInMainHand();

                                RewardRarity rarity;
                                if (args.length == 2) {
                                    rarity = RewardRarity.matchRarity(args[1]);

                                    if (rarity == null) {
                                        return false;
                                    }
                                } else {
                                    rarity = RewardRarity.NORMAL;
                                }

                                setItemReward(minigame, rewards, sender, item, rarity, isPrimary);
                                return true;

                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER);
                            }
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.REWARD_ERROR_NOVAULT,
                                    Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(),
                                            Component.text("spigot.net", Style.style(TextDecoration.UNDERLINED)).
                                                    clickEvent(ClickEvent.openUrl("https://www.spigotmc.org/resources/vault.34315/"))));
                        }

                        return true;
                    } else {
                        Material mat = Material.matchMaterial(args[0]);

                        if (mat != null) {
                            int quantity = 1;
                            if (args.length >= 2) {
                                if (args[1].matches("[0-9]+")) {
                                    quantity = Integer.parseInt(args[1]);
                                } else {
                                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));

                                    return false;
                                }
                            }

                            ItemStack item = new ItemStack(mat, quantity);

                            RewardRarity rarity;
                            if (args.length >= 3) {
                                rarity = RewardRarity.matchRarity(args[2]);

                                if (rarity == null) {
                                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_SET_REWARD_ITEM_ERROR_NOTRARITY,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                                    return false;
                                }
                            } else {
                                rarity = RewardRarity.NORMAL;
                            }

                            setItemReward(minigame, rewards, sender, item, rarity, isPrimary);
                            return true;

                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTMATERIAL,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                        }
                    }
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_SET_REWARD_ERROR_SCHEME);
                return true;
            }
        }
        return false;

    }

    @Override
    public @NotNull String getName() {
        return "reward";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_REWARD_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_REWARD_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.reward";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        return processRewardCommands(sender, minigame, args, true);
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull @Nullable [] args) {
        if (args.length == 3 || (args.length == 2 && args[0].startsWith("$"))) {
            List<String> ls = Arrays.stream(RewardRarity.values()).map(RewardRarity::toString).toList();
            return MinigameUtils.tabCompleteMatch(ls, args[args.length - 1]);
        }
        return null;
    }

}
