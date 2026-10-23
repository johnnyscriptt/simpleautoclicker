package io.github.johnnyscriptt.simpleautoclicker.client.mixin;

import io.github.johnnyscriptt.simpleautoclicker.client.AutoclickController;
import io.github.johnnyscriptt.simpleautoclicker.client.config.ConfigIo;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Skips vanilla's pause-on-focus-loss while the "keep running when tabbed out"
 * option is on and a clicker or hold is active, so the pause menu never opens.
 * The player's pauseOnLostFocus option is left untouched.
 */
@Mixin(Minecraft.class)
public abstract class PauseIfInactiveMixin {
    @Inject(method = "pauseIfInactive", at = @At("HEAD"), cancellable = true)
    private void simpleautoclicker$keepRunningUnfocused(CallbackInfo ci) {
        if (ConfigIo.get().shouldSuppressFocusPause(AutoclickController.get().isAnyActive())) {
            ci.cancel();
        }
    }
}
