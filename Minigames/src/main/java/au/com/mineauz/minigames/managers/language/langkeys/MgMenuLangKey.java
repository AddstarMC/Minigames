package au.com.mineauz.minigames.managers.language.langkeys;

import org.jetbrains.annotations.NotNull;

public enum MgMenuLangKey implements LangKey {
    MENU_BLOCKDATA_CLICKBLOCK("menu.blockData.clickBlock"),
    MENU_BLOCKDATA_DESCRIOPTION_EXTRA("menu.blockData.description.extra"),
    MENU_BLOCKDATA_ERROR_INVALID("menu.blockData.error.invalid"),
    MENU_COMMANDACTION_COMMAND_DESCRIPTION("menu.commandAction.command.description"),
    MENU_COMMANDACTION_COMMAND_NAME("menu.commandAction.command.name"),
    MENU_COMMANDACTION_SILENT_DESCRIPTION("menu.commandAction.silent.description"),
    MENU_COMMANDACTION_SILENT_NAME("menu.commandAction.silent.name"),
    MENU_DEFAULTWINNINGTEAM_NAME("menu.defaultWinningTeam.name"),
    MENU_DELETE_RIGHTCLICK("menu.delete.RightClick"),
    MENU_DELETE_SHIFTRIGHTCLICK("menu.delete.ShiftRightClick"),
    MENU_CHANGE_SHIFTCLICK("menu.change.ShiftClick"),
    MENU_DISPLAYLOADOUT_ALLOWFALLDAMAGE_NAME("menu.displayLoadout.allowFallDamage.name"),
    MENU_DISPLAYLOADOUT_ALLOWHUNGER_NAME("menu.displayLoadout.allowHunger.name"),
    MENU_DISPLAYLOADOUT_ALLOWOFFHAND_NAME("menu.displayLoadout.allowOffhand.name"),
    MENU_DISPLAYLOADOUT_DELETE("menu.displayLoadout.delete"),
    MENU_DISPLAYLOADOUT_DISPLAYINMENU_NAME("menu.displayLoadout.displayInMenu.name"),
    MENU_DISPLAYLOADOUT_EFFECTS_NAME("menu.displayLoadout.effects.name"),
    MENU_DISPLAYLOADOUT_ENTERCHAT("menu.displayLoadout.enterChat"),
    MENU_DISPLAYLOADOUT_LOCKARMOR_NAME("menu.displayLoadout.lockArmor.name"),
    MENU_DISPLAYLOADOUT_LOCKINVENTORY_NAME("menu.displayLoadout.lockInventory.name"),
    MENU_DISPLAYLOADOUT_LOCKTOTEAM_NAME("menu.displayLoadout.lockToTeam.name"),
    MENU_DISPLAYLOADOUT_NOTDELETE("menu.displayLoadout.notDelete"),
    MENU_DISPLAYLOADOUT_SAVE_NAME("menu.displayLoadout.save.name"),
    MENU_DISPLAYLOADOUT_SETTINGS_NAME("menu.displayLoadout.settings.name"),
    MENU_DISPLAYLOADOUT_USEPERMISSIONS_DESCRIPTION("menu.displayLoadout.usePermissions.description"),
    MENU_DISPLAYLOADOUT_USEPERMISSIONS_NAME("menu.displayLoadout.usePermissions.name"),
    MENU_DISPLAYLOADOUT_XPLEVEL_DESCRIPTION("menu.displayLoadout.xpLevel.description"),
    MENU_DISPLAYLOADOUT_XPLEVEL_NAME("menu.displayLoadout.xpLevel.name"),
    MENU_DISPLAYNAME_NAME("menu.displayName.name"),
    MENU_EFFECTS_SAVE_NAME("menu.effects.save.name"),
    MENU_ELEMENTNOTSET("menu.elementNotSet"),
    MENU_ENUM_ERROR("menu.enum.error"),
    MENU_FLAGADD_ENTERCHAT("menu.flagAdd.enterChat"),
    MENU_FLAGADD_NAME("menu.flagAdd.name"),
    MENU_FLAG_REMOVED("menu.flag.removed"),
    MENU_HIERARCHY_ENTERCHAT("menu.hierarchy.enterChat"),
    MENU_INVERT_NAME("menu.invert.name"),
    MENU_LIST_ENTERCHAT("menu.list.enterChat"),
    MENU_LIST_ERROR_INVALID("menu.list.error.invalid"),
    MENU_LIST_ERROR_TOOLONG("menu.list.error.long"),
    MENU_LIST_OPTION("menu.list.options"),
    MENU_LOADOUT_ADD_ENTERCHAT("menu.loadout.add.enterChat"),
    MENU_LOADOUT_ADD_NAME("menu.loadout.add.name"),
    MENU_LOADOUT_ERROR_ALREADYEXISTS("menu.loadout.error.alreadyExists"),
    MENU_LOADOUT_SAVE("menu.displayLoadout.save.success"),
    MENU_LOBBY_WAIT_PLAYER_INTERACT_NAME("menu.lobby.wait.player.interact.name"),
    MENU_LOBBY_WAIT_PLAYER_MOVE_NAME("menu.lobby.wait.player.move.name"),
    MENU_LOBBY_WAIT_PLAYER_TELEPORT_DESCRIPTION("menu.lobby.wait.player.teleport.description"),
    MENU_LOBBY_WAIT_PLAYER_TELEPORT_NAME("menu.lobby.wait.player.teleport.name"),
    MENU_LOBBY_WAIT_PLAYER_TIME_DESCRIPTION("menu.lobby.wait.player.time.description"),
    MENU_LOBBY_WAIT_PLAYER_TIME_NAME("menu.lobby.wait.player.time.name"),
    MENU_LOBBY_WAIT_START_INTERACT_NAME("menu.lobby.wait.start.interact.name"),
    MENU_LOBBY_WAIT_START_MOVE_NAME("menu.lobby.wait.start.move.name"),
    MENU_LOBBY_WAIT_START_TELEPORT_DESCRIPTION("menu.lobby.wait.teleport.description"),
    MENU_LOBBY_WAIT_START_TELEPORT_NAME("menu.lobby.wait.start.teleport.name"),
    MENU_MATERIAL_DESCRIOPTION("menu.material.description"),
    MENU_MINIGAME_ALLOWLATEJOIN_NAME("menu.minigame.allowLateJoin.name"),
    MENU_MINIGAME_ALLOWSPECTATORFLY_NAME("menu.minigame.allowSpectatorFly.name"),
    MENU_MINIGAME_DEGEN_DELAY_NAME("menu.minigame.degen.delay.name"),
    MENU_MINIGAME_DEGEN_RANDOMCHANCE_DESCRIPTION("menu.minigame.degen.randomChance.description"),
    MENU_MINIGAME_DEGEN_RANDOMCHANCE_NAME("menu.minigame.degen.randomChance.name"),
    MENU_MINIGAME_DEGEN_TYPE_DESCRIPTION("menu.minigame.degen.type.description"),
    MENU_MINIGAME_DEGEN_TYPE_NAME("menu.minigame.degen.type.name"),
    MENU_MINIGAME_ENABLED_NAME("menu.minigame.enabled.name"),
    MENU_MINIGAME_LOADOUTS_NAME("menu.minigame.loadouts.name"),
    MENU_MINIGAME_LOBBY_SETTINGS_NAME("menu.minigame.lobby.settings.name"),
    MENU_MINIGAME_MECHANIC_NAME("menu.minigame.mechanic.name"),
    MENU_MINIGAME_MECHANIC_SETTINGS_NAME("menu.minigame.mechanic.settings.name"),
    MENU_MINIGAME_MULTIPLAYERONLY_DESCRIPTION("menu.minigame.multiplayerOnly.description"),
    MENU_MINIGAME_OBJECTIVEDESCRIPTION_NAME("menu.minigame.objectiveDescription.name"),
    MENU_MINIGAME_PLAYERS_MAX_NAME("menu.minigame.players.max.name"),
    MENU_MINIGAME_PLAYERS_MIN_NAME("menu.minigame.players.min.name"),
    MENU_MINIGAME_PLAYERS_SINGLEPLAYER_CAPPED_NAME("menu.minigame.players.singleplayer.capped.name"),
    MENU_MINIGAME_RANDOMCHESTS_DESCRIPTION("menu.minigame.randomChests.description"),
    MENU_MINIGAME_RANDOMCHESTS_MAX_DESCRIPTION("menu.minigame.randomChests.max.description"),
    MENU_MINIGAME_RANDOMCHESTS_MAX_NAME("menu.minigame.randomChests.max.name"),
    MENU_MINIGAME_RANDOMCHESTS_MIN_DESCRIPTION("menu.minigame.randomChests.min.description"),
    MENU_MINIGAME_RANDOMCHESTS_MIN_NAME("menu.minigame.randomChests.min.name"),
    MENU_MINIGAME_RANDOMCHESTS_NAME("menu.minigame.randomChests.name"),
    MENU_MINIGAME_REGENDELAY_DESCRIPTION("menu.minigame.regenDelay.description"),
    MENU_MINIGAME_REGENDELAY_NAME("menu.minigame.regenDelay.name"),
    MENU_MINIGAME_SAVE_NAME("menu.minigame.save.name"),
    MENU_MINIGAME_SCOREBOARD_DISPLAY_NAME("menu.minigame.scoreboard.display.name"),
    MENU_MINIGAME_SCORE_MAX_NAME("menu.minigame.score.max.name"),
    MENU_MINIGAME_SCORE_MIN_NAME("menu.minigame.score.min.name"),
    MENU_MINIGAME_SINGLEPLAYERONLY_DESCRIPTION("menu.minigame.singleplayeronly.description"),
    MENU_MINIGAME_STARTPOINT_RANDOMIZE_DESCRIPTION("menu.minigame.startPoint.randomize.description"),
    MENU_MINIGAME_STARTPOINT_RANDOMIZE_NAME("menu.minigame.startPoint.randomize.name"),
    MENU_MINIGAME_STATISTIC_NAME("menu.minigame.statistic.name"),
    MENU_MINIGAME_TIME_GAMELENGTH_NAME("menu.minigame.time.gameLength.name"),
    MENU_MINIGAME_TIME_SHOWCOMPLETION_NAME("menu.minigame.time.showCompletion.name"),
    MENU_MINIGAME_TIME_STARTWAIT_NAME("menu.time.startWait.name"),
    MENU_MINIGAME_TYPEDESCRIPTION_NAME("menu.minigame.typeDescription.name"),
    MENU_MINIGAME_TYPE_NAME("menu.minigame.type.name"),
    MENU_MINIGAME_USEPERNS_NAME("menu.minigame.usePermissions.name"),
    MENU_MINIGAME_WHITELIST_BLOCK_DESCRIPTION_MAIN("menu.minigame.whitelist.block.description.main"),
    MENU_MINIGAME_WHITELIST_BLOCK_DESCRIPTION_SECOND("menu.minigame.whitelist.block.description.second"),
    MENU_MINIGAME_WHITELIST_BLOCK_NAME("menu.minigame.whitelist.block.name"),
    MENU_MONEYREWARD_ITEM_NAME("menu.moneyReward.item.name"),
    MENU_MONEYREWARD_MENU_NAME("menu.moneyReward.menu.name"),
    MENU_NUMBER_ENTERCHAT("menu.number.enterChat"),
    MENU_NUMBER_INFINITE("menu.number.infinite"),
    MENU_PAGE_BACK("menu.page.back"),
    MENU_PAGE_NEXT("menu.page.next"),
    MENU_PAGE_PREVIOUS("menu.page.previous"),
    MENU_PLAYERSETTINGS_BLOCK_BREAK_NAME("menu.playerSettings.block.break.name"),
    MENU_PLAYERSETTINGS_BLOCK_DROPS_NAME("menu.playerSettings.block.drops.name"),
    MENU_PLAYERSETTINGS_BLOCK_PLACE_NAME("menu.playerSettings.block.place.name"),
    MENU_PLAYERSETTINGS_BROADCASTS_CTF_DESCRIPTION("menu.playerSettings.broadcasts.ctf.description"),
    MENU_PLAYERSETTINGS_BROADCASTS_CTF_NAME("menu.playerSettings.broadcasts.ctf.name"),
    MENU_PLAYERSETTINGS_BROADCASTS_JOINEXIT_DESCRIPTION("menu.playerSettings.broadcasts.joinExit.description"),
    MENU_PLAYERSETTINGS_BROADCASTS_JOINEXIT_NAME("menu.playerSettings.broadcasts.joinExit.name"),
    MENU_PLAYERSETTINGS_CHECKPOINT_MULTIPLAYER_NAME("menu.playerSettings.checkpoints.multiplayer.name"),
    MENU_PLAYERSETTINGS_CHECKPOINT_SAVE_NAME("menu.playerSettings.checkpoint.save.name"),
    MENU_PLAYERSETTINGS_DISPLAYNAMES_DESCRIPTION("menu.playerSettings.displaynames.description"),
    MENU_PLAYERSETTINGS_DISPLAYNAMES_NAME("menu.playerSettings.displaynames.name"),
    MENU_PLAYERSETTINGS_DRAGONEGGTELEPORT_NAME("menu.playerSettings.dragonEggTeleport.name"),
    MENU_PLAYERSETTINGS_DROP_DEATH_NAME("menu.playerSettings.drop.death.name"),
    MENU_PLAYERSETTINGS_DROP_ITEM_NAME("menu.playerSettings.drops.item.name"),
    MENU_PLAYERSETTINGS_ENDERPERLS_NAME("menu.playerSettings.enderPerls.name"),
    MENU_PLAYERSETTINGS_FLIGHT_ALLOW_DESCRIPTION("menu.playerSettings.flight.allow.description"),
    MENU_PLAYERSETTINGS_FLIGHT_ALLOW_NAME("menu.playerSettings.flight.allow.name"),
    MENU_PLAYERSETTINGS_FLIGHT_ENABLE_DESCRIPTION("menu.playerSettings.flight.enable.description"),
    MENU_PLAYERSETTINGS_FLIGHT_ENABLE_NAME("menu.playerSettings.flight.enable.name"),
    MENU_PLAYERSETTINGS_FRIENDLYFIRE_LINGERING_NAME("menu.playerSettings.friednlyFire.lingering.name"),
    MENU_PLAYERSETTINGS_FRIENDLYFIRE_SPLASH_NAME("menu.playerSettings.friendlyFire.splash.name"),
    MENU_PLAYERSETTINGS_GAMEMODE_NAME("menu.playersettings.gamemode.name"),
    MENU_PLAYERSETTINGS_ITEMPICKUP_NAME("menu.playerSettings.itemPickup.name"),
    MENU_PLAYERSETTINGS_KEEPINVENTORY_NAME("menu.playerSettings.keepInventory.name"),
    MENU_PLAYERSETTINGS_LIVES_NAME("menu.playerSettings.lives.name"),
    MENU_PLAYERSETTINGS_NAME("menu.minigame.playerSettings.name"),
    MENU_PLAYERSETTINGS_PAINTBALL_DAMAGE_NAME("menu.playerSettings.paintball.damage.name"),
    MENU_PLAYERSETTINGS_PAINTBALL_MODE_NAME("menu.playerSettings.paintball.mode.name"),
    MENU_PLAYERSETTINGS_SINGLEPLAYERFLAG_NAME("menu.playerSettings.singleplayerFlag.name"),
    MENU_PLAYERSETTINGS_UNLIMITEDAMMO_NAME("menu.playerSettings.unlimitedAmmo.name"),
    MENU_PLAYSOUND_MENU_NAME("menu.playSound.menu.name"),
    MENU_PLAYSOUND_PITCH_NAME("menu.playSound.pitch.name"),
    MENU_PLAYSOUND_PRIVATEPLAYBACK_NAME("menu.playSound.privatePlayback.name"),
    MENU_PLAYSOUND_SOUND_NAME("menu.playSound.sound.name"),
    MENU_PLAYSOUND_VOLUME_NAME("menu.playSound.volume.name"),
    MENU_POTIONADD_ENTERCHAT("menu.potionAdd.enterChat"),
    MENU_POTIONADD_ERROR_SYNTAX("menu.potionAdd.error.syntax"),
    MENU_POTIONADD_NAME("menu.potionAdd.name"),
    MENU_POTION_DURATION("menu.potion.duration"),
    MENU_POTION_LEVEL("menu.potion.level"),
    MENU_REWARDPAIR_EDIT("menu.rewardPair.edit"),
    MENU_REWARD_ENTERCHAT("menu.reward.enterChat"),
    MENU_REWARD_ERROR_GROUPEXISTS("menu.reward.error.groupExists"),
    MENU_REWARD_GROUP_NAME("menu.reward.group.name"),
    MENU_REWARD_NAME("menu.reward.name"),
    MENU_REWARD_NOTREMOVED("menu.reward.notRemoved"),
    MENU_REWARD_SCHEME_NAME("menu.reward.scheme.name"),
    MENU_REWARD_SELECTTYPE_NAME("menu.reward.selectType.name"),
    MENU_REWARD_SETTINGS_NAME("menu.reward.settings.name"),
    MENU_REWARD_TYPE_NAME("menu.reward.type.name"),
    MENU_STAT_EDIT_NAME("menu.stat.edit.name"),
    MENU_STAT_SETTINGS_NAME("menu.stat.settings.name"),
    MENU_STAT_STORAGEFORMAT("menu.stat.storageFormat"),
    MENU_STRING_ALLOWNULL("menu.string.allowNull"),
    MENU_STRING_ENTERCHAT("menu.string.enterChat"),
    MENU_TEAMADD_NAME("menu.teamAdd.name"),
    MENU_TEAM_ASSIGNMSG_DESCRIPTION("menu.team.assignMsg.description"),
    MENU_TEAM_ASSIGNMSG_NAME("menu.team.assignMsg.name"),
    MENU_TEAM_AUTOBALANCE("menu.team.autobalance"),
    MENU_TEAM_AUTOBALANCEMSG_DESCRIPTION("menu.team.autobalanceMsg.description"),
    MENU_TEAM_AUTOBALANCEMSG_NAME("menu.team.autobalanceMsg.name"),
    MENU_TEAM_GAMEAUTOBALANCEMSG_DESCRIPTION("menu.team.gameAutobalanceMsg.description"),
    MENU_TEAM_GAMEAUTOBALANCEMSG_NAME("menu.team.gameAutobalanceMsg.name"),
    MENU_TEAM_MAXPLAYERS("menu.team.maxPlayers"),
    MENU_TEAM_NAMEVISIBILITY_ALWAYSVISIBLE("menu.team.nameVisibility.alwaysVisible"),
    MENU_TEAM_NAMEVISIBILITY_HIDEOTHERTEAM("menu.team.nameVisibility.hideOtherTeam"),
    MENU_TEAM_NAMEVISIBILITY_HIDEOWNTEAM("menu.team.nameVisibility.hideOwnTeam"),
    MENU_TEAM_NAMEVISIBILITY_NAME("menu.team.nameVisibility.name"),
    MENU_TEAM_NAMEVISIBILITY_NEVERVISIBLE("menu.team.nameVisibility.neverVisible"),
    MENU_TOOL_DESELECT_DESCRIPTION("menu.tool.deselect.description"),
    MENU_TOOL_DESELECT_NAME("menu.tool.deselect.name"),
    MENU_TOOL_SELECT_DESCRIPTION("menu.tool.select.description"),
    MENU_TOOL_SELECT_NAME("menu.tool.select.name"),
    MENU_TOOL_SETMODE_NAME("menu.tool.setMode.name"),
    MENU_TOOL_SETTEAM_NAME("menu.tool.setTeam.name"),
    MENU_WHITELIST_ADDMATERIAL_NAME("menu.whitelist.addMaterial.name"),
    MENU_WHITELIST_BLOCK_NAME("menu.whitelist.block.name"),
    MENU_WHITELIST_ENTERCHAT("menu.whitelist.enterChat"),
    MENU_WHITELIST_ERROR_CONTAINS("menu.whitelist.error.contains"),
    MENU_WHITELIST_INTERACT("menu.whitelist.interact"),
    MENU_WHITELIST_MODE("menu.whitelist.mode"),
    MENU_MINIGAME_TIME_DISPLAYTYPE_NAME("menu.minigame.time.displayType.name");

    private final @NotNull String path;

    MgMenuLangKey(@NotNull String path) {
        this.path = path;
    }

    public @NotNull String getPath() {
        return path;
    }
}
