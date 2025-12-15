package com.cham.Module;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

@Getter
@Setter
public abstract class Mod {

    protected String name;
    protected String message;
    protected boolean enabled;
    protected KeyBinding keycode;
    protected boolean shouldToggle;
    protected boolean messageSent;
    public long lastPressed;

    public Mod(String name, KeyBinding keycode, boolean shouldToggle) {
        this.name = name;
        this.enabled = false;
        this.keycode = keycode;
        this.shouldToggle = shouldToggle;
        this.messageSent = false;
        this.lastPressed = 0L;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public Vec3d pos() {
        final PlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return null;
        if (this.lastPressed != 0L && System.currentTimeMillis() - this.lastPressed > 100L) {
            var raycasted = player.raycast(5000.0, MinecraftClient.getInstance().getRenderTickCounter().getFixedDeltaTicks(), false);
            if (raycasted.getType() == HitResult.Type.BLOCK) {
                return raycasted.getPos();
            }
        }
        return player.getPos();
    }

    public String getMessage() {
        String var10000 = this.getName();
        return var10000 + " " + this.getEnabled(this.enabled);
    }

    public void onUpdate() {
    }

    public void onInfo() {

    }

    public void onPlace() {

    }

    public String getEnabled(boolean enable) {
        return enable ? "enabled" : "disabled";
    }

    public void debug() {
        System.out.println(this.enabled);
    }
}

