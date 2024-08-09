package com.cham.Module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

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

    public String getName() {
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KeyBinding getKeycode() {
        return this.keycode;
    }

    public boolean isShouldToggle() {
        return this.shouldToggle;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public Vec3d pos() {
        if (this.lastPressed != 0L && System.currentTimeMillis() - this.lastPressed > 100L) {
            var raycasted = MinecraftClient.getInstance().player.raycast(5000.0, MinecraftClient.getInstance().getTickDelta(), false);
            if (raycasted.getType() == HitResult.Type.BLOCK) {
                return raycasted.getPos();
            }
        }

        return MinecraftClient.getInstance().player.getPos();
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

