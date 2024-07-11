package com.cham.Module;

import net.minecraft.client.option.KeyBinding;

public abstract class Mod {

    protected String name, message;
    protected boolean enabled;
    protected KeyBinding keycode;
    protected boolean shouldToggle, messageSent;

    public Mod(String name, KeyBinding keycode, boolean shouldToggle) {
        this.name = name;
        this.enabled = false;
        this.keycode = keycode;
        this.shouldToggle = shouldToggle;
        this.messageSent = false;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KeyBinding getKeycode() {
        return keycode;
    }

    public boolean isShouldToggle() {
        return shouldToggle;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public String getMessage() {
        return this.getName() + " " + getEnabled(this.enabled);
    }

    public void onUpdate() {

    }

    public void onInfo() {

    }

    public String getEnabled(boolean enable) {
        if(enable) {
            return "enabled";
        }else{
            return "disabled";
        }
    }

    public void debug() {
        System.out.println(this.enabled);
    }

}
