package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JuggernautModule extends MinigameModule {

    private MinigamePlayer juggernaut = null;

    public JuggernautModule(@NotNull Minigame mgm, @NotNull String name) {
        super(mgm, name);
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
        return null;
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
    public void addEditMenuOptions(Menu menu) {
    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        return false;
    }

    public MinigamePlayer getJuggernaut() {
        return juggernaut;
    }

    public void setJuggernaut(MinigamePlayer player) {
        if (juggernaut != null) {
            Team team = juggernaut.getMinigame().getScoreboardManager().getTeam("juggernaut");
            juggernaut.setLoadout(null);
            team.removeEntry(team.getColor() + juggernaut.getPlayer().getDisplayName());
        }
        juggernaut = player;

        if (juggernaut != null) {
            Team team = player.getMinigame().getScoreboardManager().getTeam("juggernaut");
            team.addEntry(team.getColor() + player.getPlayer().getDisplayName());

            MinigameMessageManager.sendMgMessage(juggernaut, MinigameMessageType.SUCCESS, MinigameLangKey.PLAYER_JUGGERNAUT_PLAYERMSG);
            MinigameMessageManager.sendMinigameMessage(getMinigame(), MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_JUGGERNAUT_GAMEMSG,
                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), juggernaut.getDisplayName(getMinigame().usePlayerDisplayNames()))
            ), MinigameMessageType.INFO, juggernaut);

            LoadoutModule lm = LoadoutModule.getMinigameModule(getMinigame());
            if (lm.hasLoadout("juggernaut")) {
                player.setLoadout(lm.getLoadout("juggernaut"));
                player.getLoadout().equipLoadout(player);
            }
        }
    }

}
