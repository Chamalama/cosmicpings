package com.cham.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PingConfig {

    private boolean allyPingEnabled = true;
    private boolean trucePingEnabled = true;
    private boolean deathMarkersEnabled = true;
    private double pingVolume = 0.9;

    public PingConfig() {

    }

}
