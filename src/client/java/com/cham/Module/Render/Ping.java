package com.cham.Module.Render;

import com.cham.CosmicpingsClient;
import com.cham.Module.Keybind;
import com.cham.Module.Mod;
import com.cham.Module.Render.HudUtil.PingHandler;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Ping extends Mod {

    public static Ping INSTANCE = new Ping();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private boolean isPressed = false;

    private long defaultTime = System.currentTimeMillis();

    private Cache<UUID, Vec3d> pingPos = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MILLISECONDS).build();

    public Ping() {
        super("Ping", Keybind.ping, false);
    }

    @Override
    public void onInfo() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {

            Vec3d vec3d = pos();

            if(pingPos.asMap().containsKey(client.player.getUuid())) {
                vec3d = pingPos.asMap().get(client.player.getUuid());;
            }

            int x = (int) vec3d.x;
            int y = (int) vec3d.y;
            int z = (int) vec3d.z;
            int health = (int) client.player.getHealth();
            int maxHealth = (int) client.player.getMaxHealth();

            String worldName = PingHandler.worldName(client.player);

            if (worldName == null) {
                client.player.sendMessage(Text.literal("This world is unavailable for pings!"));
                return;
            }

            if (!CosmicpingsClient.cd.contains(client.player.getUuid())) {
                client.player.networkHandler.sendCommand("c a");
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
