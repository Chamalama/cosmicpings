package com.cham.screen;

import com.cham.CosmicpingsClient;
import com.cham.config.PingConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class PingConfigScreen extends Screen {

    public static PingConfigScreen INSTANCE = new PingConfigScreen();

    private final PingConfig pingConfig = CosmicpingsClient.getINSTANCE().getPingConfig();

    private final Map<Integer, ButtonWidget> buttonWidgets = new HashMap<>();

    public PingConfigScreen() {
        super(Text.literal("PingConfigScreen"));
    }

    @Override
    protected void init() {
        final Window window = MinecraftClient.getInstance().getWindow();
        final int width = 150;
        final int height = 20;
        final ButtonWidget allyPingWidget = ButtonWidget.builder(Text.literal("Ally Pings: " + getEnabledText(pingConfig.isAllyPingEnabled())), action -> {
            pingConfig.setAllyPingEnabled(!pingConfig.isAllyPingEnabled());
            getButtonWidget(1).setMessage(Text.literal("Ally Pings: " + getEnabledText(pingConfig.isAllyPingEnabled())));
            update();
        }).dimensions((window.getScaledWidth() - width) / 2, ((window.getScaledHeight() - height) / 2) - 40, width, height).build();
        final ButtonWidget trucePingWidget = ButtonWidget.builder(Text.literal("Truce Pings: " + getEnabledText(pingConfig.isTrucePingEnabled())), action -> {
            pingConfig.setTrucePingEnabled(!pingConfig.isTrucePingEnabled());
            getButtonWidget(2).setMessage(Text.literal("Truce Pings: " + getEnabledText(pingConfig.isTrucePingEnabled())));
            update();
        }).dimensions((window.getScaledWidth() - width) / 2, ((window.getScaledHeight() - height) / 2) - 20, width, height).build();
        final ButtonWidget deathPingWidget = ButtonWidget.builder(Text.literal("Death Pings: " + getEnabledText(pingConfig.isDeathMarkersEnabled())), action -> {
            pingConfig.setDeathMarkersEnabled(!pingConfig.isDeathMarkersEnabled());
            getButtonWidget(3).setMessage(Text.literal("Death Pings: " + getEnabledText(pingConfig.isDeathMarkersEnabled())));
            update();
        }).dimensions((window.getScaledWidth() - width) / 2, ((window.getScaledHeight() - height) / 2), width, height).build();
        addWidget(1, allyPingWidget);
        addWidget(2, trucePingWidget);
        addWidget(3, deathPingWidget);
    }

    private void addWidget(int index, ButtonWidget buttonWidget) {
        buttonWidgets.put(index, buttonWidget);
        this.addDrawableChild(buttonWidget);
    }

    private void update() {
        MinecraftClient.getInstance().setScreen(this);
        CosmicpingsClient.getINSTANCE().getPingConfigStorage().update();
    }

    private ButtonWidget getButtonWidget(int index) {
        return buttonWidgets.get(index);
    }

    private String getEnabledText(boolean enabled) {
        return enabled ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled";
    }

}
