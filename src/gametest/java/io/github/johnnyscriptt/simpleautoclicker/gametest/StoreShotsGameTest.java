package io.github.johnnyscriptt.simpleautoclicker.gametest;

import io.github.johnnyscriptt.simpleautoclicker.client.SimpleAutoclickerClient;
import io.github.johnnyscriptt.simpleautoclicker.client.config.ConfigIo;
import io.github.johnnyscriptt.simpleautoclicker.client.ui.AutoclickerSettingsScreen;
import io.github.johnnyscriptt.simpleautoclicker.core.config.AutoclickerConfig;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

/**
 * Real in-game captures for the store pages. Not a pass/fail test: it exists so every store
 * image is an honest screenshot of the mod. Every toggle goes through the mod's own keybinds,
 * so the action-bar messages, held keys and clicks are the real ones. Shots are taken at
 * 960x540, where the GUI is laid out, and enlarged 2x afterwards by
 * {@code release/make_store_images.py}.
 *
 * <p>Output: {@code build/run/clientGameTest/screenshots/store-*.png}.
 */
public final class StoreShotsGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        context.getInput().resizeWindow(960, 540);
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksRender();
            for (String command : new String[] {"time set noon", "weather clear", "gamerule advance_time false",
                    "difficulty normal", "gamemode survival @a"}) {
                world.getServer().runCommand(command);
            }

            BlockPos base = world.getServer().computeOnServer(server -> {
                ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
                BlockPos origin = player.blockPosition();
                ServerLevel level = player.level();
                buildPen(level, origin);
                buildTunnel(level, origin.offset(40, 0, 0));
                buildRange(level, origin.offset(80, 0, 0));
                return origin;
            });
            String zombie = "{NoAI:1b,PersistenceRequired:1b,Silent:1b,Rotation:[180f,0f],Health:500f,"
                    + "attributes:[{id:\"minecraft:max_health\",base:500d}],"
                    + "equipment:{head:{id:\"minecraft:iron_helmet\",count:1}}}";
            for (int[] at : new int[][] {{0, 2}, {-2, 3}, {2, 4}}) {
                world.getServer().runCommand("summon minecraft:zombie " + (base.getX() + at[0] + 0.5) + " "
                        + base.getY() + " " + (base.getZ() + at[1] + 0.5) + " " + zombie);
            }
            for (String command : new String[] {"item replace entity @a hotbar.0 with minecraft:diamond_sword",
                    "item replace entity @a hotbar.1 with minecraft:iron_pickaxe",
                    "item replace entity @a hotbar.2 with minecraft:bow",
                    "item replace entity @a hotbar.3 with minecraft:bread 12",
                    "item replace entity @a hotbar.4 with minecraft:torch 32",
                    "item replace entity @a inventory.0 with minecraft:arrow 64"}) {
                world.getServer().runCommand(command);
            }

            // 1: left autoclick on, swinging at a zombie.
            standAt(context, world, base);
            context.getInput().lookAt(base.offset(0, 1, 2));
            select(context, 0);
            settle(context, world);
            context.getInput().pressKey(SimpleAutoclickerClient.TOGGLE_LEFT_KEY);
            context.waitTicks(14);
            worldShot(context, "store-01-left-autoclick");
            context.getInput().pressKey(SimpleAutoclickerClient.TOGGLE_LEFT_KEY);

            // 2: hold left click, mining an ore with nobody on the mouse.
            BlockPos tunnel = base.offset(40, 0, 0);
            standAt(context, world, tunnel);
            context.getInput().lookAt(tunnel.offset(0, 1, 2));
            select(context, 1);
            settle(context, world);
            context.getInput().pressKey(SimpleAutoclickerClient.HOLD_LEFT_KEY);
            boolean mining = waitUntil(context, client -> client.gameMode.getDestroyStage() >= 6, 40);
            worldShot(context, "store-02-hold-left");
            System.out.println("[store shots] hold left mining reached stage 6: " + mining);
            context.getInput().pressKey(SimpleAutoclickerClient.HOLD_LEFT_KEY);

            // 3: hold right click, keeping a bow drawn.
            BlockPos range = base.offset(80, 0, 0);
            standAt(context, world, range);
            context.getInput().lookAt(range.offset(0, 2, 9));
            select(context, 2);
            settle(context, world);
            context.getInput().pressKey(SimpleAutoclickerClient.HOLD_RIGHT_KEY);
            boolean drawing = waitUntil(context, client -> client.player.getTicksUsingItem() >= 22, 40);
            worldShot(context, "store-03-hold-right");
            System.out.println("[store shots] hold right drawing bow: " + drawing);
            context.getInput().pressKey(SimpleAutoclickerClient.HOLD_RIGHT_KEY);
            context.waitTicks(5);

            // 4: the settings screen, opened with its key.
            context.runOnClient(client -> {
                AutoclickerConfig cfg = ConfigIo.get();
                cfg.leftIntervalMs = 250;
                cfg.rightIntervalMs = 1500;
                cfg.keepRunningUnfocused = true;
            });
            context.getInput().pressKey(SimpleAutoclickerClient.SETTINGS_KEY);
            context.waitForScreen(AutoclickerSettingsScreen.class);
            shot(context, "store-04-settings");
            context.setScreen(() -> null);

            // 5: the mod's keybinds in vanilla Controls, scrolled to its category at the end.
            context.setScreen(() -> new KeyBindsScreen(null, context.computeOnClient(client -> client.options)));
            context.waitTicks(2);
            context.runOnClient(client -> {
                for (GuiEventListener child : client.gui.screen().children()) {
                    if (child instanceof KeyBindsList list) {
                        list.setScrollAmount(list.maxScrollAmount());
                    }
                }
            });
            shot(context, "store-05-keybinds");
            context.setScreen(() -> null);
        }
    }

    /** Screens: let the GUI settle first. */
    private static void shot(ClientGameTestContext context, String name) {
        context.waitTicks(8);
        worldShot(context, name);
    }

    /** In-world: taken straight away, while the action-bar message is still up. */
    private static void worldShot(ClientGameTestContext context, String name) {
        context.runOnClient(client -> {
            client.gui.toastManager().clear();
            client.gui.hud.getChat().clearMessages(false);
        });
        context.waitTick();
        context.takeScreenshot(TestScreenshotOptions.of(name).disableCounterPrefix());
    }

    private static boolean waitUntil(ClientGameTestContext context,
            Predicate<net.minecraft.client.Minecraft> done, int maxTicks) {
        for (int i = 0; i < maxTicks; i++) {
            if (context.computeOnClient(done::test)) {
                return true;
            }
            context.waitTick();
        }
        return false;
    }

    private static void select(ClientGameTestContext context, int slot) {
        context.getInput().pressKey(options -> options.keyHotbarSlots[slot]);
    }

    private static void standAt(ClientGameTestContext context, TestSingleplayerContext world, BlockPos pos) {
        world.getServer().runOnServer(server -> server.getPlayerList().getPlayers().getFirst()
                .teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        context.waitFor(client -> client.player != null && client.player.blockPosition().equals(pos), 100);
    }

    /** Long enough for the held item's name above the hotbar to fade. */
    private static void settle(ClientGameTestContext context, TestSingleplayerContext world) {
        world.getConnection().waitForChunksRender();
        context.waitTicks(60);
    }

    /** A stone-brick back wall with lanterns behind the zombies, fences down the sides. */
    private static void buildPen(ServerLevel level, BlockPos base) {
        for (int x = -7; x <= 7; x++) {
            for (int y = 0; y <= 2; y++) {
                level.setBlockAndUpdate(base.offset(x, y, 6), Blocks.STONE_BRICKS.defaultBlockState());
            }
            if (x % 3 == 0) {
                level.setBlockAndUpdate(base.offset(x, 3, 6), Blocks.LANTERN.defaultBlockState());
            }
        }
        for (int z = 1; z <= 5; z++) {
            level.setBlockAndUpdate(base.offset(-5, 0, z), Blocks.OAK_FENCE.defaultBlockState());
            level.setBlockAndUpdate(base.offset(5, 0, z), Blocks.OAK_FENCE.defaultBlockState());
        }
    }

    /** A short deepslate tunnel, open behind the player, with an ore face ahead. */
    private static void buildTunnel(ServerLevel level, BlockPos base) {
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 4; y++) {
                for (int z = -3; z <= 4; z++) {
                    boolean inside = Math.abs(x) <= 1 && y >= 0 && y <= 2 && z <= 1;
                    level.setBlockAndUpdate(base.offset(x, y, z),
                            inside ? Blocks.AIR.defaultBlockState() : Blocks.DEEPSLATE.defaultBlockState());
                }
            }
        }
        BlockState diamond = Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState();
        level.setBlockAndUpdate(base.offset(0, 1, 2), diamond);
        level.setBlockAndUpdate(base.offset(1, 1, 2), diamond);
        level.setBlockAndUpdate(base.offset(1, 0, 2), Blocks.DEEPSLATE_IRON_ORE.defaultBlockState());
        level.setBlockAndUpdate(base.offset(-1, 2, 2), Blocks.DEEPSLATE_COAL_ORE.defaultBlockState());
        level.setBlockAndUpdate(base.offset(-2, 1, 1), Blocks.DEEPSLATE_IRON_ORE.defaultBlockState());
        level.setBlockAndUpdate(base.offset(-1, 0, 0), Blocks.TORCH.defaultBlockState());
        level.setBlockAndUpdate(base.offset(1, 0, -2), Blocks.TORCH.defaultBlockState());
    }

    /** Three archery targets on hay bales. */
    private static void buildRange(ServerLevel level, BlockPos base) {
        for (int[] at : new int[][] {{0, 9}, {-4, 12}, {4, 11}}) {
            level.setBlockAndUpdate(base.offset(at[0], 0, at[1]), Blocks.HAY_BLOCK.defaultBlockState());
            level.setBlockAndUpdate(base.offset(at[0], 1, at[1]), Blocks.HAY_BLOCK.defaultBlockState());
            level.setBlockAndUpdate(base.offset(at[0], 2, at[1]), Blocks.TARGET.defaultBlockState());
        }
    }
}
