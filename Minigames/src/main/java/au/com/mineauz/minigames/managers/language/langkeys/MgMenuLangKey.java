package au.com.mineauz.minigames.managers.language.langkeys;

import org.jetbrains.annotations.NotNull;

public enum MgMenuLangKey implements LangKey {
    MENU_BLOCKDATA_CLICKBLOCK("menu.blockData.clickBlock"),
    MENU_BLOCKDATA_ERROR_INVALID("menu.blockData.error.invalid"),
    MENU_DECIMAL_ENTERCHAT("menu.decimal.enterChat"),
    MENU_DEFAULTWINNINGTEAM_NAME("menu.defaultWinningTeam.name"),
    MENU_DELETE_RIGHTCLICK("menu.delete.RightClick"),
    MENU_DELETE_SHIFTRIGHTCLICK("menu.delete.ShiftRightClick"),
    MENU_DISPLAYLOADOUT_DELETE("menu.displayLoadout.delete"),
    MENU_DISPLAYLOADOUT_DISPLAYINMENU_NAME("menu.displayLoadout.displayInMenu.name"),
    MENU_DISPLAYLOADOUT_EFFECTS_NAME("menu.displayLoadout.effects.name"),
    MENU_DISPLAYLOADOUT_ENTERCHAT("menu.displayLoadout.enterChat"),
    MENU_DISPLAYLOADOUT_LOCKTOTEAM_NAME("menu.displayLoadout.lockToTeam.name"),
    MENU_DISPLAYLOADOUT_NOTDELETE("menu.displayLoadout.notDelete"),
    MENU_DISPLAYLOADOUT_SAVE_NAME("menu.displayLoadout.save.name"),
    MENU_DISPLAYLOADOUT_SETTINGS_NAME("menu.displayLoadout.settings.name"),
    MENU_FLAGADD_ENTERCHAT("menu.flagAdd.enterChat"),
    MENU_FLAGADD_NAME("menu.flagAdd.name"),
    MENU_HIERARCHY_ENTERCHAT("menu.hierarchy.enterChat"),
    MENU_LOADOUT_SAVE("menu.displayLoadout.save.success"),
    MENU_MONEYREWARD_ITEM_NAME("menu.moneyReward.item.name"),
    MENU_MONEYREWARD_MENU_NAME("menu.moneyReward.menu.name"),
    MENU_PAGE_BACK("menu.page.back"),
    MENU_PAGE_NEXT("menu.page.next"),
    MENU_PAGE_PREVIOUS("menu.page.previous"),
    MENU_PLAYSOUND_MENU_NAME("menu.playSound.menu.name"),
    MENU_PLAYSOUND_PITCH_NAME("menu.playSound.pitch.name"),
    MENU_PLAYSOUND_PRIVATEPLAYBACK_NAME("menu.playSound.privatePlayback.name"),
    MENU_PLAYSOUND_SOUND_NAME("menu.playSound.sound.name"),
    MENU_PLAYSOUND_VOLUME_NAME("menu.playSound.volume.name"),
    MENU_POTIONADD_ENTERCHAT("menu.potionAdd.enterChat"),
    MENU_POTIONADD_ERROR_SYNTAX("menu.potionAdd.error.syntax"),
    MENU_POTIONADD_NAME("menu.potionAdd.name"),
    MENU_TEAMADD_NAME("menu.teamAdd.name"),
    MENU_TEAM_AUTOBALANCE("menu.team.autobalance"),
    MENU_TEAM_DISPLAYNAME("menu.team.displayName"),
    MENU_TEAM_MAXPLAYERS("menu.team.maxPlayers"),
    MENU_TEAM_NAMEVISIBILITY_ALWAYSVISIBLE("menu.team.nameVisibility.alwaysVisible"),
    MENU_TEAM_NAMEVISIBILITY_HIDEOTHERTEAM("menu.team.nameVisibility.hideOtherTeam"),
    MENU_TEAM_NAMEVISIBILITY_HIDEOWNTEAM("menu.team.nameVisibility.hideOwnTeam"),
    MENU_TEAM_NAMEVISIBILITY_NAME("menu.team.nameVisibility.name"),
    MENU_TEAM_NAMEVISIBILITY_NEVERVISIBLE("menu.team.nameVisibility.neverVisible"),
    MENU_WHITELIST_ENTERCHAT("menu.whitelist.enterChat"),
    MENU_WHITELIST_ERROR_CONTAINS("menu.whitelist.error.contains");

    private final @NotNull String path;

    MgMenuLangKey(@NotNull String path) {
        this.path = path;
    }

    public @NotNull String getPath() {
        return path;
    }
}
