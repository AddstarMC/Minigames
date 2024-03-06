package au.com.mineauz.minigames.helpers;

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
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TestHelper {
    public static Minigame createMinigame(Minigames plugin, WorldMock world, MinigameType type, GameMechanics.MECHANIC_NAME mechanic) {
        Location start = new Location(world, 0, 21, 0);
        Minigame game = new Minigame("TestGame", MinigameType.MULTIPLAYER, start);
        game.setType(type);
        game.setMechanic(mechanic.toString());
        game.setDeathDrops(true);
        Location quit = new Location(world, 0, 20, 0);
        game.setQuitLocation(quit);
        Location lobby = new Location(world, 0, 5., 0);
        game.setLobbyLocation(lobby);
        Location end = new Location(world, 0, 25, 0);
        game.setEndLocation(end);
        game.setEnabled(true);
        game.setStartWaitTime(5);
        game.setTimer(5);
        game.setMaxScore(3);
        game.setMaxPlayers(2);
        plugin.getMinigameManager().addMinigame(game);
        return game;
    }

    public static BlockMock createSignBlock(Map<Integer, String> lines, WorldMock world) {
        MockSign sign = new MockSign(Material.CRIMSON_SIGN, true);
        for (Map.Entry<Integer, String> e : lines.entrySet()) {
            sign.setLine(e.getKey(), e.getValue());
        }
        BlockData bData = new BlockData() { //there is probably something in MockBukkit 4 this
            @Override
            public @NotNull Material getMaterial() {
                return Material.CRIMSON_SIGN;
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
            public @NotNull BlockData merge(BlockData blockData) {
                return this;
            }

            @Override
            public boolean matches(BlockData blockData) {
                return true;
            }

            @Override
            public @NotNull BlockData clone() {
                return this;
            }

            @Override
            public @NotNull SoundGroup getSoundGroup() {
                return null;
            }

            @Override
            public int getLightEmission() {
                return 0;
            }

            @Override
            public boolean isOccluding() {
                return false;
            }

            @Override
            public boolean requiresCorrectToolForDrops() {
                return false;
            }


            @Override
            public boolean isPreferredTool(@NotNull ItemStack tool) {
                return false;
            }

            @Override
            public @NotNull PistonMoveReaction getPistonMoveReaction() {
                return PistonMoveReaction.BREAK;
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
            public @NotNull Material getPlacementMaterial() {
                return null;
            }

            @Override
            public void rotate(@NotNull StructureRotation structureRotation) {

            }

            @Override
            public void mirror(@NotNull Mirror mirror) {

            }

            @Override
            public boolean isRandomlyTicked() {
                return false;
            }
        };
        return new SignBlockMock(Material.CRIMSON_SIGN, new Location(world, 10, 40, 10), sign, bData);
    }
}
