package io.github.johnnyscriptt.simpleautoclicker.client;

import io.github.johnnyscriptt.simpleautoclicker.client.config.ConfigIo;
import io.github.johnnyscriptt.simpleautoclicker.client.mixin.MinecraftInvoker;
import io.github.johnnyscriptt.simpleautoclicker.core.ClickerTimer;
import io.github.johnnyscriptt.simpleautoclicker.core.config.AutoclickerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** Owns the two per-button timers and fires synthetic clicks from the client tick. */
public final class AutoclickController {
    private static final AutoclickController INSTANCE = new AutoclickController();

    private final ClickerTimer left = new ClickerTimer();
    private final ClickerTimer right = new ClickerTimer();

    private AutoclickController() {
    }

    public static AutoclickController get() {
        return INSTANCE;
    }

    public void toggleLeft(Minecraft mc) {
        boolean on = left.toggle();
        mc.player.sendOverlayMessage(Component.translatable(
                on ? "simpleautoclicker.toggle.left_on" : "simpleautoclicker.toggle.left_off"));
    }

    public void toggleRight(Minecraft mc) {
        boolean on = right.toggle();
        mc.player.sendOverlayMessage(Component.translatable(
                on ? "simpleautoclicker.toggle.right_on" : "simpleautoclicker.toggle.right_off"));
    }

    public void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {
            // Leaving the world switches both off; toggles are session-only.
            left.disable();
            right.disable();
            return;
        }
        if (mc.gui.screen() != null) {
            // Paused while any GUI is open — never click into a screen.
            return;
        }
        AutoclickerConfig cfg = ConfigIo.get();
        MinecraftInvoker invoker = (MinecraftInvoker) mc;
        if (left.tick(cfg.leftIntervalTicks())) {
            invoker.simpleautoclicker$startAttack();
        }
        if (right.tick(cfg.rightIntervalTicks())) {
            invoker.simpleautoclicker$startUseItem();
        }
    }
}
