package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetBlockWhitelistCommand implements ICommand {

    @Override
    public String getName() {
        return "blockwhitelist";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"bwl", "blockwl"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Adds, removes and changes whitelist mode on or off (off by default). " +
                "When off, it is in blacklist mode, meaning the blocks in the list are the only blocks that list can't be placed or destroyed";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"add", "remove", "list", "clear"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame set <Minigame> blockwhitelist <true/false>",
                "/minigame set <Minigame> blockwhitelist add <Block type>",
                "/minigame set <Minigame> blockwhitelist remove <Block type>",
                "/minigame set <Minigame> blockwhitelist list",
                "/minigame set <Minigame> blockwhitelist clear"
        };
    }

    @Override
    public String getPermission() {
        return "minigame.set.blockwhitelist";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("add") && args.length >= 2) {
                Material mat = Material.matchMaterial(args[1].toUpperCase());

                if (mat != null && mat.isBlock()) {
                    minigame.getRecorderData().addWBBlock(mat);

                    Component addedMat;
                    if (mat.getBlockTranslationKey() != null) {
                        addedMat = Component.translatable(mat.getBlockTranslationKey());
                    } else if (mat.getItemTranslationKey() != null) {
                        addedMat = Component.translatable(mat.getItemTranslationKey());
                    } else {
                        addedMat = Component.text(WordUtils.capitalize(mat.toString().replace("_", " ").toLowerCase()));
                    }

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_WHITELIST_ADDED,
                            Placeholder.component(MinigamePlaceHolderKey.MATERIAL.getKey(), addedMat),
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOMATERIAL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                }
            } else if (args[0].equalsIgnoreCase("remove") && args.length >= 2) {
                Material mat = Material.matchMaterial(args[1]);

                if (mat != null) {
                    minigame.getRecorderData().removeWBBlock(mat);

                    Component removedMat;
                    if (mat.getBlockTranslationKey() != null) {
                        removedMat = Component.translatable(mat.getBlockTranslationKey());
                    } else if (mat.getItemTranslationKey() != null) {
                        removedMat = Component.translatable(mat.getItemTranslationKey());
                    } else {
                        removedMat = Component.text(WordUtils.capitalize(mat.toString().replace("_", " ").toLowerCase()));
                    }

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_WHITELIST_REMOVE,
                            Placeholder.component(MinigamePlaceHolderKey.MATERIAL.getKey(), removedMat),
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOMATERIAL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                }
            } else if (args[0].equalsIgnoreCase("clear")) {
                minigame.getRecorderData().getWBBlocks().clear();

                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_WHITELIST_CLEAR,
                        Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), MinigameMessageManager.getMgMessage(
                                minigame.getRecorderData().getWhitelistMode() ? MinigameLangKey.WHITELIST : MinigameLangKey.BLACKLIST)),
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
            } else if (args[0].equalsIgnoreCase("list")) { //todo set list doesn't feel right
                String whiteListedBlocks = minigame.getRecorderData().getWBBlocks().stream().map(Material::toString).collect(Collectors.joining("<gray>, </gray>"));

                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_WHITELIST_LIST,
                        Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), MinigameMessageManager.getMgMessage(
                                minigame.getRecorderData().getWhitelistMode() ? MinigameLangKey.WHITELIST : MinigameLangKey.BLACKLIST)),
                        Placeholder.parsed(MinigamePlaceHolderKey.TEXT.getKey(), whiteListedBlocks));
            } else {
                Boolean bool = BooleanUtils.toBooleanObject(args[0]);

                if (bool != null) {
                    minigame.getRecorderData().setWhitelistMode(bool);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_WHITELIST_MODE,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                            Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), MinigameMessageManager.getMgMessage(
                                    bool ? MinigameLangKey.WHITELIST : MinigameLangKey.BLACKLIST)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOBOOL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(List.of("true", "false", "add", "remove", "list", "clear"), args[0]);
        else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            List<String> ls = new ArrayList<>();
            for (Material m : minigame.getRecorderData().getWBBlocks()) {
                ls.add(m.toString());
            }
            return MinigameUtils.tabCompleteMatch(ls, args[1]);
        }
        return null;
    }

}
