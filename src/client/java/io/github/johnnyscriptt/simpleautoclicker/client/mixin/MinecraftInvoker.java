package io.github.johnnyscriptt.simpleautoclicker.client.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Exposes the private vanilla click entry points so synthetic clicks route through hit-result and cooldown logic. */
@Mixin(Minecraft.class)
public interface MinecraftInvoker {
    @Invoker("startAttack")
    boolean simpleautoclicker$startAttack();

    @Invoker("startUseItem")
    void simpleautoclicker$startUseItem();
}
