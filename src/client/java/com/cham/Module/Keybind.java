package com.cham.Module;

import com.cham.CosmicpingsClient;
import com.cham.Module.Render.Ping;
import com.cham.Module.Render.TrucePing;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybind {

    public static KeyBinding ping, tping;

    public static void register() {
        ping = KeyBindingHelper.registerKeyBinding(new KeyBinding("Ping",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "CosmicPings"));
        CosmicpingsClient.keyMap.put(ping, new Ping());


        tping = KeyBindingHelper.registerKeyBinding(new KeyBinding("Truce Ping",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                "CosmicPings"));
        CosmicpingsClient.keyMap.put(tping, new TrucePing());

    }


}
