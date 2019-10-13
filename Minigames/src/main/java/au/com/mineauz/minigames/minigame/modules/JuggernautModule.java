package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.Team;

import java.util.Map;

public class JuggernautModule extends MinigameModule {

    private MinigamePlayer juggernaut = null;

    public JuggernautModule(Minigame mgm) {
        super(mgm);
    }

    public static JuggernautModule getMinigameModule(Minigame minigame) {
        return (JuggernautModule) minigame.getModule("Juggernaut");
    }

    @Override
    public String getName() {
        return "Juggernaut";
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

            juggernaut.sendMessage(MinigameUtils.getLang("player.juggernaut.plyMsg"), MinigameMessageType.WIN);
            Minigames.getPlugin().getMinigameManager().sendMinigameMessage(getMinigame(),
                    MinigameUtils.formStr("player.juggernaut.gameMsg", juggernaut.getDisplayName(getMinigame().usePlayerDisplayNames())), MinigameMessageType.INFO, juggernaut);

            LoadoutModule lm = LoadoutModule.getMinigameModule(getMinigame());
            if (lm.hasLoadout("juggernaut")) {
                player.setLoadout(lm.getLoadout("juggernaut"));
                player.getLoadout().equiptLoadout(player);
            }
        }
    }

}
