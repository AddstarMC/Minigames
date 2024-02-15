package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.TimeFlag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameOverModule extends MinigameModule {
    private final TimeFlag timer = new TimeFlag(0L, "gameOver.timer");
    private final BooleanFlag invincible = new BooleanFlag(false, "gameOver.invincible");
    private final BooleanFlag humiliation = new BooleanFlag(false, "gameOver.humiliation");
    private final BooleanFlag interact = new BooleanFlag(false, "gameOver.interact");

    private final List<MinigamePlayer> winners = new ArrayList<>();
    private final List<MinigamePlayer> losers = new ArrayList<>();
    private int task = -1;

    public GameOverModule(@NotNull Minigame mgm, @NotNull String name) {
        super(mgm, name);
    }

    public static @Nullable GameOverModule getMinigameModule(@NotNull Minigame mgm) {
        return ((GameOverModule) mgm.getModule(MgModules.GAME_OVER.getName()));
    }

    @Override
    public Map<String, Flag<?>> getConfigFlags() {
        Map<String, Flag<?>> map = new HashMap<>();
        map.put(timer.getName(), timer);
        map.put(invincible.getName(), invincible);
        map.put(humiliation.getName(), humiliation);
        return map;
    }

    @Override
    public boolean useSeparateConfig() {
        return false;
    }

    @Override
    public void save(FileConfiguration config) {
    }

    @Override
    public void load(FileConfiguration config) {
    }

    @Override
    public void addEditMenuOptions(Menu previous) {
        Menu menu = new Menu(6, MgMenuLangKey.MENU_GAMEOVER_NAME, previous.getViewer());
        menu.addItem(timer.getMenuItem(Material.CLOCK, MgMenuLangKey.MENU_GAMEOVER_TIME_NAME, 0L, null));

        menu.addItem(invincible.getMenuItem(Material.ENDER_PEARL, MgMenuLangKey.MENU_GAMEOVER_INVINCIBILITY_NAME));
        menu.addItem(humiliation.getMenuItem(Material.DIAMOND_SWORD, MgMenuLangKey.MENU_GAMEOVER_HUMILIATION_NAME,
                MgMenuLangKey.MENU_GAMEOVER_HUMILIATION_DESCRIPTION));
        menu.addItem(interact.getMenuItem(Material.STONE_PRESSURE_PLATE, MgMenuLangKey.MENU_GAMEOVER_INTERACT_NAME));

        menu.addItem(new MenuItemBack(previous), menu.getSize() - 9);

        previous.addItem(new MenuItemPage(Material.OAK_DOOR, MgMenuLangKey.MENU_GAMEOVER_NAME, menu));
    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        return false;
    }

    public void startEndGameTimer() {
        MinigameMessageManager.sendMinigameMessage(getMinigame(), MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_GAMEOVERQUIT,
                Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(timer.getFlag()))));
        getMinigame().setState(MinigameState.ENDED);

        List<MinigamePlayer> allPlys = new ArrayList<>(winners.size() + losers.size());
        allPlys.addAll(losers);
        allPlys.addAll(winners);

        for (MinigamePlayer p : allPlys) {
            if (!isInteractAllowed()) {
                p.setCanInteract(false);
            }
            if (isHumiliationMode() && losers.contains(p)) {
                p.getPlayer().getInventory().clear();
                p.getPlayer().getInventory().setHelmet(null);
                p.getPlayer().getInventory().setChestplate(null);
                p.getPlayer().getInventory().setLeggings(null);
                p.getPlayer().getInventory().setBoots(null);

                for (PotionEffect potion : p.getPlayer().getActivePotionEffects()) {
                    p.getPlayer().removePotionEffect(potion.getType());
                }
            }
            if (isInvincible()) {
                p.setInvincible(true);
            }
        }

        if (timer.getFlag() > 0) {
            if (task != -1) {
                stopEndGameTimer();
            }

            task = Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.getPlugin(), () -> {
                for (MinigamePlayer loser : new ArrayList<>(losers)) {
                    if (loser.isInMinigame()) {
                        Minigames.getPlugin().getPlayerManager().quitMinigame(loser, true);
                    }
                }
                for (MinigamePlayer winner : new ArrayList<>(winners)) {
                    if (winner.isInMinigame()) {
                        Minigames.getPlugin().getPlayerManager().quitMinigame(winner, true);
                    }
                }

                clearLosers();
                clearWinners();
            }, timer.getFlag() * 20);
        }
    }

    public void stopEndGameTimer() {
        if (task != -1) {
            Bukkit.getScheduler().cancelTask(task);
        }
    }

    public void clearWinners() {
        winners.clear();
    }

    public List<MinigamePlayer> getWinners() {
        return winners;
    }

    public void setWinners(List<MinigamePlayer> winners) {
        this.winners.addAll(winners);
    }

    public void clearLosers() {
        losers.clear();
    }

    public List<MinigamePlayer> getLosers() {
        return losers;
    }

    public void setLosers(List<MinigamePlayer> losers) {
        this.losers.addAll(losers);
    }

    public long getTimer() {
        return timer.getFlag();
    }

    public void setTimer(long amount) {
        timer.setFlag(amount);
    }

    public boolean isInvincible() {
        return invincible.getFlag();
    }

    public void setInvincible(boolean bool) {
        invincible.setFlag(bool);
    }

    public boolean isHumiliationMode() {
        return humiliation.getFlag();
    }

    public void setHumiliationMode(boolean bool) {
        humiliation.setFlag(bool);
    }

    public boolean isInteractAllowed() {
        return interact.getFlag();
    }

    public void setInteractAllowed(boolean bool) {
        interact.setFlag(bool);
    }

}
