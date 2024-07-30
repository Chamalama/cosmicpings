package com.cham.Module.Render;

import com.cham.Module.Keybind;
import com.cham.Module.Mod;

public class DeathPing extends Mod {

    public DeathPing() {
        super("Death Pings", Keybind.deathPing, true);
        this.enabled = true;
    }
}
