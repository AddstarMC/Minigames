package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardDisplay;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Directional;
import org.bukkit.metadata.FixedMetadataValue;

public class ScoreboardSign implements MinigameSign {
    private Minigames plugin = Minigames.getPlugin();

    @Override
    public String getName() {
        return "Scoreboard";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.scoreboard";
    }

    @Override
    public String getCreatePermissionMessage() {
        return "You do not have permission to create a Minigame scoreboard sign!";
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.scoreboard";
    }

    @Override
    public String getUsePermissionMessage() {
        return "You do not have permission to set up a Minigame scoreboard sign!";
    }

    @Override
    public boolean signCreate(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock().getState();
        if (sign.getType() != Material.OAK_WALL_SIGN) {
            event.getPlayer().sendMessage(ChatColor.RED + "Scoreboards must be placed on a wall!");
            return false;
        }

        // Parse minigame
        Minigame minigame;

        if (plugin.getMinigameManager().hasMinigame(event.getLine(2))) {
            minigame = plugin.getMinigameManager().getMinigame(event.getLine(2));
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "No Minigame found by the name " + event.getLine(2));
            return false;
        }

        // Parse size
        int width;
        int height;

        if (event.getLine(3).isEmpty()) {
            width = ScoreboardDisplay.defaultWidth;
            height = ScoreboardDisplay.defaultHeight;
        } else if (event.getLine(3).matches("[0-9]+x[0-9]+")) {
            String[] parts = event.getLine(3).split("x");
            width = Integer.parseInt(parts[0]);
            height = Integer.parseInt(parts[1]);
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "Invalid size. Requires nothing or (width)x(height) eg. 3x3");
            return false;
        }

        // So we dont have to deal with even size scoreboards
        if (width % 2 == 0) {
            event.getPlayer().sendMessage(ChatColor.RED + "Length must not be an even number!");
            return false;
        }

        BlockFace facing = ((Directional) sign.getData()).getFacing();

        // Add our display
        ScoreboardDisplay display = new ScoreboardDisplay(minigame, width, height, event.getBlock().getLocation(), facing);
        display.placeSigns();

        minigame.getScoreboardData().addDisplay(display);

        // Reformat this sign for the next part
        event.setLine(1, ChatColor.GREEN + "Scoreboard");
        event.setLine(2, minigame.getName(false));

        event.getBlock().setMetadata("Minigame", new FixedMetadataValue(plugin, minigame));
        return true;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        Minigame minigame = plugin.getMinigameManager().getMinigame(sign.getLine(2));
        if (minigame == null) {
            return false;
        }

        ScoreboardDisplay display = minigame.getScoreboardData().getDisplay(sign.getBlock());
        if (display == null) {
            return false;
        }

        display.displayMenu(player);

        return false;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {
        Minigame minigame = (Minigame) sign.getBlock().getMetadata("Minigame").get(0).value();
        if (minigame != null) {
            minigame.getScoreboardData().removeDisplay(sign.getBlock());
        }
    }

}
