package com.cham.mixin.client;

import com.cham.CosmicpingsClient;
import com.cham.Module.Keybind;
import com.cham.Module.Render.HudUtil.PingHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mixin(LivingEntity.class)
public class DeathMixin {

    public boolean sent = false;

    private ScheduledExecutorService delay = new ScheduledThreadPoolExecutor(1);

    @Inject(at = @At("TAIL"), method = "tick")
    public void death(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client != null &&  client.player != null && client.player.networkHandler != null) {
            Entity dead = (LivingEntity)(Object)this;
            if (dead.getUuid().equals(client.player.getUuid()) && !dead.isAlive()) {
                if (CosmicpingsClient.getKeyMap().get(Keybind.deathPing).isEnabled()) {
                    if (!sent) {
                        sent = true;
                        client.player.networkHandler.sendCommand("c a");
                        PlayerEntity p = client.player;

                        String worldName = PingHandler.worldName(client.player);

                        if (worldName == null) {
                            client.player.sendMessage(Text.literal("This world is unavailable for pings!"));
                            return;
                        }

                        Vec3d pos = p.getPos();
                        int x = (int) pos.x;
                        int y = (int) pos.y;
                        int z = (int) pos.z;
                        delay.schedule(() -> {
                            client.player.networkHandler.sendChatMessage("[!] " + client.player.getName().getLiteralString() + " has died at " + x + "x " + y + "y " + z + "z " + worldName + " ");
                        }, 30, TimeUnit.MILLISECONDS);
                        if(dead.isAlive() && sent) {
                            sent = false;
                        }
                    }
                }
            }
        }
    }
}
