package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.BooleanUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetBlockWhitelistCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "blockwhitelist";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"bwl", "blockwl"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_WHITELIST_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_WHITELIST_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.blockwhitelist";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("add") && args.length >= 2) {
                Material mat = Material.matchMaterial(args[1].toUpperCase());

                if (mat != null && mat.isBlock()) {
                    minigame.getRecorderData().addWBBlock(mat);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_WHITELIST_ADDED,
                            Placeholder.component(MinigamePlaceHolderKey.MATERIAL.getKey(), Component.translatable(mat.translationKey())),
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTMATERIAL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                }
            } else if (args[0].equalsIgnoreCase("remove") && args.length >= 2) {
                Material mat = Material.matchMaterial(args[1]);

                if (mat != null) {
                    minigame.getRecorderData().removeWBBlock(mat);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_WHITELIST_REMOVE,
                            Placeholder.component(MinigamePlaceHolderKey.MATERIAL.getKey(), Component.translatable(mat.translationKey())),
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTMATERIAL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                }
            } else if (args[0].equalsIgnoreCase("clear")) {
                minigame.getRecorderData().getWBBlocks().clear();

                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_WHITELIST_CLEAR,
                        Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), MinigameMessageManager.getMgMessage(
                                minigame.getRecorderData().getWhitelistMode() ? MinigameLangKey.CONFIG_WHITELIST : MinigameLangKey.CONFIG_BLACKLIST)),
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
            } else if (args[0].equalsIgnoreCase("list")) { //todo set list doesn't feel right
                String whiteListedBlocks = minigame.getRecorderData().getWBBlocks().stream().map(Material::toString).collect(Collectors.joining("<gray>, </gray>"));

                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_WHITELIST_LIST,
                        Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), MinigameMessageManager.getMgMessage(
                                minigame.getRecorderData().getWhitelistMode() ? MinigameLangKey.CONFIG_WHITELIST : MinigameLangKey.CONFIG_BLACKLIST)),
                        Placeholder.parsed(MinigamePlaceHolderKey.TEXT.getKey(), whiteListedBlocks));
            } else {
                Boolean bool = BooleanUtils.toBooleanObject(args[0]);

                if (bool != null) {
                    minigame.getRecorderData().setWhitelistMode(bool);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_WHITELIST_MODE,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                            Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), MinigameMessageManager.getMgMessage(
                                    bool ? MinigameLangKey.CONFIG_WHITELIST : MinigameLangKey.CONFIG_BLACKLIST)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTBOOL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull @Nullable [] args) {
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
