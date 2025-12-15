package com.cham.mixin.client;

import com.cham.CosmicpingsClient;
import com.cham.Module.Keybind;
import com.cham.Module.Render.HudUtil.PingHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mixin(PlayerEntity.class)
public abstract class DeathMixin {

    private boolean cooldown;

    private ScheduledExecutorService delay = new ScheduledThreadPoolExecutor(1);

    private final MinecraftClient client = MinecraftClient.getInstance();

    @Inject(at = @At("HEAD"), method = "tick")
    public void death(CallbackInfo ci) {
        if (client == null || client.player == null || client.player.networkHandler == null) return;
        final LivingEntity dead = (LivingEntity) (Object) this;
        if (!dead.getUuid().equals(client.player.getUuid()) || !CosmicpingsClient.getKeyMap().get(Keybind.deathPing).isEnabled())
            return;
        if (!dead.isDead() || cooldown) return;
        cooldown = true;
        final PlayerEntity p = client.player;
        final String worldName = PingHandler.worldName(client.player);
        if (worldName == null) {
            client.player.sendMessage(Text.literal("This world is unavailable for pings!"), false);
            return;
        }
        final Vec3d pos = p.getPos();
        int x = (int) pos.x;
        int y = (int) pos.y;
        int z = (int) pos.z;
        delay.schedule(() -> {
            client.options.forwardKey.setPressed(true);
        }, 600, TimeUnit.MILLISECONDS);
        delay.schedule(() -> {
            String command = CosmicpingsClient.getINSTANCE().isPrisons() ? "gang chat" : "c a";
            client.player.networkHandler.sendChatCommand(command);
            client.options.forwardKey.setPressed(false);
        }, 900, TimeUnit.MILLISECONDS);
        delay.schedule(() -> {
            client.player.networkHandler.sendChatMessage("[!] " + client.player.getName().getLiteralString() + " has died at " + x + "x " + y + "y " + z + "z " + worldName + " ");
            cooldown = false;
        }, 1000, TimeUnit.MILLISECONDS);
    }
}
