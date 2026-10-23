package io.github.johnnyscriptt.simpleautoclicker.client.ui;

import io.github.johnnyscriptt.simpleautoclicker.client.config.ConfigIo;
import io.github.johnnyscriptt.simpleautoclicker.core.IntervalInput;
import io.github.johnnyscriptt.simpleautoclicker.core.config.AutoclickerConfig;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * One row per mouse button: an interval slider plus seconds/milliseconds boxes
 * that stay in sync with it. Box text applies live while typing and normalizes
 * on focus loss — typing 1.5 into the seconds box becomes Sec: 1 / Ms: 500.
 * Saved on Done/close.
 */
public final class AutoclickerSettingsScreen extends net.minecraft.client.gui.screens.Screen {
    private static final int ROW_W = 300;
    private static final int SLIDER_W = 150;
    private static final int SEC_LABEL_W = 26;
    private static final int SEC_BOX_W = 34;
    private static final int MS_LABEL_W = 22;
    private static final int MS_BOX_W = 44;
    private static final int ROW_H = 20;
    private static final int GAP = 4;

    private IntervalRow leftRow;
    private IntervalRow rightRow;

    public AutoclickerSettingsScreen() {
        super(Component.translatable("simpleautoclicker.settings.title"));
    }

    @Override
    protected void init() {
        AutoclickerConfig cfg = ConfigIo.get();
        int x = (this.width - ROW_W) / 2;
        int y = 60;

        leftRow = new IntervalRow(x, y, "simpleautoclicker.settings.left_interval",
                () -> cfg.leftIntervalMs, v -> cfg.leftIntervalMs = v);
        y += ROW_H + GAP;
        rightRow = new IntervalRow(x, y, "simpleautoclicker.settings.right_interval",
                () -> cfg.rightIntervalMs, v -> cfg.rightIntervalMs = v);
        y += ROW_H + GAP;

        addRenderableWidget(CycleButton.onOffBuilder(cfg.keepRunningUnfocused)
                .create(x, y, ROW_W, ROW_H,
                        Component.translatable("simpleautoclicker.settings.keep_running_unfocused"),
                        (btn, on) -> cfg.keepRunningUnfocused = on));
        y += ROW_H + GAP + 8;

        addRenderableWidget(Button.builder(Component.translatable("simpleautoclicker.settings.done"),
                        btn -> onClose())
                .bounds(x + (ROW_W - 80) / 2, y, 80, ROW_H).build());
    }

    @Override
    public void onClose() {
        leftRow.commitBoxes();
        rightRow.commitBoxes();
        ConfigIo.save();
        minecraft.setScreenAndShow(null);
    }

    /** Slider + seconds box + millis box bound to one config field. */
    private final class IntervalRow {
        private final IntSupplier getter;
        private final IntConsumer setter;
        private final IntervalSlider slider;
        private final EditBox secondsBox;
        private final EditBox msBox;
        private boolean syncing;

        IntervalRow(int x, int y, String sliderLabelKey, IntSupplier getter, IntConsumer setter) {
            this.getter = getter;
            this.setter = setter;

            slider = new IntervalSlider(x, y, sliderLabelKey);
            addRenderableWidget(slider);
            int bx = x + SLIDER_W + GAP;

            addRenderableWidget(new StringWidget(bx, y, SEC_LABEL_W, ROW_H,
                    Component.translatable("simpleautoclicker.settings.seconds"), font));
            bx += SEC_LABEL_W;
            secondsBox = new CommittingEditBox(bx, y,
                    SEC_BOX_W, Component.translatable("simpleautoclicker.settings.seconds"));
            addRenderableWidget(secondsBox);
            bx += SEC_BOX_W + GAP;

            addRenderableWidget(new StringWidget(bx, y, MS_LABEL_W, ROW_H,
                    Component.translatable("simpleautoclicker.settings.millis"), font));
            bx += MS_LABEL_W;
            msBox = new CommittingEditBox(bx, y,
                    MS_BOX_W, Component.translatable("simpleautoclicker.settings.millis"));
            addRenderableWidget(msBox);

            syncFromConfig();
        }

        /** Applies valid box text to the config as typed, without rewriting what's being typed. */
        private void onBoxEdited() {
            if (syncing) {
                return;
            }
            int total = IntervalInput.parseTotalMs(secondsBox.getValue(), msBox.getValue());
            if (total != IntervalInput.INVALID) {
                setter.accept(total);
                slider.syncFromConfig();
            }
        }

        /** Normalizes both boxes (1.5 s -> 1 s / 500 ms) once typing is done; invalid text reverts. */
        void commitBoxes() {
            syncFromConfig();
        }

        private void syncFromConfig() {
            syncing = true;
            int total = getter.getAsInt();
            secondsBox.setValue(String.valueOf(IntervalInput.secondsPart(total)));
            msBox.setValue(String.valueOf(IntervalInput.millisPart(total)));
            slider.syncFromConfig();
            syncing = false;
        }

        /** EditBox that live-applies edits and normalizes when it loses focus. */
        private final class CommittingEditBox extends EditBox {
            CommittingEditBox(int x, int y, int w, Component label) {
                super(AutoclickerSettingsScreen.this.font, x, y, w, ROW_H, label);
                setMaxLength(7);
                setResponder(text -> onBoxEdited());
            }

            @Override
            public void setFocused(boolean focused) {
                boolean hadFocus = isFocused();
                super.setFocused(focused);
                if (hadFocus && !focused) {
                    commitBoxes();
                }
            }
        }

        private final class IntervalSlider extends AbstractSliderButton {
            private final String labelKey;

            IntervalSlider(int x, int y, String labelKey) {
                super(x, y, SLIDER_W, ROW_H, Component.empty(), 0.0);
                this.labelKey = labelKey;
                syncFromConfig();
            }

            private int millis() {
                int min = AutoclickerConfig.MIN_INTERVAL_MS;
                int max = AutoclickerConfig.MAX_INTERVAL_MS;
                int step = AutoclickerConfig.MS_PER_TICK;
                // Snap to whole ticks — the game can't click between them.
                int raw = (int) Math.round(min + (max - min) * this.value);
                return Math.clamp(Math.round(raw / (float) step) * step, min, max);
            }

            void syncFromConfig() {
                int min = AutoclickerConfig.MIN_INTERVAL_MS;
                int max = AutoclickerConfig.MAX_INTERVAL_MS;
                this.value = (getter.getAsInt() - min) / (double) (max - min);
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                setMessage(Component.translatable(labelKey, String.valueOf(getter.getAsInt())));
            }

            @Override
            protected void applyValue() {
                // Written straight into the live config; the controller reads it next tick.
                setter.accept(millis());
                IntervalRow.this.syncFromConfig();
            }
        }
    }
}
