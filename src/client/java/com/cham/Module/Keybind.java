package com.cham.Module;

import com.cham.CosmicpingsClient;
import com.cham.Module.Render.DeathPing;
import com.cham.Module.Render.Ping;
import com.cham.Module.Render.TrucePing;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybind {

    public static KeyBinding ping, tping, deathPing;

    public static void register() {
        ping = KeyBindingHelper.registerKeyBinding(new KeyBinding("Ping",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "Cosmic Pings"));
        CosmicpingsClient.keyMap.put(ping, Ping.INSTANCE);


        tping = KeyBindingHelper.registerKeyBinding(new KeyBinding("Truce Ping",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                "Cosmic Pings"));
        CosmicpingsClient.keyMap.put(tping, TrucePing.INSTANCE);

        deathPing = KeyBindingHelper.registerKeyBinding(new KeyBinding("Death Ping",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "Cosmic Pings"));
        CosmicpingsClient.keyMap.put(deathPing, new DeathPing());

    }


}
