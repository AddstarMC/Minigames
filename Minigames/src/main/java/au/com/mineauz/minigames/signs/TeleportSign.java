package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.langkeys.MgSignLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeleportSign extends AMinigameSign {
    private final static Pattern coordPattern = Pattern.compile("(?<x>[+-]?[0-9]+),(?<y>[+-]?[0-9]+),(?<z>[+-]?[0-9]+)");
    private final static Pattern anglePattern = Pattern.compile("(?<yaw>-?[0-9]+),(?<pitch>-?[0-9]+)");

    @Override
    public @NotNull Component getName() {
        return MinigameMessageManager.getMgMessage(MgSignLangKey.TYPE_TELEPORT);
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.teleport";
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.teleport";
    }

    @Override
    public boolean signCreate(@NotNull SignChangeEvent event) {
        event.line(1, getName());
        if (event.getLine(2).isEmpty()) {
            return false;
        } else {
            return coordPattern.matcher(PlainTextComponentSerializer.plainText().serialize(event.line(2))).matches();
        }
    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        if (!sign.getSide(Side.FRONT).getLine(2).isEmpty()) {
            Matcher coordMatcher = coordPattern.matcher(PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(2)));
            if (coordMatcher.matches()) {
                double x = Double.parseDouble(coordMatcher.group("x"));
                double y = Double.parseDouble(coordMatcher.group("y"));
                double z = Double.parseDouble(coordMatcher.group("z"));

                if (!sign.getSide(Side.FRONT).getLine(3).isEmpty()) {
                    Matcher angleMatcher = anglePattern.matcher(PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(3)));
                    if (angleMatcher.matches()) {
                        float yaw = Float.parseFloat(angleMatcher.group("yaw"));
                        float pitch = Float.parseFloat(angleMatcher.group("pitch"));

                        mgPlayer.teleport(new Location(mgPlayer.getPlayer().getWorld(), x + 0.5, y, z + 0.5, yaw, pitch));
                        return true;
                    }
                }
                mgPlayer.teleport(new Location(mgPlayer.getPlayer().getWorld(), x + 0.5, y, z + 0.5));
                return true;
            }
        }
        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_TELEPORT_INVALID);
        return false;
    }

    @Override
    public void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer) {

    }
}
