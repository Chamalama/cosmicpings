package com.cham.Module.Render;

import com.cham.CosmicpingsClient;
import com.cham.Module.Keybind;
import com.cham.Module.Mod;
import com.cham.Module.Render.HudUtil.PingHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrucePing extends Mod {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public TrucePing() {
        super("Truce Ping", Keybind.tping, false);
    }


    @Override
    public void onInfo() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {

            Vec3d vec3d = client.player.getPos();
            int x = (int) vec3d.x;
            int y = (int) vec3d.y;
            int z = (int) vec3d.z;
            int health = (int) client.player.getHealth();
            int maxHealth = (int) client.player.getMaxHealth();

            String worldName = PingHandler.worldName(client.player);


            if (!CosmicpingsClient.cd.contains(client.player.getUuid())) {
                client.player.networkHandler.sendCommand("c t");
                scheduler.schedule(() -> {
                    client.execute(() -> {
                        client.player.networkHandler.sendChatMessage("[!] " + client.player.getName().getLiteralString() + " has pinged at " + x + "x " + y + "y " + z + "z " + worldName + " | HP: " + health + "/" + maxHealth + " | Facing: " + client.player.getMovementDirection().asString().toUpperCase());
                    });
                }, 20, TimeUnit.MILLISECONDS);
                CosmicpingsClient.cd.add(client.player.getUuid());
            }
        }
    }
}
