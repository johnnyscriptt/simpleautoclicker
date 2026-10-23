package io.github.johnnyscriptt.simpleautoclicker.client;

import io.github.johnnyscriptt.simpleautoclicker.client.config.ConfigIo;
import io.github.johnnyscriptt.simpleautoclicker.client.mixin.MinecraftInvoker;
import io.github.johnnyscriptt.simpleautoclicker.core.ButtonModes;
import io.github.johnnyscriptt.simpleautoclicker.core.config.AutoclickerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Owns the click/hold state for both buttons and drives them from the client tick:
 * clicks fire synthetic attacks/uses, holds keep the vanilla attack/use key down.
 */
public final class AutoclickController {
    private static final AutoclickController INSTANCE = new AutoclickController();

    private final ButtonModes left = new ButtonModes();
    private final ButtonModes right = new ButtonModes();

    private AutoclickController() {
    }

    public static AutoclickController get() {
        return INSTANCE;
    }

    /** True while either button is clicking or holding. */
    public boolean isAnyActive() {
        return left.isActive() || right.isActive();
    }

    public void toggleLeft(Minecraft mc) {
        boolean wasHolding = left.isHolding();
        boolean on = left.toggleClick();
        if (wasHolding) {
            // Clicking took over from holding.
            mc.options.keyAttack.setDown(false);
        }
        mc.player.sendOverlayMessage(Component.translatable(
                on ? "simpleautoclicker.toggle.left_on" : "simpleautoclicker.toggle.left_off"));
    }

    public void toggleRight(Minecraft mc) {
        boolean wasHolding = right.isHolding();
        boolean on = right.toggleClick();
        if (wasHolding) {
            mc.options.keyUse.setDown(false);
        }
        mc.player.sendOverlayMessage(Component.translatable(
                on ? "simpleautoclicker.toggle.right_on" : "simpleautoclicker.toggle.right_off"));
    }

    public void toggleHoldLeft(Minecraft mc) {
        boolean on = left.toggleHold();
        if (!on) {
            mc.options.keyAttack.setDown(false);
        }
        mc.player.sendOverlayMessage(Component.translatable(
                on ? "simpleautoclicker.toggle.left_hold_on" : "simpleautoclicker.toggle.left_hold_off"));
    }

    public void toggleHoldRight(Minecraft mc) {
        boolean on = right.toggleHold();
        if (!on) {
            mc.options.keyUse.setDown(false);
        }
        mc.player.sendOverlayMessage(Component.translatable(
                on ? "simpleautoclicker.toggle.right_hold_on" : "simpleautoclicker.toggle.right_hold_off"));
    }

    public void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {
            // Leaving the world switches both off; toggles are session-only.
            if (left.isHolding()) {
                mc.options.keyAttack.setDown(false);
            }
            if (right.isHolding()) {
                mc.options.keyUse.setDown(false);
            }
            left.disable();
            right.disable();
            return;
        }
        if (mc.gui.screen() != null) {
            // Paused while any GUI is open — never click into a screen.
            return;
        }
        // Opening/closing a screen resets key state to the physical buttons, so a
        // hold is re-asserted every tick; vanilla's next tick then mines or uses.
        if (left.isHolding()) {
            mc.options.keyAttack.setDown(true);
        }
        if (right.isHolding()) {
            mc.options.keyUse.setDown(true);
        }
        AutoclickerConfig cfg = ConfigIo.get();
        MinecraftInvoker invoker = (MinecraftInvoker) mc;
        if (left.tickClick(cfg.leftIntervalTicks())) {
            invoker.simpleautoclicker$startAttack();
        }
        if (right.tickClick(cfg.rightIntervalTicks())) {
            invoker.simpleautoclicker$startUseItem();
        }
    }
}
