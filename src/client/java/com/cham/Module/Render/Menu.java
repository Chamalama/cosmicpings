package com.cham.Module.Render;

import com.cham.Module.Keybind;
import com.cham.Module.Mod;
import com.cham.screen.PingConfigScreen;
import net.minecraft.client.MinecraftClient;

public class Menu extends Mod {

    public static Menu INSTANCE = new Menu();

    public Menu() {
        super("Menu", Keybind.menu, false);
    }

    @Override
    public void onInfo() {
        if (MinecraftClient.getInstance().currentScreen == null) {
            MinecraftClient.getInstance().setScreen(PingConfigScreen.INSTANCE);
        }
    }
}
