package au.com.mineauz.minigames.helpers;

import java.util.*;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MockSign;
import au.com.mineauz.minigames.objects.SignBlockMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockSupport;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

/**
 * Created for the AddstarMC Project. Created by Narimm on 6/02/2019.
 */
public class TestHelper {
    public static Minigame createMinigame(Minigames plugin, WorldMock world, MinigameType type, GameMechanics.MECHANIC_NAME mechanic) {
        Location start = new Location(world, 0, 21, 0);
        Minigame game = new Minigame("TestGame", MinigameType.MULTIPLAYER, start);
        game.setType(type);
        game.setMechanic(mechanic.toString());
        game.setDeathDrops(true);
        Location quit = new Location(world, 0, 20, 0);
        game.setQuitPosition(quit);
        Location lobby = new Location(world, 0, 5., 0);
        game.setLobbyPosition(lobby);
        Location end = new Location(world, 0, 25, 0);
        game.setEndPosition(end);
        game.setEnabled(true);
        game.setStartWaitTime(5);
        game.setTimer(5);
        game.setMaxScore(3);
        game.setMaxPlayers(2);
        plugin.getMinigameManager().addMinigame(game);
        return game;
    }

    public static BlockMock createSignBlock(Map<Integer, String> lines, WorldMock world) {
        MaterialData data = new MaterialData(Material.OAK_SIGN, (byte) 0);
        MockSign sign = new MockSign(data, true);
        for (Map.Entry<Integer, String> e : lines.entrySet()) {
            sign.setLine(e.getKey(), e.getValue());
        }
        BlockData bData = new BlockData() {
            @Override
            public Material getMaterial() {
                return Material.OAK_SIGN;
            }

            @Override
            public String getAsString() {
                return null;
            }

            @Override
            public String getAsString(boolean b) {
                return "SIGN";
            }

            @Override
            public BlockData merge(BlockData blockData) {
                return this;
            }

            @Override
            public boolean matches(BlockData blockData) {
                return true;
            }

            @Override
            public BlockData clone() {
                return this;
            }

            @Override
            public @NotNull SoundGroup getSoundGroup() {
                return null;
            }

            @Override
            public boolean isSupported(@NotNull Block block) {
                return false;
            }

            @Override
            public boolean isSupported(@NotNull Location location) {
                return false;
            }

            @Override
            public boolean isFaceSturdy(@NotNull BlockFace blockFace, @NotNull BlockSupport blockSupport) {
                return false;
            }

            @Override
            public boolean isRandomlyTicked() {
                return false;
            }
        };
        return new SignBlockMock(Material.OAK_SIGN, new Location(world, 10, 40, 10), sign, bData);
    }
}
