package io.github.johnnyscriptt.simpleautoclicker.client;

import io.github.johnnyscriptt.simpleautoclicker.client.ui.AutoclickerSettingsScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAutoclickerClient implements ClientModInitializer {
    public static final String MOD_ID = "simpleautoclicker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));

    public static final KeyMapping TOGGLE_LEFT_KEY =
            new KeyMapping("key.simpleautoclicker.toggle_left", InputConstants.KEY_COMMA, CATEGORY);
    public static final KeyMapping TOGGLE_RIGHT_KEY =
            new KeyMapping("key.simpleautoclicker.toggle_right", InputConstants.KEY_PERIOD, CATEGORY);
    public static final KeyMapping SETTINGS_KEY =
            new KeyMapping("key.simpleautoclicker.settings", InputConstants.KEY_SEMICOLON, CATEGORY);

    @Override
    public void onInitializeClient() {
        KeyMappingHelper.registerKeyMapping(TOGGLE_LEFT_KEY);
        KeyMappingHelper.registerKeyMapping(TOGGLE_RIGHT_KEY);
        KeyMappingHelper.registerKeyMapping(SETTINGS_KEY);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            AutoclickController controller = AutoclickController.get();
            boolean inWorld = mc.player != null && mc.level != null;
            while (TOGGLE_LEFT_KEY.consumeClick()) {
                if (inWorld) {
                    controller.toggleLeft(mc);
                }
            }
            while (TOGGLE_RIGHT_KEY.consumeClick()) {
                if (inWorld) {
                    controller.toggleRight(mc);
                }
            }
            while (SETTINGS_KEY.consumeClick()) {
                if (inWorld && mc.gui.screen() == null) {
                    mc.setScreenAndShow(new AutoclickerSettingsScreen());
                }
            }
            controller.tick(mc);
        });

        LOGGER.info("Simple Autoclicker loaded.");
    }
}
