package com.cham.Module.Render;

import com.cham.Cosmicpings;
import com.cham.CosmicpingsClient;
import com.cham.Module.Keybind;
import com.cham.Module.Mod;
import com.cham.Module.Render.HudUtil.PingHandler;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrucePing extends Mod {

    public static TrucePing INSTANCE = new TrucePing();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Cache<UUID, Vec3d> pingPos = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();
    private final MinecraftClient client = MinecraftClient.getInstance();

    public TrucePing() {
        super("Truce Ping", Keybind.tping, false);
    }

    @Override
    public void onInfo() {
        if(client.player == null || client.world == null) return;
        if(CosmicpingsClient.getINSTANCE().isPrisons()) {
            client.player.sendMessage(Text.literal(Formatting.RED + "Truce pings are not enabled on Prisons!"), false);
            return;
        }
        Vec3d vec3d = pos();
        if(vec3d == null) return;
        if(pingPos.getIfPresent(client.player.getUuid()) != null) {
            vec3d = pingPos.asMap().get(client.player.getUuid());;
        }
        int x = (int) vec3d.x;
        int y = (int) vec3d.y;
        int z = (int) vec3d.z;
        int health = (int) client.player.getHealth();
        int maxHealth = (int) client.player.getMaxHealth();
        final String worldName = PingHandler.worldName(client.player);
        if(worldName == null) {
            client.player.sendMessage(Text.literal("This world is unavailable for pings!"), false);
            return;
        }
        if (!CosmicpingsClient.getINSTANCE().isCooldown()) {
            client.player.networkHandler.sendChatCommand("c t");
            scheduler.schedule(() -> {
                client.execute(() -> {
                    client.player.networkHandler.sendChatMessage("[!] " + client.player.getName().getLiteralString() + " has pinged at " + x + "x " + y + "y " + z + "z " + worldName + " | HP: " + health + "/" + maxHealth + " | Facing: " + client.player.getMovementDirection().asString().toUpperCase());
                });
            }, 20, TimeUnit.MILLISECONDS);
            CosmicpingsClient.getINSTANCE().setCooldown(true);
        }
    }
}
